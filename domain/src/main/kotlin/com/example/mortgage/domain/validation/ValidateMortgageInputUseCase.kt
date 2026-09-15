package com.example.mortgage.domain.validation

import com.example.mortgage.domain.model.Hipoteca
import com.example.mortgage.domain.model.Periodicidad
import java.math.BigDecimal
import java.time.format.DateTimeParseException

class ValidateMortgageInputUseCase {
    operator fun invoke(input: MortgageInput): ValidationResult<Hipoteca> {
        val errors = mutableListOf<FieldError>()
        val amount = input.importe.toBigDecimalOrNull()
        val term = input.plazoAnios.toIntOrNull()
        val rate = input.interesAnual.toBigDecimalOrNull()
        val frequency = runCatching { Periodicidad.valueOf(input.periodicidad) }.getOrNull()
        val startDate = runCatching { parseOptionalDate(input.fechaInicio) }.getOrElse {
            errors += FieldError("fechaInicio", "La fecha no es válida")
            null
        }

        if (amount == null || amount <= BigDecimal.ZERO) {
            errors += FieldError("importe", "El importe debe ser mayor que cero")
        } else if (amount > BigDecimal("10000000.00")) {
            errors += FieldError("importe", "El importe máximo es 10.000.000,00 EUR")
        }
        if (term == null || term !in 1..50 || input.plazoAnios.contains('.')) {
            errors += FieldError("plazoAnios", "El plazo debe ser un número entero entre 1 y 50")
        }
        if (rate == null || rate < BigDecimal.ZERO) {
            errors += FieldError("interesAnual", "El interés debe ser cero o positivo")
        }
        if (frequency == null) {
            errors += FieldError("periodicidad", "Selecciona mensual, quincenal o semanal")
        }

        return if (errors.isNotEmpty()) {
            ValidationResult.Invalid(errors)
        } else {
            ValidationResult.Valid(
                Hipoteca(
                    importe = amount!!,
                    plazoAnios = term!!,
                    interesAnual = rate!!,
                    periodicidad = frequency!!,
                    fechaInicio = startDate
                )
            )
        }
    }
}
