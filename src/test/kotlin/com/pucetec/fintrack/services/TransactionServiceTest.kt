package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.BusinessException
import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.models.entities.Category
import com.pucetec.fintrack.models.entities.Transaction
import com.pucetec.fintrack.models.entities.TransactionType
import com.pucetec.fintrack.models.entities.User
import com.pucetec.fintrack.models.requests.CreateTransactionRequest
import com.pucetec.fintrack.models.requests.ReportRequest
import com.pucetec.fintrack.models.requests.UpdateTransactionRequest
import com.pucetec.fintrack.repositories.CategoryRepository
import com.pucetec.fintrack.repositories.TransactionRepository
import com.pucetec.fintrack.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.times
import org.mockito.Mockito.never
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TransactionServiceTest {

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var categoryRepositoryMock: CategoryRepository
    private lateinit var transactionRepositoryMock: TransactionRepository

    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun init() {
        userRepositoryMock = mock(UserRepository::class.java)
        categoryRepositoryMock = mock(CategoryRepository::class.java)
        transactionRepositoryMock = mock(TransactionRepository::class.java)

        transactionService = TransactionService(
            userRepository = userRepositoryMock,
            categoryRepository = categoryRepositoryMock,
            transactionRepository = transactionRepositoryMock
        )
    }

    @Test
    fun `SHOULD create a transaction GIVEN valid user and category`() {
        val userId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        val user = User(id = userId, fullName = "Wilson", email = "wilson@mail.com")
        val category = Category(id = categoryId, user = user, name = "Sueldo", isIncome = true)

        val request = CreateTransactionRequest(
            userId = userId,
            categoryId = categoryId,
            amount = BigDecimal("100.00"),
            trxDate = LocalDate.now(),
            description = "Pago"
        )

        val savedTrx = Transaction(
            id = UUID.randomUUID(),
            user = user,
            category = category,
            type = TransactionType.INCOME,
            amount = BigDecimal("100.00"),
            description = "Pago",
            trxDate = request.trxDate,
            note = null
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category))
        `when`(transactionRepositoryMock.save(ArgumentMatchers.any(Transaction::class.java))).thenReturn(savedTrx)

        val response = transactionService.create(request)

        assertEquals(savedTrx.id, response.id)
        assertEquals(userId, response.userId)
        assertEquals(categoryId, response.categoryId)
        assertEquals(BigDecimal("100.00"), response.amount)
        assertEquals(TransactionType.INCOME, response.type)

        verify(transactionRepositoryMock, times(1)).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on create`() {
        val request = CreateTransactionRequest(
            userId = UUID.randomUUID(),
            categoryId = UUID.randomUUID(),
            amount = BigDecimal("10.00"),
            trxDate = LocalDate.now(),
            description = null
        )

        `when`(userRepositoryMock.findById(request.userId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.create(request)
        }

        verify(transactionRepositoryMock, never()).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN category does not exist on create`() {
        val userId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        val user = User(id = userId, fullName = "W", email = "w@mail.com")

        val request = CreateTransactionRequest(
            userId = userId,
            categoryId = categoryId,
            amount = BigDecimal("10.00"),
            trxDate = LocalDate.now(),
            description = null
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.create(request)
        }

        verify(transactionRepositoryMock, never()).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD return BusinessException GIVEN category does not belong to user`() {
        val userId = UUID.randomUUID()
        val otherUserId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        val user = User(id = userId, fullName = "User", email = "u@mail.com")
        val otherUser = User(id = otherUserId, fullName = "Other", email = "o@mail.com")

        val category = Category(id = categoryId, user = otherUser, name = "Comida", isIncome = false)

        val request = CreateTransactionRequest(
            userId = userId,
            categoryId = categoryId,
            amount = BigDecimal("10.00"),
            trxDate = LocalDate.now(),
            description = null
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category))

        assertThrows<BusinessException> {
            transactionService.create(request)
        }

        verify(transactionRepositoryMock, never()).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD return BusinessException GIVEN amount is zero or negative on create`() {
        val userId = UUID.randomUUID()
        val categoryId = UUID.randomUUID()

        val user = User(id = userId, fullName = "W", email = "w@mail.com")
        val category = Category(id = categoryId, user = user, name = "Comida", isIncome = false)

        val request = CreateTransactionRequest(
            userId = userId,
            categoryId = categoryId,
            amount = BigDecimal.ZERO,
            trxDate = LocalDate.now(),
            description = null
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category))

        assertThrows<BusinessException> {
            transactionService.create(request)
        }

        // opcional pero pro:
        verify(transactionRepositoryMock, never()).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD return a transaction GIVEN an existing id`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("5.00"),
            description = "snack",
            trxDate = LocalDate.now(),
            note = null
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))

        val response = transactionService.getById(trxId)

        assertEquals(trxId, response.id)
        assertEquals(BigDecimal("5.00"), response.amount)
        assertEquals(TransactionType.EXPENSE, response.type)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN transaction does not exist on getById`() {
        val trxId = UUID.randomUUID()

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.getById(trxId)
        }
    }

    @Test
    fun `SHOULD list transactions by user GIVEN existing user`() {
        val userId = UUID.randomUUID()
        val user = User(id = userId, fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val list = listOf(
            Transaction(user = user, category = category, type = TransactionType.EXPENSE, amount = BigDecimal("2.00"), trxDate = LocalDate.now()),
            Transaction(user = user, category = category, type = TransactionType.EXPENSE, amount = BigDecimal("3.00"), trxDate = LocalDate.now())
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(transactionRepositoryMock.findAllByUser_Id(userId)).thenReturn(list)

        val response = transactionService.listByUser(userId)

        assertEquals(2, response.size)
        assertEquals(BigDecimal("2.00"), response[0].amount)
        verify(transactionRepositoryMock, times(1)).findAllByUser_Id(userId)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on listByUser`() {
        val userId = UUID.randomUUID()

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.listByUser(userId)
        }

        verify(transactionRepositoryMock, never()).findAllByUser_Id(userId)
    }

    @Test
    fun `SHOULD list transactions by category GIVEN existing category`() {
        val categoryId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(id = categoryId, user = user, name = "Comida", isIncome = false)

        val list = listOf(
            Transaction(user = user, category = category, type = TransactionType.EXPENSE, amount = BigDecimal("7.00"), trxDate = LocalDate.now())
        )

        `when`(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.of(category))
        `when`(transactionRepositoryMock.findAllByCategory_Id(categoryId)).thenReturn(list)

        val response = transactionService.listByCategory(categoryId)

        assertEquals(1, response.size)
        assertEquals(BigDecimal("7.00"), response[0].amount)
        assertEquals(categoryId, response[0].categoryId)
        verify(transactionRepositoryMock, times(1)).findAllByCategory_Id(categoryId)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN category does not exist on listByCategory`() {
        val categoryId = UUID.randomUUID()

        `when`(categoryRepositoryMock.findById(categoryId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.listByCategory(categoryId)
        }

        verify(transactionRepositoryMock, never()).findAllByCategory_Id(categoryId)
    }

    @Test
    fun `SHOULD return report GIVEN valid range`() {
        val userId = UUID.randomUUID()
        val user = User(id = userId, fullName = "W", email = "w@mail.com")

        val categoryIncome = Category(user = user, name = "Sueldo", isIncome = true)
        val categoryExpense = Category(user = user, name = "Comida", isIncome = false)

        val start = LocalDate.now().minusDays(7)
        val end = LocalDate.now()

        val list = listOf(
            Transaction(user = user, category = categoryIncome, type = TransactionType.INCOME, amount = BigDecimal("100.00"), trxDate = start.plusDays(1)),
            Transaction(user = user, category = categoryExpense, type = TransactionType.EXPENSE, amount = BigDecimal("40.00"), trxDate = start.plusDays(2))
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(transactionRepositoryMock.findAllByUser_IdAndTrxDateBetween(userId, start, end)).thenReturn(list)

        val response = transactionService.report(
            ReportRequest(userId = userId, startDate = start, endDate = end)
        )

        assertEquals(BigDecimal("100.00"), response.totalIncome)
        assertEquals(BigDecimal("40.00"), response.totalExpense)
        assertEquals(BigDecimal("60.00"), response.balance)

        verify(transactionRepositoryMock, times(1)).findAllByUser_IdAndTrxDateBetween(userId, start, end)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on report`() {
        val userId = UUID.randomUUID()
        val start = LocalDate.now().minusDays(1)
        val end = LocalDate.now()

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.report(
                ReportRequest(userId = userId, startDate = start, endDate = end)
            )
        }

        verify(transactionRepositoryMock, never()).findAllByUser_IdAndTrxDateBetween(userId, start, end)
    }

    @Test
    fun `SHOULD return BusinessException GIVEN endDate is before startDate on report`() {
        val userId = UUID.randomUUID()
        val user = User(id = userId, fullName = "W", email = "w@mail.com")

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))

        assertThrows<BusinessException> {
            transactionService.report(
                ReportRequest(
                    userId = userId,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().minusDays(1)
                )
            )
        }
    }

    @Test
    fun `SHOULD update transaction amount and description GIVEN valid request`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            description = "old",
            trxDate = LocalDate.now(),
            note = null
        )

        val req = UpdateTransactionRequest(
            amount = BigDecimal("25.50"),
            description = "  nueva desc  "
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))
        `when`(transactionRepositoryMock.save(ArgumentMatchers.any(Transaction::class.java)))
            .thenAnswer { it.arguments[0] as Transaction }

        val response = transactionService.update(trxId, req)

        assertEquals(BigDecimal("25.50"), response.amount)
        assertEquals("nueva desc", response.description)
        assertEquals(trxId, response.id)

        verify(transactionRepositoryMock, times(1)).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD update only amount GIVEN description is null`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            description = "keep",
            trxDate = LocalDate.now(),
            note = null
        )

        val req = UpdateTransactionRequest(
            amount = BigDecimal("11.00"),
            description = null
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))
        `when`(transactionRepositoryMock.save(ArgumentMatchers.any(Transaction::class.java)))
            .thenAnswer { it.arguments[0] as Transaction }

        val response = transactionService.update(trxId, req)

        assertEquals(BigDecimal("11.00"), response.amount)
        assertEquals("keep", response.description)
    }

    @Test
    fun `SHOULD update only description GIVEN amount is null`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            description = "old",
            trxDate = LocalDate.now(),
            note = null
        )

        val req = UpdateTransactionRequest(
            amount = null,
            description = "  solo desc  "
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))
        `when`(transactionRepositoryMock.save(ArgumentMatchers.any(Transaction::class.java)))
            .thenAnswer { it.arguments[0] as Transaction }

        val response = transactionService.update(trxId, req)

        assertEquals(BigDecimal("10.00"), response.amount)
        assertEquals("solo desc", response.description)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN transaction does not exist on update`() {
        val trxId = UUID.randomUUID()

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.update(trxId, UpdateTransactionRequest(amount = BigDecimal("1.00"), description = null))
        }
    }

    @Test
    fun `SHOULD return BusinessException GIVEN update request has no fields`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            trxDate = LocalDate.now()
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))

        assertThrows<BusinessException> {
            transactionService.update(trxId, UpdateTransactionRequest(amount = null, description = null))
        }

        verify(transactionRepositoryMock, never()).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD return BusinessException GIVEN amount is zero or negative on update`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            trxDate = LocalDate.now()
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))

        assertThrows<BusinessException> {
            transactionService.update(trxId, UpdateTransactionRequest(amount = BigDecimal.ZERO, description = "x"))
        }

        verify(transactionRepositoryMock, never()).save(ArgumentMatchers.any(Transaction::class.java))
    }

    @Test
    fun `SHOULD update description to empty string GIVEN description is only spaces`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)

        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            description = "old",
            trxDate = LocalDate.now()
        )

        val req = UpdateTransactionRequest(
            amount = null,
            description = "     "
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))
        `when`(transactionRepositoryMock.save(ArgumentMatchers.any(Transaction::class.java)))
            .thenAnswer { it.arguments[0] as Transaction }

        val response = transactionService.update(trxId, req)

        assertEquals("", response.description)
    }

    @Test
    fun `SHOULD delete transaction GIVEN existing id`() {
        val trxId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")
        val category = Category(user = user, name = "Comida", isIncome = false)
        val trx = Transaction(
            id = trxId,
            user = user,
            category = category,
            type = TransactionType.EXPENSE,
            amount = BigDecimal("10.00"),
            trxDate = LocalDate.now()
        )

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.of(trx))

        transactionService.delete(trxId)

        verify(transactionRepositoryMock, times(1)).delete(trx)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN transaction does not exist on delete`() {
        val trxId = UUID.randomUUID()

        `when`(transactionRepositoryMock.findById(trxId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            transactionService.delete(trxId)
        }

        verify(transactionRepositoryMock, never()).delete(ArgumentMatchers.any(Transaction::class.java))
    }
}
