package com.pucetec.fintrack.models.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class CreateCategoryRequest(
    @field:NotNull
    val userId: UUID,

    @field:NotBlank
    val name: String,

    val isIncome: Boolean
)
