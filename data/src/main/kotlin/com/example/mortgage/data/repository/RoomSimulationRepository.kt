package com.example.mortgage.data.repository

import androidx.room.withTransaction
import com.example.mortgage.data.local.room.MortgageDatabase
import com.example.mortgage.data.mapper.toDomain
import com.example.mortgage.data.mapper.toEntity
import com.example.mortgage.data.mapper.toPeriodEntities
import com.example.mortgage.domain.model.SimulationId
import com.example.mortgage.domain.model.Simulacion
import com.example.mortgage.domain.repository.SimulationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RoomSimulationRepository(private val database: MortgageDatabase) : SimulationRepository {
    private val dao = database.mortgageDao()

    override fun observeSimulations(): Flow<List<Simulacion>> = flow {
        dao.observeSimulations().collect { entities ->
            emit(entities.map { entity -> entity.toDomain(dao.findPeriods(entity.id)) })
        }
    }

    override suspend fun getSimulation(id: SimulationId): Simulacion? = dao.findSimulation(id.value)
        ?.let { it.toDomain(dao.findPeriods(id.value)) }

    override suspend fun saveSimulation(simulation: Simulacion): SimulationId = database.withTransaction {
        val generatedId = dao.insertSimulation(simulation.copy(id = SimulationId(0)).toEntity())
        dao.insertPeriods(simulation.copy(id = SimulationId(generatedId)).toPeriodEntities())
        SimulationId(generatedId)
    }

    override suspend fun deleteSimulation(id: SimulationId) {
        dao.deleteSimulation(id.value)
    }
}
