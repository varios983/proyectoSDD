package com.example.mortgage.domain.calculation

import com.example.mortgage.domain.model.AmortizacionPeriodo
import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.ResultadoHipoteca
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.time.LocalDate

class FrenchAmortizationCalculator {
    private val mathContext = MathContext(34, RoundingMode.HALF_UP)
    private val moneyScale = 2
    private val moneyRounding = RoundingMode.HALF_UP

    operator fun invoke(hipoteca: Hipoteca): ResultadoHipoteca {
        val frequency = hipoteca.periodicidad.periodsPerYear
        val totalPeriods = hipoteca.plazoAnios * frequency
        val periodicRate = hipoteca.interesAnual
            .divide(BigDecimal("100"), mathContext)
            .divide(BigDecimal(frequency), mathContext)
        val exactPayment = if (periodicRate.compareTo(BigDecimal.ZERO) == 0) {
            hipoteca.importe.divide(BigDecimal(totalPeriods), mathContext)
        } else {
            val growth = BigDecimal.ONE.add(periodicRate, mathContext).pow(totalPeriods, mathContext)
            hipoteca.importe.multiply(periodicRate, mathContext)
                .multiply(growth, mathContext)
                .divide(growth.subtract(BigDecimal.ONE, mathContext), mathContext)
        }
        val regularPayment = exactPayment.setScale(moneyScale, moneyRounding)
        var balance = hipoteca.importe.setScale(moneyScale, moneyRounding)
        val periods = buildList(totalPeriods) { periodNumber ->
            val interest = balance.multiply(periodicRate, mathContext)
                .setScale(moneyScale, moneyRounding)
            val principal = if (periodNumber == totalPeriods) {
                balance
            } else {
                regularPayment.subtract(interest).setScale(moneyScale, moneyRounding)
            }
            val payment = if (periodNumber == totalPeriods) {
                principal.add(interest).setScale(moneyScale, moneyRounding)
            } else {
                regularPayment
            }
            balance = balance.subtract(principal).setScale(moneyScale, moneyRounding)
            add(
                AmortizacionPeriodo(
                    numeroPeriodo = periodNumber + 1,
                    fecha = periodDate(hipoteca.fechaInicio, hipoteca.periodicidad.periodsPerYear, periodNumber + 1),
                    cuota = payment,
                    capitalAmortizado = principal,
                    intereses = interest,
                    capitalPendiente = balance.max(BigDecimal.ZERO).setScale(moneyScale, moneyRounding)
                )
            )
        }
        val totalInterest = periods.fold(BigDecimal.ZERO) { total, period -> total + period.intereses }
            .setScale(moneyScale, moneyRounding)
        val totalPaid = hipoteca.importe.setScale(moneyScale, moneyRounding)
            .add(totalInterest).setScale(moneyScale, moneyRounding)
        return ResultadoHipoteca(
            cuotaPeriodica = regularPayment,
            totalIntereses = totalInterest,
            totalPagado = totalPaid,
            periodos = periods
        )
    }

    private fun periodDate(start: LocalDate?, frequency: Int, period: Int): LocalDate? {
        if (start == null) return null
        return when (frequency) {
            12 -> start.plusMonths(period.toLong())
            24 -> start.plusDays(period * 14L)
            else -> start.plusDays(period * 7L)
        }
    }
}

private fun <T> buildList(size: Int, builder: MutableList<T>.(Int) -> Unit): List<T> {
    val result = ArrayList<T>(size)
    repeat(size) { result.builder(it) }
    return result
}
