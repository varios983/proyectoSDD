package com.example.mortgage.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import com.example.mortgage.domain.usecase.DeleteSimulationUseCase
import com.example.mortgage.domain.usecase.ListSimulationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SavedSimulationsViewModel @Inject constructor(
    listSimulations: ListSimulationsUseCase,
    private val deleteSimulation: DeleteSimulationUseCase
) : ViewModel() {
    val simulations: StateFlow<List<Simulacion>> = listSimulations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(id: Long) {
        viewModelScope.launch { deleteSimulation(SimulationId(id)) }
    }
}
