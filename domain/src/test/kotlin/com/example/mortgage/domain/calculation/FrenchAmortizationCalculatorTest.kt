package com.example.mortgage.domain.calculation

import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.Periodicidad
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FrenchAmortizationCalculatorTest {
    private val calculator = FrenchAmortizationCalculator()

    @Test
    fun `calculates monthly reference mortgage and closes balance`() {
        val result = calculator(
            Hipoteca(BigDecimal("200000.00"), 30, BigDecimal("3.5"), Periodicidad.MENSUAL)
        )

        assertEquals(360, result.periodos.size)
        assertEquals(BigDecimal("898.09"), result.cuotaPeriodica)
        assertEquals(BigDecimal("123312.40"), result.totalIntereses)
        assertEquals(BigDecimal("0.00"), result.periodos.last().capitalPendiente)
        assertEquals(BigDecimal("200000.00"), result.periodos.sumOf { it.capitalAmortizado })
    }

    @Test
    fun `zero interest splits principal across periods`() {
        val result = calculator(
            Hipoteca(BigDecimal("1200.00"), 1, BigDecimal.ZERO, Periodicidad.MENSUAL)
        )

        assertEquals(BigDecimal("100.00"), result.cuotaPeriodica)
        assertEquals(BigDecimal.ZERO.setScale(2), result.totalIntereses)
        assertEquals(BigDecimal("0.00"), result.periodos.last().capitalPendiente)
    }

    @Test
    fun `includes dates according to monthly period`() {
        val result = calculator(
            Hipoteca(
                BigDecimal("1000.00"),
                1,
                BigDecimal.ZERO,
                Periodicidad.MENSUAL,
                LocalDate.of(2026, 1, 1)
            )
        )

        assertEquals(LocalDate.of(2026, 2, 1), result.periodos.first().fecha)
        assertEquals(LocalDate.of(2027, 1, 1), result.periodos.last().fecha)
    }

    @Test
    fun `supports quincenal and weekly schedules`() {
        val mortgage = Hipoteca(BigDecimal("5200.00"), 1, BigDecimal.ZERO, Periodicidad.QUINCENAL)
        val fortnightly = calculator(mortgage)
        val weekly = calculator(mortgage.copy(periodicidad = Periodicidad.SEMANAL))

        assertEquals(24, fortnightly.periodos.size)
        assertEquals(52, weekly.periodos.size)
        assertTrue(weekly.periodos.all { it.capitalPendiente >= BigDecimal.ZERO })
    }
}
