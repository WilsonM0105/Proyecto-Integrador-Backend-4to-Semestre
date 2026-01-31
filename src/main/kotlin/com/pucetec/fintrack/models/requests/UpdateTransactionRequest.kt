package com.pucetec.fintrack.models.requests

import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class UpdateTransactionRequest(
    @field:Positive(message = "amount must be greater than 0")
    val amount: BigDecimal? = null,

    val description: String? = null
)
