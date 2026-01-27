package com.pucetec.fintrack.controllers

import com.pucetec.fintrack.models.requests.CreateCategoryRequest
import com.pucetec.fintrack.models.responses.CategoryResponse
import com.pucetec.fintrack.services.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateCategoryRequest): CategoryResponse {
        return categoryService.create(req)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): CategoryResponse {
        return categoryService.getById(id)
    }

    @GetMapping
    fun listByUser(@RequestParam userId: UUID): List<CategoryResponse> {
        return categoryService.listByUser(userId)
    }
}
