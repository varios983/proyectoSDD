package com.example.mortgage.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mortgage.domain.model.Periodicidad
import com.example.mortgage.domain.usecase.CalculateMortgageUseCase
import com.example.mortgage.domain.usecase.SaveSimulationUseCase
import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import com.example.mortgage.domain.validation.MortgageInput
import com.example.mortgage.domain.validation.ValidateMortgageInputUseCase
import com.example.mortgage.domain.validation.ValidationResult
import java.time.Instant
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculateMortgage: CalculateMortgageUseCase
    , private val validateMortgageInput: ValidateMortgageInputUseCase
    , private val saveSimulation: SaveSimulationUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CalculatorUiState())
    val state: StateFlow<CalculatorUiState> = _state.asStateFlow()

    fun onEvent(event: CalculatorUiEvent) {
        _state.value = when (event) {
            is CalculatorUiEvent.AmountChanged -> _state.value.copy(amount = event.value, fieldErrors = emptyMap())
            is CalculatorUiEvent.TermChanged -> _state.value.copy(termYears = event.value, fieldErrors = emptyMap())
            is CalculatorUiEvent.AnnualRateChanged -> _state.value.copy(annualRate = event.value, fieldErrors = emptyMap())
            is CalculatorUiEvent.FrequencyChanged -> _state.value.copy(frequency = event.value, fieldErrors = emptyMap())
            is CalculatorUiEvent.StartDateChanged -> _state.value.copy(startDate = event.value, fieldErrors = emptyMap())
            is CalculatorUiEvent.SaveNameChanged -> _state.value.copy(saveName = event.value, saveMessage = null)
            CalculatorUiEvent.SaveDialogOpened -> _state.value.copy(showSaveDialog = true, saveMessage = null)
            CalculatorUiEvent.SaveDialogClosed -> _state.value.copy(showSaveDialog = false)
            CalculatorUiEvent.SaveClicked -> _state.value.copy(saveMessage = null)
            CalculatorUiEvent.CalculateClicked -> _state.value.copy(isCalculating = true, fieldErrors = emptyMap(), generalError = null)
        }
        if (event == CalculatorUiEvent.CalculateClicked) calculate()
        if (event == CalculatorUiEvent.SaveClicked) save()
    }

    private fun calculate() {
        val current = _state.value
        viewModelScope.launch {
            when (val calculation = calculateMortgage(
                MortgageInput(
                    importe = current.amount,
                    plazoAnios = current.termYears,
                    interesAnual = current.annualRate,
                    periodicidad = current.frequency.name,
                    fechaInicio = current.startDate
                )
            )) {
                is ValidationResult.Invalid -> _state.value = current.copy(
                    isCalculating = false,
                    fieldErrors = calculation.errors.associate { it.field to it.message }
                )
                is ValidationResult.Valid -> _state.value = current.copy(
                    isCalculating = false,
                    result = calculation.value,
                    fieldErrors = emptyMap()
                )
            }
        }
    }

    private fun save() {
        val current = _state.value
        val result = current.result ?: return
        viewModelScope.launch {
            when (val mortgage = validateMortgageInput(
                MortgageInput(current.amount, current.termYears, current.annualRate, current.frequency.name, current.startDate)
            )) {
                is ValidationResult.Invalid -> _state.value = current.copy(
                    fieldErrors = mortgage.errors.associate { it.field to it.message }
                )
                is ValidationResult.Valid -> {
                    val simulation = Simulacion(
                        id = SimulationId(0),
                        nombre = current.saveName,
                        hipoteca = mortgage.value,
                        fechaCreacion = Instant.now(),
                        resultado = result
                    )
                    val saved = saveSimulation(current.saveName, simulation)
                    _state.value = current.copy(
                        showSaveDialog = false,
                        saveMessage = saved.fold({ "Simulación guardada" }, { it.message })
                    )
                }
            }
        }
    }
}
