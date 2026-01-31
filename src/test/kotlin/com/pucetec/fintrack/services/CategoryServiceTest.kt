package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.models.entities.Category
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
    fun `SHOULD create a category GIVEN an existing user`() {

        val userId = UUID.randomUUID()

        val user = User(
            id = userId,
            fullName = "Wilson",
            email = "wilson@mail.com"
        )

        val request = CreateCategoryRequest(
            userId = userId,
            name = "Comida",
            isIncome = false
        )

        val savedCategory = Category(
            id = UUID.randomUUID(),
            user = user,
            name = "Comida",
            isIncome = false
        )

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.of(user))

        `when`(categoryRepositoryMock.save(ArgumentMatchers.any(Category::class.java)))
            .thenReturn(savedCategory)

        val response = categoryService.create(request)

        assertEquals(savedCategory.id, response.id)
        assertEquals(userId, response.userId)
        assertEquals("Comida", response.name)
        assertEquals(false, response.isIncome)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on create`() {

        val userId = UUID.randomUUID()

        val request = CreateCategoryRequest(
            userId = userId,
            name = "Comida",
            isIncome = false
        )

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            categoryService.create(request)
        }
    }

    @Test
    fun `SHOULD return a category GIVEN an existing id`() {

        val categoryId = UUID.randomUUID()

        val user = User(fullName = "W", email = "w@mail.com")

        val category = Category(
            id = categoryId,
            user = user,
            name = "Transporte",
            isIncome = false
        )

        `when`(categoryRepositoryMock.findById(categoryId))
            .thenReturn(Optional.of(category))

        val response = categoryService.getById(categoryId)

        assertEquals(categoryId, response.id)
        assertEquals("Transporte", response.name)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN category does not exist`() {

        val categoryId = UUID.randomUUID()

        `when`(categoryRepositoryMock.findById(categoryId))
            .thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            categoryService.getById(categoryId)
        }
    }

    @Test
    fun `SHOULD list categories by user GIVEN existing user`() {

        val userId = UUID.randomUUID()

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.of(User(id = userId, fullName = "W", email = "w@mail.com")))

        val user = User(id = userId, fullName = "W", email = "w@mail.com")
        val c1 = Category(user = user, name = "A", isIncome = false)
        val c2 = Category(user = user, name = "B", isIncome = true)

        `when`(categoryRepositoryMock.findAllByUser_Id(userId))
            .thenReturn(listOf(c1, c2))

        val response = categoryService.listByUser(userId)

        assertEquals(2, response.size)
        assertEquals("A", response[0].name)
        assertEquals("B", response[1].name)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN user does not exist on listByUser`() {

        val userId = UUID.randomUUID()

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            categoryService.listByUser(userId)
        }
    }

    @Test
    fun `SHOULD return an empty list GIVEN user exists but no categories`() {

        val userId = UUID.randomUUID()

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.of(User(id = userId, fullName = "W", email = "w@mail.com")))

        `when`(categoryRepositoryMock.findAllByUser_Id(userId))
            .thenReturn(emptyList())

        val response = categoryService.listByUser(userId)

        assertEquals(0, response.size)
    }
}
