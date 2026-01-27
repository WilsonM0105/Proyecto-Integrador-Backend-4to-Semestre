package com.pucetec.fintrack.services

import com.pucetec.fintrack.exceptions.NotFoundException
import com.pucetec.fintrack.mappers.CategoryMapper
import com.pucetec.fintrack.models.requests.CreateCategoryRequest
import com.pucetec.fintrack.models.responses.CategoryResponse
import com.pucetec.fintrack.repositories.CategoryRepository
import com.pucetec.fintrack.repositories.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CategoryService(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository
) {

    fun create(req: CreateCategoryRequest): CategoryResponse {
        val user = userRepository.findById(req.userId)
            .orElseThrow { NotFoundException("User", req.userId.toString()) }

        val entity = CategoryMapper.toEntity(req, user)
        val saved = categoryRepository.save(entity)

        return CategoryMapper.toResponse(saved)
    }

    fun getById(id: UUID): CategoryResponse {
        val category = categoryRepository.findById(id)
            .orElseThrow { NotFoundException("Category", id.toString()) }

        return CategoryMapper.toResponse(category)
    }

    fun listByUser(userId: UUID): List<CategoryResponse> {
        // 404 consistente si el user no existe
        userRepository.findById(userId)
            .orElseThrow { NotFoundException("User", userId.toString()) }

        return categoryRepository.findAllByUser_Id(userId)
            .map { CategoryMapper.toResponse(it) }
    }
}
