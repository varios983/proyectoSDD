package com.example.mortgage.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@JvmInline
value class SimulationId(val value: Long)

enum class Periodicidad(val periodsPerYear: Int) {
    MENSUAL(12),
    QUINCENAL(24),
    SEMANAL(52)
}

data class Hipoteca(
    val importe: BigDecimal,
    val plazoAnios: Int,
    val interesAnual: BigDecimal,
    val periodicidad: Periodicidad,
    val fechaInicio: LocalDate? = null
)

data class AmortizacionPeriodo(
    val numeroPeriodo: Int,
    val fecha: LocalDate?,
    val cuota: BigDecimal,
    val capitalAmortizado: BigDecimal,
    val intereses: BigDecimal,
    val capitalPendiente: BigDecimal
)

data class ResultadoHipoteca(
    val cuotaPeriodica: BigDecimal,
    val totalIntereses: BigDecimal,
    val totalPagado: BigDecimal,
    val periodos: List<AmortizacionPeriodo>
)

data class Simulacion(
    val id: SimulationId,
    val nombre: String,
    val hipoteca: Hipoteca,
    val fechaCreacion: Instant,
    val resultado: ResultadoHipoteca
)
