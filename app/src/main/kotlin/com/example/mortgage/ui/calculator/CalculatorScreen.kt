package com.example.mortgage.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mortgage.domain.model.Periodicidad
import com.example.mortgage.ui.amortization.AmortizationTable
import com.example.mortgage.ui.calculator.MortgageSummary

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel, onOpenSaved: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CalculatorContent(state = state, onEvent = viewModel::onEvent, onOpenSaved = onOpenSaved)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorContent(state: CalculatorUiState, onEvent: (CalculatorUiEvent) -> Unit, onOpenSaved: () -> Unit) {
    var frequencyExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Simulador de hipotecas", style = MaterialTheme.typography.headlineMedium)
        MortgageField("Importe del préstamo", state.amount, state.fieldErrors["importe"], KeyboardType.Decimal) {
            onEvent(CalculatorUiEvent.AmountChanged(it))
        }
        MortgageField("Plazo en años", state.termYears, state.fieldErrors["plazoAnios"], KeyboardType.Number) {
            onEvent(CalculatorUiEvent.TermChanged(it))
        }
        MortgageField("Interés anual fijo (%)", state.annualRate, state.fieldErrors["interesAnual"], KeyboardType.Decimal) {
            onEvent(CalculatorUiEvent.AnnualRateChanged(it))
        }
        OutlinedTextField(
            value = state.startDate,
            onValueChange = { onEvent(CalculatorUiEvent.StartDateChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Fecha de inicio (AAAA-MM-DD, opcional)") },
            supportingText = { state.fieldErrors["fechaInicio"]?.let { Text(it) } },
            isError = state.fieldErrors.containsKey("fechaInicio"),
            singleLine = true
        )
        ExposedDropdownMenuBox(
            expanded = frequencyExpanded,
            onExpandedChange = { frequencyExpanded = !frequencyExpanded }
        ) {
            OutlinedTextField(
                value = state.frequency.name,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth().semantics { contentDescription = "Periodicidad de pago" },
                label = { Text("Periodicidad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = frequencyExpanded) }
            )
            ExposedDropdownMenu(expanded = frequencyExpanded, onDismissRequest = { frequencyExpanded = false }) {
                Periodicidad.entries.forEach { frequency ->
                    DropdownMenuItem(
                        text = { Text(frequency.name) },
                        onClick = {
                            onEvent(CalculatorUiEvent.FrequencyChanged(frequency))
                            frequencyExpanded = false
                        }
                    )
                }
            }
        }
        Button(
            onClick = { onEvent(CalculatorUiEvent.CalculateClicked) },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Calcular hipoteca" },
            enabled = !state.isCalculating
        ) { Text(if (state.isCalculating) "Calculando..." else "Calcular") }
        Button(onClick = onOpenSaved, modifier = Modifier.fillMaxWidth()) { Text("Ver simulaciones guardadas") }
        state.result?.let { result ->
            Button(
                onClick = { onEvent(CalculatorUiEvent.SaveDialogOpened) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Guardar simulación") }
            state.saveMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            if (state.showSaveDialog) {
                SaveSimulationDialog(
                    name = state.saveName,
                    onNameChanged = { onEvent(CalculatorUiEvent.SaveNameChanged(it)) },
                    onConfirm = { onEvent(CalculatorUiEvent.SaveClicked) },
                    onDismiss = { onEvent(CalculatorUiEvent.SaveDialogClosed) }
                )
            }
            MortgageSummary(result)
            Text("Cuadro de amortización", style = MaterialTheme.typography.titleLarge)
            AmortizationTable(result.periodos)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun MortgageField(label: String, value: String, error: String?, keyboardType: KeyboardType, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        supportingText = { error?.let { Text(it) } },
        isError = error != null,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
