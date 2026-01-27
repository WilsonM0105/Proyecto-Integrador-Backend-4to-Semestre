package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.BusinessException
import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.mappers.TransactionMapper
import com.pucetec.fintrack.models.entities.TransactionType
import com.pucetec.fintrack.models.requests.CreateTransactionRequest
import com.pucetec.fintrack.models.requests.ReportRequest
import com.pucetec.fintrack.models.responses.ReportResponse
import com.pucetec.fintrack.models.responses.TransactionResponse
import com.pucetec.fintrack.repositories.CategoryRepository
import com.pucetec.fintrack.repositories.TransactionRepository
import com.pucetec.fintrack.repositories.UserRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class TransactionService(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {

    fun create(req: CreateTransactionRequest): TransactionResponse {
        val user = userRepository.findById(req.userId)
            .orElseThrow { NotFoundException("User", req.userId.toString()) }

        val category = categoryRepository.findById(req.categoryId)
            .orElseThrow { NotFoundException("Category", req.categoryId.toString()) }

        // Regla: categoría debe pertenecer al mismo user
        if (category.user.id != user.id) {
            throw BusinessException("Category does not belong to the user")
        }

        // Regla: amount positivo (igual ya lo valida @Positive, pero en service igual por coverage)
        if (req.amount <= BigDecimal.ZERO) {
            throw BusinessException("Amount must be greater than 0")
        }


        val entity = TransactionMapper.toEntity(req, user, category)
        val saved = transactionRepository.save(entity)

        return TransactionMapper.toResponse(saved)
    }

    fun getById(id: UUID): TransactionResponse {
        val trx = transactionRepository.findById(id)
            .orElseThrow { NotFoundException("Transaction", id.toString()) }

        return TransactionMapper.toResponse(trx)
    }

    fun listByUser(userId: UUID): List<TransactionResponse> {
        // 404 consistente si el user no existe
        userRepository.findById(userId)
            .orElseThrow { NotFoundException("User", userId.toString()) }

        return transactionRepository.findAllByUser_Id(userId)
            .map { TransactionMapper.toResponse(it) }
    }

    fun listByCategory(categoryId: UUID): List<TransactionResponse> {
        // 404 consistente si category no existe
        categoryRepository.findById(categoryId)
            .orElseThrow { NotFoundException("Category", categoryId.toString()) }

        return transactionRepository.findAllByCategory_Id(categoryId)
            .map { TransactionMapper.toResponse(it) }
    }

    fun report(req: ReportRequest): ReportResponse {
        // 404 consistente si el user no existe
        userRepository.findById(req.userId)
            .orElseThrow { NotFoundException("User", req.userId.toString()) }

        if (req.endDate.isBefore(req.startDate)) {
            throw BusinessException("endDate must be >= startDate")
        }

        val list = transactionRepository.findAllByUser_IdAndDateBetween(
            userId = req.userId,
            start = req.startDate,
            end = req.endDate
        )

        val totalIncome = list
            .filter { it.type == TransactionType.INCOME }   // <-- ajusta si tu enum difiere
            .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }

        val totalExpense = list
            .filter { it.type == TransactionType.EXPENSE }  // <-- ajusta si tu enum difiere
            .fold(BigDecimal.ZERO) { acc, t -> acc + t.amount }

        val balance = totalIncome - totalExpense

        return ReportResponse(
            userId = req.userId,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = balance
        )
    }
}
