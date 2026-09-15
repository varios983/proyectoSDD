package com.example.mortgage.di

import android.content.Context
import androidx.room.Room
import com.example.mortgage.data.local.room.MortgageDatabase
import com.example.mortgage.data.repository.RoomSimulationRepository
import com.example.mortgage.domain.repository.SimulationRepository
import com.example.mortgage.domain.usecase.CalculateMortgageUseCase
import com.example.mortgage.domain.usecase.CompareSimulationsUseCase
import com.example.mortgage.domain.usecase.DeleteSimulationUseCase
import com.example.mortgage.domain.usecase.GetSimulationUseCase
import com.example.mortgage.domain.usecase.ListSimulationsUseCase
import com.example.mortgage.domain.usecase.SaveSimulationUseCase
import com.example.mortgage.domain.calculation.FrenchAmortizationCalculator
import com.example.mortgage.domain.validation.ValidateMortgageInputUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MortgageDatabase =
        Room.databaseBuilder(context, MortgageDatabase::class.java, "mortgage.db").build()

    @Provides
    @Singleton
    fun provideSimulationRepository(database: MortgageDatabase): SimulationRepository =
        RoomSimulationRepository(database)

    @Provides
    fun provideValidator() = ValidateMortgageInputUseCase()

    @Provides
    fun provideCalculator() = FrenchAmortizationCalculator()

    @Provides
    fun provideCalculateMortgageUseCase(
        validator: ValidateMortgageInputUseCase,
        calculator: FrenchAmortizationCalculator
    ) = CalculateMortgageUseCase(validator, calculator)

    @Provides
    fun provideSaveSimulationUseCase(repository: SimulationRepository) = SaveSimulationUseCase(repository)

    @Provides
    fun provideListSimulationsUseCase(repository: SimulationRepository) = ListSimulationsUseCase(repository)

    @Provides
    fun provideGetSimulationUseCase(repository: SimulationRepository) = GetSimulationUseCase(repository)

    @Provides
    fun provideDeleteSimulationUseCase(repository: SimulationRepository) = DeleteSimulationUseCase(repository)

    @Provides
    fun provideCompareSimulationsUseCase(repository: SimulationRepository) = CompareSimulationsUseCase(repository)
}
