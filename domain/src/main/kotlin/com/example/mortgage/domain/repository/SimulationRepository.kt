package com.example.mortgage.domain.repository

import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import kotlinx.coroutines.flow.Flow

interface SimulationRepository {
    fun observeSimulations(): Flow<List<Simulacion>>
    suspend fun getSimulation(id: SimulationId): Simulacion?
    suspend fun saveSimulation(simulation: Simulacion): SimulationId
    suspend fun deleteSimulation(id: SimulationId)
}
