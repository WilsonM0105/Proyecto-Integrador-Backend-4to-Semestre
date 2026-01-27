package com.pucetec.fintrack.mappers

import com.pucetec.fintrack.models.entities.User
import com.pucetec.fintrack.models.requests.CreateUserRequest
import com.pucetec.fintrack.models.responses.UserResponse
import java.time.Instant

object UserMapper {

    fun toEntity(req: CreateUserRequest): User {
        return User(
            fullName = req.fullName.trim(),
            email = req.email.trim().lowercase(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun toResponse(entity: User): UserResponse {
        return UserResponse(
            id = entity.id,
            fullName = entity.fullName,
            email = entity.email,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
