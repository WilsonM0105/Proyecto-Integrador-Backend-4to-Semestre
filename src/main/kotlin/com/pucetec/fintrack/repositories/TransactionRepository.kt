package com.pucetec.fintrack.repositories

import com.pucetec.fintrack.models.entities.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate
import java.util.UUID

interface TransactionRepository : JpaRepository<Transaction, UUID> {
    fun findAllByUser_Id(userId: UUID): List<Transaction>
    fun findAllByUser_IdAndTransactionDateBetween(userId: UUID, start: LocalDate, end: LocalDate): List<Transaction>
    fun findAllByCategory_Id(categoryId: UUID): List<Transaction>
}
