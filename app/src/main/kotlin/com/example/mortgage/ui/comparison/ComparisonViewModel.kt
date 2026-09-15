package com.example.mortgage.ui.comparison

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mortgage.domain.usecase.CompareSimulationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ComparisonViewModel @Inject constructor(
    private val compareSimulations: CompareSimulationsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<ComparisonState>(ComparisonState.Loading)
    val state: StateFlow<ComparisonState> = _state.asStateFlow()

    fun load(firstId: Long, secondId: Long) {
        viewModelScope.launch {
            _state.value = compareSimulations(firstId, secondId).fold(
                onSuccess = { ComparisonState.Ready(it) },
                onFailure = { ComparisonState.Error(it.message ?: "No se pudo comparar") }
            )
        }
    }
}

sealed interface ComparisonState {
    data object Loading : ComparisonState
    data class Ready(val comparison: com.example.mortgage.domain.usecase.SimulationComparison) : ComparisonState
    data class Error(val message: String) : ComparisonState
}
