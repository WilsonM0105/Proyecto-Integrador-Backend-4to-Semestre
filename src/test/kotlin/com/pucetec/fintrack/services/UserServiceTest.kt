package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.BusinessException
import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.models.entities.User
import com.pucetec.fintrack.models.requests.CreateUserRequest
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

class UserServiceTest {

    private lateinit var userRepositoryMock: UserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun init() {
        userRepositoryMock = mock(UserRepository::class.java)
        userService = UserService(userRepository = userRepositoryMock)
    }

    @Test
    fun `SHOULD create a user GIVEN a valid request`() {

        val request = CreateUserRequest(
            fullName = "Wilson Mites",
            email = " WILSON@MAIL.COM "
        )

        val savedUser = User(
            id = UUID.randomUUID(),
            fullName = "Wilson Mites",
            email = "wilson@mail.com"
        )

        `when`(userRepositoryMock.findByEmail("wilson@mail.com"))
            .thenReturn(Optional.empty())

        `when`(userRepositoryMock.save(ArgumentMatchers.any(User::class.java)))
            .thenReturn(savedUser)

        val response = userService.create(request)

        assertEquals(expected = savedUser.id, actual = response.id)
        assertEquals(expected = savedUser.fullName, actual = response.fullName)
        assertEquals(expected = savedUser.email, actual = response.email)
    }

    @Test
    fun `SHOULD NOT create a user GIVEN an existing email`() {

        val request = CreateUserRequest(
            fullName = "Wilson Mites",
            email = "test@mail.com"
        )

        val existing = User(
            id = UUID.randomUUID(),
            fullName = "Existing",
            email = "test@mail.com"
        )

        `when`(userRepositoryMock.findByEmail("test@mail.com"))
            .thenReturn(Optional.of(existing))

        assertThrows<BusinessException> {
            userService.create(request)
        }
    }

    @Test
    fun `SHOULD return a user GIVEN an existing id`() {

        val userId = UUID.randomUUID()

        val user = User(
            id = userId,
            fullName = "Wilson",
            email = "wilson@mail.com"
        )

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.of(user))

        val response = userService.getById(userId)

        assertEquals(expected = user.id, actual = response.id)
        assertEquals(expected = user.fullName, actual = response.fullName)
        assertEquals(expected = user.email, actual = response.email)
    }

    @Test
    fun `SHOULD return NotFoundException GIVEN a non existing id`() {

        val userId = UUID.randomUUID()

        `when`(userRepositoryMock.findById(userId))
            .thenReturn(Optional.empty())

        assertThrows<NotFoundException> {
            userService.getById(userId)
        }
    }

    @Test
    fun `SHOULD list users GIVEN existing users`() {

        val u1 = User(fullName = "A", email = "a@mail.com")
        val u2 = User(fullName = "B", email = "b@mail.com")

        `when`(userRepositoryMock.findAll())
            .thenReturn(listOf(u1, u2))

        val response = userService.list()

        assertEquals(2, response.size)
        assertEquals("A", response[0].fullName)
        assertEquals("B", response[1].fullName)
    }

    @Test
    fun `SHOULD return an empty list GIVEN no users`() {

        `when`(userRepositoryMock.findAll())
            .thenReturn(emptyList())

        val response = userService.list()

        assertEquals(0, response.size)
    }
}
