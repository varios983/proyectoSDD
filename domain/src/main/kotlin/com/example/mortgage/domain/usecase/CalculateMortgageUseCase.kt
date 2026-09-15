package com.example.mortgage.domain.usecase

import com.example.mortgage.domain.calculation.FrenchAmortizationCalculator
import com.example.mortgage.domain.model.ResultadoHipoteca
import com.example.mortgage.domain.validation.MortgageInput
import com.example.mortgage.domain.validation.ValidateMortgageInputUseCase
import com.example.mortgage.domain.validation.ValidationResult

class CalculateMortgageUseCase(
    private val validateMortgageInput: ValidateMortgageInputUseCase,
    private val calculator: FrenchAmortizationCalculator
) {
    operator fun invoke(input: MortgageInput): ValidationResult<ResultadoHipoteca> = when (
        val validation = validateMortgageInput(input)
    ) {
        is ValidationResult.Invalid -> validation
        is ValidationResult.Valid -> ValidationResult.Valid(calculator(validation.value))
    }
}
