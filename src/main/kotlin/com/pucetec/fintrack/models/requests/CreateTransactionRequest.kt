package com.pucetec.fintrack.models.requests

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateTransactionRequest(
    @field:NotNull
    val userId: UUID,

    @field:NotNull
    val categoryId: UUID,

    @field:Positive
    val amount: BigDecimal,

    @field:NotNull
    val transactionDate: LocalDate,

    val description: String?
)
