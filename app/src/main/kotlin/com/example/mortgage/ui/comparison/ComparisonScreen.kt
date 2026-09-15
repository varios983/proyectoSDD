package com.example.mortgage.ui.comparison

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ComparisonScreen(viewModel: ComparisonViewModel, firstId: Long, secondId: Long, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(firstId, secondId) { viewModel.load(firstId, secondId) }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Comparación", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onBack) { Text("Volver") }
        }
        when (val current = state) {
            ComparisonState.Loading -> Text("Cargando...")
            is ComparisonState.Error -> Text(current.message, color = MaterialTheme.colorScheme.error)
            is ComparisonState.Ready -> {
                val currency = NumberFormat.getCurrencyInstance(Locale("es", "ES"))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ComparisonRow("Cuota", currency.format(current.comparison.first.resultado.cuotaPeriodica), currency.format(current.comparison.second.resultado.cuotaPeriodica), currency.format(current.comparison.paymentDifference))
                        ComparisonRow("Intereses", currency.format(current.comparison.first.resultado.totalIntereses), currency.format(current.comparison.second.resultado.totalIntereses), currency.format(current.comparison.interestDifference))
                        ComparisonRow("Plazo", "${current.comparison.first.resultado.periodos.size}", "${current.comparison.second.resultado.periodos.size}", current.comparison.termDifference.toString())
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(label: String, first: String, second: String, difference: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(first)
        Text(second)
        Text("Dif: $difference", style = MaterialTheme.typography.labelSmall)
    }
}
