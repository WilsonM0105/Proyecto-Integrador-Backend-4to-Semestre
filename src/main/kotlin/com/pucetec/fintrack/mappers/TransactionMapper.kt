package com.pucetec.fintrack.mappers

import com.pucetec.fintrack.models.entities.Category
import com.pucetec.fintrack.models.entities.Transaction
import com.pucetec.fintrack.models.entities.User
import com.pucetec.fintrack.models.requests.CreateTransactionRequest
import com.pucetec.fintrack.models.responses.TransactionResponse
import java.time.Instant

object TransactionMapper {

    fun toEntity(
        req: CreateTransactionRequest,
        user: User,
        category: Category
    ): Transaction {

        return Transaction(
            user = user,
            category = category,
            type = category.type,
            amount = req.amount,
            transactionDate = req.transactionDate,
            description = req.description?.trim(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun toResponse(entity: Transaction): TransactionResponse {
        return TransactionResponse(
            id = entity.id,
            userId = entity.user.id,
            categoryId = entity.category.id,
            categoryName = entity.category.name,
            type = entity.type,
            amount = entity.amount,
            transactionDate = entity.transactionDate,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
