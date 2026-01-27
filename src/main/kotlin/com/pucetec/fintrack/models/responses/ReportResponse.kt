package com.pucetec.fintrack.models.responses

import java.math.BigDecimal
import java.util.UUID

data class ReportResponse(
    val userId: UUID,
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val balance: BigDecimal
)
