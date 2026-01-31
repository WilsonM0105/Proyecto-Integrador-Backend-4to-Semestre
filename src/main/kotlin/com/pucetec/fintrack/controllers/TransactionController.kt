package com.pucetec.fintrack.controllers

import com.pucetec.fintrack.models.requests.CreateTransactionRequest
import com.pucetec.fintrack.models.requests.ReportRequest
import com.pucetec.fintrack.models.requests.UpdateTransactionRequest
import com.pucetec.fintrack.models.responses.ReportResponse
import com.pucetec.fintrack.models.responses.TransactionResponse
import com.pucetec.fintrack.services.TransactionService
import com.pucetec.fintrack.exceptions.BusinessException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.UUID

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateTransactionRequest): TransactionResponse {
        return transactionService.create(req)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): TransactionResponse {
        return transactionService.getById(id)
    }

    @GetMapping
    fun list(
        @RequestParam(required = false) userId: UUID?,
        @RequestParam(required = false) categoryId: UUID?
    ): List<TransactionResponse> {
        return when {
            userId != null -> transactionService.listByUser(userId)
            categoryId != null -> transactionService.listByCategory(categoryId)
            else -> throw BusinessException("userId or categoryId is required")
        }
    }

    @GetMapping("/report")
    fun report(
        @RequestParam userId: UUID,
        @RequestParam startDate: LocalDate,
        @RequestParam endDate: LocalDate
    ): ReportResponse {
        return transactionService.report(
            ReportRequest(
                userId = userId,
                startDate = startDate,
                endDate = endDate
            )
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody req: UpdateTransactionRequest
    ): TransactionResponse {
        return transactionService.update(id, req)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        transactionService.delete(id)
    }
}
