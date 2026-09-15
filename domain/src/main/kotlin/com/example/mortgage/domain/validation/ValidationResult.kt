package com.example.mortgage.domain.validation

import java.time.LocalDate

data class MortgageInput(
    val importe: String,
    val plazoAnios: String,
    val interesAnual: String,
    val periodicidad: String,
    val fechaInicio: String?
)

data class FieldError(val field: String, val message: String)

sealed interface ValidationResult<out T> {
    data class Valid<T>(val value: T) : ValidationResult<T>
    data class Invalid(val errors: List<FieldError>) : ValidationResult<Nothing>
}

fun parseOptionalDate(raw: String?): LocalDate? = raw
    ?.takeIf { it.isNotBlank() }
    ?.let { LocalDate.parse(it) }
