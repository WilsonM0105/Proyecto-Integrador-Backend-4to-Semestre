package com.pucetec.fintrack.models.responses

import com.pucetec.fintrack.models.entities.TransactionType
import java.time.Instant
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val type: TransactionType,
    val createdAt: Instant,
    val updatedAt: Instant
)
