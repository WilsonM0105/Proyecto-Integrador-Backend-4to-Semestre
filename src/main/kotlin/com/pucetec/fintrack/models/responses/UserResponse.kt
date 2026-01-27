package com.pucetec.fintrack.models.responses

import java.time.Instant
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val fullName: String,
    val email: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
