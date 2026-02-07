package com.pucetec.fintrack.mappers

import com.pucetec.fintrack.models.entities.Category
import com.pucetec.fintrack.models.entities.User
import com.pucetec.fintrack.models.requests.CreateCategoryRequest
import com.pucetec.fintrack.models.responses.CategoryResponse
import java.time.Instant

object CategoryMapper {

    fun toEntity(req: CreateCategoryRequest, user: User): Category {
        return Category(
            user = user,
            name = req.name.trim(),
            type = req.type,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }

    fun toResponse(entity: Category): CategoryResponse {
        return CategoryResponse(
            id = entity.id,
            userId = entity.user.id,
            name = entity.name,
            type = entity.type,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}
