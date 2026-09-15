package com.example.mortgage.data.export

import com.example.mortgage.data.export.csv.CsvExporter
import com.example.mortgage.domain.calculation.FrenchAmortizationCalculator
import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.Periodicidad
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class CsvExporterTest {
    @Test
    fun `exports stable headers and every period`() {
        val result = FrenchAmortizationCalculator()(
            Hipoteca(BigDecimal("1200.00"), 1, BigDecimal.ZERO, Periodicidad.MENSUAL)
        )
        val csv = CsvExporter().export(
            ExportDocument("Prueba", Instant.EPOCH, "1200.00", 1, "0", "MENSUAL", result)
        )

        assertContains(csv, "numeroPeriodo,fecha,cuota,capitalAmortizado,intereses,capitalPendiente")
        assertEquals(12, csv.lines().count { it.firstOrNull()?.isDigit() == true })
        assertContains(csv, "0.00")
    }
}
