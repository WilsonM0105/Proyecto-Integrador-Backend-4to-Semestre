package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.models.entities.Category
import com.pucetec.fintrack.models.entities.TransactionType
import com.pucetec.fintrack.models.entities.User
import com.pucetec.fintrack.models.requests.CreateCategoryRequest
import com.pucetec.fintrack.repositories.CategoryRepository
import com.pucetec.fintrack.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals

class CategoryServiceTest {

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var categoryRepositoryMock: CategoryRepository

    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun init() {
        userRepositoryMock = mock(UserRepository::class.java)
        categoryRepositoryMock = mock(CategoryRepository::class.java)

        categoryService = CategoryService(
            userRepository = userRepositoryMock,
            categoryRepository = categoryRepositoryMock
        )
    }

    @Test
    fun `SHOULD create a category GIVEN valid user`() {
        val userId = UUID.randomUUID()
        val user = User(id = userId, fullName = "Wilson", email = "wilson@mail.com")

        val request = CreateCategoryRequest(
            userId = userId,
            name = "Sueldo",
            type = TransactionType.INCOME
        )

        val saved = Category(
            id = UUID.randomUUID(),
            user = user,
            name = "Sueldo",
            type = TransactionType.INCOME
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(categoryRepositoryMock.save(ArgumentMatchers.any(Category::class.java))).thenReturn(saved)

        val response = categoryService.create(request)

        assertEquals(saved.id, response.id)
        assertEquals(userId, response.userId)
        assertEquals("Sueldo", response.name)
        assertEquals(TransactionType.INCOME, response.type)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on create`() {
        val request = CreateCategoryRequest(
            userId = UUID.randomUUID(),
            name = "Comida",
            type = TransactionType.EXPENSE
        )

        `when`(userRepositoryMock.findById(request.userId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            categoryService.create(request)
        }
    }

    @Test
    fun `SHOULD return a category GIVEN existing id`() {
        val id = UUID.randomUUID()
        val user = User(fullName = "W", email = "w@mail.com")

        val category = Category(
            id = id,
            user = user,
            name = "Comida",
            type = TransactionType.EXPENSE
        )

        `when`(categoryRepositoryMock.findById(id)).thenReturn(Optional.of(category))

        val response = categoryService.getById(id)

        assertEquals(id, response.id)
        assertEquals("Comida", response.name)
        assertEquals(TransactionType.EXPENSE, response.type)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN category does not exist on getById`() {
        val id = UUID.randomUUID()
        `when`(categoryRepositoryMock.findById(id)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            categoryService.getById(id)
        }
    }

    @Test
    fun `SHOULD list categories by user GIVEN existing user`() {
        val userId = UUID.randomUUID()
        val user = User(id = userId, fullName = "W", email = "w@mail.com")

        val list = listOf(
            Category(user = user, name = "Sueldo", type = TransactionType.INCOME),
            Category(user = user, name = "Comida", type = TransactionType.EXPENSE)
        )

        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user))
        `when`(categoryRepositoryMock.findAllByUser_Id(userId)).thenReturn(list)

        val response = categoryService.listByUser(userId)

        assertEquals(2, response.size)
        assertEquals("Sueldo", response[0].name)
        assertEquals(TransactionType.INCOME, response[0].type)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on listByUser`() {
        val userId = UUID.randomUUID()
        `when`(userRepositoryMock.findById(userId)).thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            categoryService.listByUser(userId)
        }
    }


}
