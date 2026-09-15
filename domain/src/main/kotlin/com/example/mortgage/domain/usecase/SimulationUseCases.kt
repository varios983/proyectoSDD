package com.example.mortgage.domain.usecase

import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import com.example.mortgage.domain.repository.SimulationRepository
import kotlinx.coroutines.flow.Flow
import java.time.Instant

class SaveSimulationUseCase(private val repository: SimulationRepository) {
    suspend operator fun invoke(name: String, simulation: Simulacion): Result<SimulationId> {
        val normalizedName = name.trim()
        if (normalizedName.isBlank()) return Result.failure(IllegalArgumentException("El nombre es obligatorio"))
        return runCatching {
            repository.saveSimulation(simulation.copy(nombre = normalizedName, fechaCreacion = Instant.now()))
        }
    }
}

class ListSimulationsUseCase(private val repository: SimulationRepository) {
    operator fun invoke(): Flow<List<Simulacion>> = repository.observeSimulations()
}

class GetSimulationUseCase(private val repository: SimulationRepository) {
    suspend operator fun invoke(id: SimulationId): Simulacion? = repository.getSimulation(id)
}

class DeleteSimulationUseCase(private val repository: SimulationRepository) {
    suspend operator fun invoke(id: SimulationId) = repository.deleteSimulation(id)
}
