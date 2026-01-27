package com.pucetec.fintrack.exceptions.handlers

import com.pucetec.fintrack.exceptions.AlreadyExistsException
import com.pucetec.fintrack.exceptions.BusinessException
import com.pucetec.fintrack.exceptions.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    data class ErrorResponse(
        val status: Int,
        val error: String,
        val message: String,
        val timestamp: Instant = Instant.now(),
        val details: Map<String, String>? = null
    )

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, "NOT_FOUND", ex.message ?: "Not found"))
    }

    @ExceptionHandler(AlreadyExistsException::class)
    fun handleAlreadyExists(ex: AlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(409, "CONFLICT", ex.message ?: "Already exists"))
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(ex: BusinessException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "BAD_REQUEST", ex.message ?: "Business error"))
    }

    // @Valid body errors (DTO validation)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors
            .associate { err -> err.field to (err.defaultMessage ?: "Invalid value") }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "BAD_REQUEST",
                    message = "Validation failed",
                    details = fieldErrors
                )
            )
    }

    // bad query param types, UUID invalid, date invalid, etc.
    @ExceptionHandler(MethodArgumentTypeMismatchException::class, IllegalArgumentException::class)
    fun handleBadParams(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "BAD_REQUEST",
                    message = ex.message ?: "Invalid parameter"
                )
            )
    }

    // fallback controlado
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "INTERNAL_SERVER_ERROR", "Unexpected error"))
    }
}
