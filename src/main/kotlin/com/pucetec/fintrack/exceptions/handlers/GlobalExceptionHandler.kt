package com.pucetec.fintrack.exceptions.handlers

import com.pucetec.fintrack.exceptions.AlreadyExistsException
import com.pucetec.fintrack.exceptions.BusinessException
import com.pucetec.fintrack.exceptions.NotFoundException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
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
        val details: Map<String, Any?>? = null
    )

    // =========================
    // 404
    // =========================
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(404, "NOT_FOUND", ex.message ?: "Not found"))
    }

    // =========================
    // 409
    // =========================
    @ExceptionHandler(AlreadyExistsException::class)
    fun handleAlreadyExists(ex: AlreadyExistsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse(409, "CONFLICT", ex.message ?: "Already exists"))
    }

    // =========================
    // 400 - reglas de negocio
    // =========================
    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(ex: BusinessException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "BAD_REQUEST", ex.message ?: "Business error"))
    }

    // =========================
    // 400 - @Valid (DTO body)
    // =========================
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleBodyValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.associate { fe ->
            fe.field to (fe.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "VALIDATION_ERROR",
                    message = "Validation failed",
                    details = mapOf("fields" to fieldErrors)
                )
            )
    }

    // =========================
    // 400 - validaciones de @RequestParam / @PathVariable (si algún día usas @Validated)
    // =========================
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val violations = ex.constraintViolations.associate { v ->
            v.propertyPath.toString() to (v.message ?: "Invalid value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "VALIDATION_ERROR",
                    message = "Validation failed",
                    details = mapOf("violations" to violations)
                )
            )
    }

    // =========================
    // 400 - faltan params (tu caso userId/categoryId)
    // =========================
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "BAD_REQUEST", ex.message ?: "Invalid request"))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ErrorResponse> {
        val msg = "Missing required parameter: ${ex.parameterName}"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "MISSING_PARAMETER",
                    message = msg,
                    details = mapOf("parameter" to ex.parameterName)
                )
            )
    }

    // =========================
    // 400 - tipo inválido (UUID mal, LocalDate mal, etc.)
    // =========================
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        val msg = "Invalid value for '${ex.name}': '${ex.value}'"
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = 400,
                    error = "TYPE_MISMATCH",
                    message = msg,
                    details = mapOf(
                        "parameter" to ex.name,
                        "value" to ex.value,
                        "expectedType" to (ex.requiredType?.simpleName ?: "unknown")
                    )
                )
            )
    }

    // =========================
    // 400 - JSON malformado / body ilegible
    // =========================
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(400, "MALFORMED_BODY", "Request body is invalid or malformed"))
    }

    // =========================
    // 500 - fallback controlado (solo para errores realmente inesperados)
    // =========================
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(500, "INTERNAL_SERVER_ERROR", "Unexpected error"))
    }
}
