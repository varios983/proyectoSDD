package com.example.mortgage.domain.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ValidateMortgageInputUseCaseTest {
    private val validate = ValidateMortgageInputUseCase()

    @Test
    fun `accepts valid input and zero interest`() {
        val result = validate(MortgageInput("200000.00", "30", "0", "MENSUAL", null))

        val valid = assertIs<ValidationResult.Valid<*>>(result)
        assertEquals(30, valid.value.plazoAnios)
    }

    @Test
    fun `rejects all out of range values`() {
        val result = validate(MortgageInput("10000001", "51", "-1", "INVALID", "not-a-date"))

        val invalid = assertIs<ValidationResult.Invalid>(result)
        assertEquals(5, invalid.errors.size)
    }

    @Test
    fun `rejects decimal term`() {
        val result = validate(MortgageInput("1000", "1.5", "2", "MENSUAL", null))

        val invalid = assertIs<ValidationResult.Invalid>(result)
        assertEquals("plazoAnios", invalid.errors.single().field)
    }
}
