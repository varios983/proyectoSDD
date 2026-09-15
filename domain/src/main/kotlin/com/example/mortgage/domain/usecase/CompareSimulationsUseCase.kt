package com.example.mortgage.domain.usecase

import com.example.mortgage.domain.model.Simulacion
import com.example.mortgage.domain.repository.SimulationRepository
import java.math.BigDecimal

 data class SimulationComparison(
    val first: Simulacion,
    val second: Simulacion,
    val paymentDifference: BigDecimal,
    val interestDifference: BigDecimal,
    val termDifference: Int
)

class CompareSimulationsUseCase(private val repository: SimulationRepository) {
    suspend operator fun invoke(firstId: Long, secondId: Long): Result<SimulationComparison> = runCatching {
        require(firstId != secondId) { "Selecciona dos simulaciones distintas" }
        val first = requireNotNull(repository.getSimulation(com.example.mortgage.domain.model.SimulationId(firstId)))
        val second = requireNotNull(repository.getSimulation(com.example.mortgage.domain.model.SimulationId(secondId)))
        SimulationComparison(
            first = first,
            second = second,
            paymentDifference = first.resultado.cuotaPeriodica - second.resultado.cuotaPeriodica,
            interestDifference = first.resultado.totalIntereses - second.resultado.totalIntereses,
            termDifference = first.resultado.periodos.size - second.resultado.periodos.size
        )
    }
}
