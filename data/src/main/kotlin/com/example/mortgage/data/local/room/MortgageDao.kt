package com.example.mortgage.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MortgageDao {
    @Query("SELECT * FROM simulations ORDER BY createdAtEpochMillis DESC")
    fun observeSimulations(): Flow<List<SimulationEntity>>

    @Query("SELECT * FROM simulations WHERE id = :id")
    suspend fun findSimulation(id: Long): SimulationEntity?

    @Query("SELECT * FROM amortization_periods WHERE simulationId = :simulationId ORDER BY periodNumber ASC")
    suspend fun findPeriods(simulationId: Long): List<AmortizationPeriodEntity>

    @Insert
    suspend fun insertSimulation(simulation: SimulationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriods(periods: List<AmortizationPeriodEntity>)

    @Query("DELETE FROM simulations WHERE id = :id")
    suspend fun deleteSimulation(id: Long)
}
