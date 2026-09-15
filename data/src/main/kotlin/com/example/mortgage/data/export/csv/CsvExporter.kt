package com.example.mortgage.data.export.csv

import com.example.mortgage.data.export.ExportDocument
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

class CsvExporter {
    fun export(document: ExportDocument): String = buildString {
        appendLine("nombre,fechaCreacion,importe,plazoAnios,interesAnual,periodicidad")
        appendLine(listOf(document.name, document.createdAt.toString(), document.annualAmount, document.termYears, document.annualInterest, document.frequency).joinToString(",", transform = ::escape))
        appendLine("cuotaPeriodica,totalIntereses,totalPagado")
        appendLine(listOf(document.result.cuotaPeriodica, document.result.totalIntereses, document.result.totalPagado).joinToString(",", transform = { it.toPlainString() }))
        appendLine()
        appendLine("numeroPeriodo,fecha,cuota,capitalAmortizado,intereses,capitalPendiente")
        document.result.periodos.forEach { period ->
            appendLine(listOf(
                period.numeroPeriodo.toString(),
                period.fecha?.format(DateTimeFormatter.ISO_LOCAL_DATE).orEmpty(),
                period.cuota.toPlainString(),
                period.capitalAmortizado.toPlainString(),
                period.intereses.toPlainString(),
                period.capitalPendiente.toPlainString()
            ).joinToString(",", transform = ::escape))
        }
    }

    private fun escape(value: Any): String {
        val text = when (value) {
            is BigDecimal -> value.toPlainString()
            else -> value.toString()
        }
        return if (text.any { it == ',' || it == '"' || it == '\n' }) {
            "\"${text.replace("\"", "\"\"")}\""
        } else {
            text
        }
    }
}
