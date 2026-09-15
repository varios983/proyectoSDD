package com.example.mortgage.data.mapper

import com.example.mortgage.data.local.room.AmortizationPeriodEntity
import com.example.mortgage.data.local.room.SimulationEntity
import com.example.mortgage.domain.model.AmortizacionPeriodo
import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.Periodicidad
import com.example.mortgage.domain.model.ResultadoHipoteca
import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

fun Long.toMoney(): BigDecimal = BigDecimal(this).movePointLeft(2)
fun BigDecimal.toCents(): Long = setScale(2).movePointRight(2).longValueExact()

fun SimulationEntity.toDomain(periods: List<AmortizationPeriodEntity>): Simulacion = Simulacion(
    id = SimulationId(id),
    nombre = name,
    hipoteca = Hipoteca(
        importe = amountCents.toMoney(),
        plazoAnios = termYears,
        interesAnual = annualInterest.toBigDecimal(),
        periodicidad = Periodicidad.valueOf(frequency),
        fechaInicio = startDate?.let(LocalDate::parse)
    ),
    fechaCreacion = Instant.ofEpochMilli(createdAtEpochMillis),
    resultado = ResultadoHipoteca(
        cuotaPeriodica = paymentCents.toMoney(),
        totalIntereses = totalInterestCents.toMoney(),
        totalPagado = totalPaidCents.toMoney(),
        periodos = periods.map { it.toDomain() }
    )
)

fun AmortizationPeriodEntity.toDomain(): AmortizacionPeriodo = AmortizacionPeriodo(
    numeroPeriodo = periodNumber,
    fecha = date?.let(LocalDate::parse),
    cuota = paymentCents.toMoney(),
    capitalAmortizado = principalCents.toMoney(),
    intereses = interestCents.toMoney(),
    capitalPendiente = balanceCents.toMoney()
)

fun Simulacion.toEntity(): SimulationEntity = SimulationEntity(
    id = id.value,
    name = nombre,
    amountCents = hipoteca.importe.toCents(),
    termYears = hipoteca.plazoAnios,
    annualInterest = hipoteca.interesAnual.toPlainString(),
    frequency = hipoteca.periodicidad.name,
    startDate = hipoteca.fechaInicio?.toString(),
    createdAtEpochMillis = fechaCreacion.toEpochMilli(),
    paymentCents = resultado.cuotaPeriodica.toCents(),
    totalInterestCents = resultado.totalIntereses.toCents(),
    totalPaidCents = resultado.totalPagado.toCents()
)

fun Simulacion.toPeriodEntities(): List<AmortizationPeriodEntity> = resultado.periodos.map {
    AmortizationPeriodEntity(
        simulationId = id.value,
        periodNumber = it.numeroPeriodo,
        date = it.fecha?.toString(),
        paymentCents = it.cuota.toCents(),
        principalCents = it.capitalAmortizado.toCents(),
        interestCents = it.intereses.toCents(),
        balanceCents = it.capitalPendiente.toCents()
    )
}
