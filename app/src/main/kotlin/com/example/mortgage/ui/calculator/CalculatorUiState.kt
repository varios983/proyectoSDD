package com.example.mortgage.ui.calculator

import com.example.mortgage.domain.model.Periodicidad
import com.example.mortgage.domain.model.ResultadoHipoteca

data class CalculatorUiState(
    val amount: String = "",
    val termYears: String = "",
    val annualRate: String = "",
    val frequency: Periodicidad = Periodicidad.MENSUAL,
    val startDate: String = "",
    val saveName: String = "",
    val showSaveDialog: Boolean = false,
    val saveMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
    val result: ResultadoHipoteca? = null,
    val isCalculating: Boolean = false,
    val generalError: String? = null
)

sealed interface CalculatorUiEvent {
    data class AmountChanged(val value: String) : CalculatorUiEvent
    data class TermChanged(val value: String) : CalculatorUiEvent
    data class AnnualRateChanged(val value: String) : CalculatorUiEvent
    data class FrequencyChanged(val value: Periodicidad) : CalculatorUiEvent
    data class StartDateChanged(val value: String) : CalculatorUiEvent
    data class SaveNameChanged(val value: String) : CalculatorUiEvent
    data object SaveDialogOpened : CalculatorUiEvent
    data object SaveDialogClosed : CalculatorUiEvent
    data object SaveClicked : CalculatorUiEvent
    data object CalculateClicked : CalculatorUiEvent
}
