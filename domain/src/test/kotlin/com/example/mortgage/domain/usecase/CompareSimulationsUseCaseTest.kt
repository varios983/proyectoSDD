package com.example.mortgage.domain.usecase

import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.Periodicidad
import com.example.mortgage.domain.model.ResultadoHipoteca
import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import com.example.mortgage.domain.repository.SimulationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class CompareSimulationsUseCaseTest {
    @Test
    fun `returns signed differences for two simulations`() = runBlocking {
        val first = simulation(1, "100.00")
        val second = simulation(2, "80.00")
        val repository = FakeRepository(listOf(first, second))

        val result = CompareSimulationsUseCase(repository)(1, 2).getOrThrow()

        assertEquals(BigDecimal("20.00"), result.paymentDifference)
    }

    private fun simulation(id: Long, payment: String) = Simulacion(
        SimulationId(id), "S$id", Hipoteca(BigDecimal("1000"), 1, BigDecimal.ZERO, Periodicidad.MENSUAL), Instant.EPOCH,
        ResultadoHipoteca(BigDecimal(payment), BigDecimal.ZERO, BigDecimal("1000"), emptyList())
    )

    private class FakeRepository(private val values: List<Simulacion>) : SimulationRepository {
        override fun observeSimulations(): Flow<List<Simulacion>> = emptyFlow()
        override suspend fun getSimulation(id: SimulationId) = values.firstOrNull { it.id == id }
        override suspend fun saveSimulation(simulation: Simulacion) = simulation.id
        override suspend fun deleteSimulation(id: SimulationId) = Unit
    }
}
