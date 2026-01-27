package com.pucetec.fintrack.models.responses

import java.time.Instant
import java.util.UUID

data class CategoryResponse(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val isIncome: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)
