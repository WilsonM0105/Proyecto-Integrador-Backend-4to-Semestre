package com.pucetec.fintrack.models.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class CreateUserRequest(
    @field:NotBlank
    val fullName: String,

    @field:Email
    val email: String
)
