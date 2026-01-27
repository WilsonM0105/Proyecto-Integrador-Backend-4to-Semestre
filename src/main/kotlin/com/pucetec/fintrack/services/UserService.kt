package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.BusinessException
import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.mappers.UserMapper
import com.pucetec.fintrack.models.requests.CreateUserRequest
import com.pucetec.fintrack.models.responses.UserResponse
import com.pucetec.fintrack.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun create(req: CreateUserRequest): UserResponse {
        val email = req.email.trim().lowercase()

        val exists = userRepository.findByEmail(email).isPresent
        if (exists) throw BusinessException("Email already exists")

        val entity = UserMapper.toEntity(
            req.copy(email = email)
        )

        val saved = userRepository.save(entity)
        return UserMapper.toResponse(saved)
    }

    fun getById(id: UUID): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { NotFoundException("User", id.toString()) }

        return UserMapper.toResponse(user)
    }

    fun list(): List<UserResponse> {
        return userRepository.findAll().map { UserMapper.toResponse(it) }
    }
}
