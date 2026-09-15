package com.example.mortgage.data.export

import com.example.mortgage.domain.model.ResultadoHipoteca
import java.time.Instant

data class ExportDocument(
    val name: String,
    val createdAt: Instant,
    val annualAmount: String,
    val termYears: Int,
    val annualInterest: String,
    val frequency: String,
    val result: ResultadoHipoteca
)
