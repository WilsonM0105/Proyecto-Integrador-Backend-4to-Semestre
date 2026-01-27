package com.pucetec.fintrack.models.responses

import com.pucetec.fintrack.models.entities.TransactionType
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class TransactionResponse(
    val id: UUID,
    val userId: UUID,
    val categoryId: UUID,
    val categoryName: String,
    val type: TransactionType,
    val amount: BigDecimal,
    val trxDate: LocalDate,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
