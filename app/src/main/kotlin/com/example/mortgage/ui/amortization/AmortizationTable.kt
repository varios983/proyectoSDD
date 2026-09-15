package com.example.mortgage.ui.amortization

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mortgage.domain.model.AmortizacionPeriodo
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AmortizationTable(periods: List<AmortizacionPeriodo>) {
    val currency = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
    Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            TableCell("Periodo", true)
            TableCell("Cuota", true)
            TableCell("Capital", true)
            TableCell("Intereses", true)
            TableCell("Pendiente", true)
        }
        HorizontalDivider()
        periods.forEach { period ->
            Row(modifier = Modifier.padding(vertical = 7.dp)) {
                TableCell(period.numeroPeriodo.toString())
                TableCell(currency.format(period.cuota))
                TableCell(currency.format(period.capitalAmortizado))
                TableCell(currency.format(period.intereses))
                TableCell(currency.format(period.capitalPendiente))
            }
        }
    }
}

@Composable
private fun TableCell(value: String, header: Boolean = false) {
    Text(
        text = value,
        modifier = Modifier.padding(horizontal = 8.dp),
        style = if (header) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall
    )
}
