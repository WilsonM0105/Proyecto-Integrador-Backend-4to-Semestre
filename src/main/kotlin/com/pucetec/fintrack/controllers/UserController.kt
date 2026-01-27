package com.pucetec.fintrack.controllers

import com.pucetec.fintrack.models.requests.CreateUserRequest
import com.pucetec.fintrack.models.responses.UserResponse
import com.pucetec.fintrack.services.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody req: CreateUserRequest): UserResponse {
        return userService.create(req)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): UserResponse {
        return userService.getById(id)
    }

    @GetMapping
    fun list(): List<UserResponse> {
        return userService.list()
    }
}
