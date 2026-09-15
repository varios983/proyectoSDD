package com.example.mortgage.data.export.pdf

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.example.mortgage.data.export.ExportDocument
import java.io.OutputStream
import java.time.format.DateTimeFormatter

class PdfExporter {
    fun export(document: ExportDocument, output: OutputStream) {
        val pdf = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 9f }
        val titlePaint = Paint(paint).apply { textSize = 14f; isFakeBoldText = true }
        val pageWidth = 595
        val pageHeight = 842
        val left = 24f
        val rowHeight = 18f
        var pageNumber = 1
        var page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
        var canvas = page.canvas
        var y = 32f

        fun header() {
            canvas.drawText(document.name, left, y, titlePaint)
            y += 22f
            canvas.drawText("Importe: ${document.annualAmount} EUR | Plazo: ${document.termYears} años | Interés: ${document.annualInterest}%", left, y, paint)
            y += 16f
            canvas.drawText("Cuota: ${document.result.cuotaPeriodica} | Intereses: ${document.result.totalIntereses} | Total: ${document.result.totalPagado}", left, y, paint)
            y += 22f
            canvas.drawText("Periodo    Fecha          Cuota       Capital       Intereses     Pendiente", left, y, paint)
            y += rowHeight
        }

        fun nextPage() {
            pdf.finishPage(page)
            pageNumber++
            page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            canvas = page.canvas
            y = 32f
            header()
        }

        header()
        document.result.periodos.forEach { period ->
            if (y > pageHeight - 30f) nextPage()
            val date = period.fecha?.format(DateTimeFormatter.ISO_LOCAL_DATE).orEmpty()
            canvas.drawText(
                "%4d       %-12s %10s %12s %12s %12s".format(
                    period.numeroPeriodo,
                    date,
                    period.cuota.toPlainString(),
                    period.capitalAmortizado.toPlainString(),
                    period.intereses.toPlainString(),
                    period.capitalPendiente.toPlainString()
                ),
                left,
                y,
                paint
            )
            y += rowHeight
        }
        pdf.finishPage(page)
        output.use { pdf.writeTo(it) }
        pdf.close()
    }
}
