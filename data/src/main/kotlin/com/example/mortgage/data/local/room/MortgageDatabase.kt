package com.example.mortgage.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SimulationEntity::class, AmortizationPeriodEntity::class],
    version = 1,
    exportSchema = true
)
abstract class MortgageDatabase : RoomDatabase() {
    abstract fun mortgageDao(): MortgageDao
}
