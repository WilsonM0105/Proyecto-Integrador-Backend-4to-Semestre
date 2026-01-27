package com.pucetec.fintrack.models.requests

import java.time.LocalDate
import java.util.UUID

data class ReportRequest(
    val userId: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate
)
