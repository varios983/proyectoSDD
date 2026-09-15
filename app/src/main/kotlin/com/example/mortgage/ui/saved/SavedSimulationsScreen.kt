package com.example.mortgage.ui.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mortgage.domain.model.Simulacion

@Composable
fun SavedSimulationsScreen(
    viewModel: SavedSimulationsViewModel,
    onBack: () -> Unit,
    onCompare: (Long, Long) -> Unit
) {
    val simulations by viewModel.simulations.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Simulaciones guardadas", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onBack) { Text("Volver") }
        }
        if (simulations.isEmpty()) {
            Text("Todavía no hay simulaciones guardadas.")
        } else {
            SimulationList(simulations, viewModel::delete, onCompare)
        }
    }
}

@Composable
private fun SimulationList(
    simulations: List<Simulacion>,
    onDelete: (Long) -> Unit,
    onCompare: (Long, Long) -> Unit
) {
    val firstTwo = simulations.take(2)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (firstTwo.size == 2) {
            Button(onClick = { onCompare(firstTwo[0].id.value, firstTwo[1].id.value) }) {
                Text("Comparar las dos primeras")
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(simulations, key = { it.id.value }) { simulation ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(simulation.nombre, style = MaterialTheme.typography.titleMedium)
                            Text("${simulation.hipoteca.importe} EUR · ${simulation.hipoteca.plazoAnios} años")
                        }
                        Button(onClick = { onDelete(simulation.id.value) }) { Text("Eliminar") }
                    }
                }
            }
        }
    }
}
