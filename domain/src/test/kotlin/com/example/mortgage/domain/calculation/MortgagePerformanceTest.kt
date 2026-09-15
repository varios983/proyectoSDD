package com.example.mortgage.domain.calculation

import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.Periodicidad
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class MortgagePerformanceTest {
    @Test
    fun `calculates maximum supported mortgage under 100 milliseconds`() {
        val elapsed = measureTime {
            FrenchAmortizationCalculator()(
                Hipoteca(BigDecimal("10000000.00"), 50, BigDecimal("3.5"), Periodicidad.MENSUAL)
            )
        }

        assertTrue(elapsed < 100.milliseconds, "Calculation took $elapsed")
    }
}
