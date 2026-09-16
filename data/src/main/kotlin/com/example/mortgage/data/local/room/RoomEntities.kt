package com.example.mortgage.data.local.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "simulations")
data class SimulationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amountCents: Long,
    val termYears: Int,
    val annualInterest: String,
    val frequency: String,
    val startDate: String?,
    val createdAtEpochMillis: Long,
    val paymentCents: Long,
    val totalInterestCents: Long,
    val totalPaidCents: Long
)

@Entity(
    tableName = "amortization_periods",
    foreignKeys = [
        ForeignKey(
            entity = SimulationEntity::class,
            parentColumns = ["id"],
            childColumns = ["simulationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("simulationId")]
)
data class AmortizationPeriodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val simulationId: Long,
    val periodNumber: Int,
    val date: String?,
    val paymentCents: Long,
    val principalCents: Long,
    val interestCents: Long,
    val balanceCents: Long
)

data class SimulationWithPeriods(
    @Embedded val simulation: SimulationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "simulationId"
    )
    val periods: List<AmortizationPeriodEntity>
)
