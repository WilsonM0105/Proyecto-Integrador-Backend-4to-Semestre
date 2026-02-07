package com.pucetec.fintrack.models.requests

import com.pucetec.fintrack.models.entities.TransactionType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateCategoryRequest(
    @field:NotNull
    val userId: UUID,

    @field:NotBlank
    val name: String,

    @field:NotNull
    val type: TransactionType
)
