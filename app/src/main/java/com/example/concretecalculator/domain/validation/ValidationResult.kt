package com.example.concretecalculator.domain.validation

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
) {
    companion object {
        fun success(): ValidationResult = ValidationResult(isValid = true, errorMessage = null)
        fun error(message: String): ValidationResult = ValidationResult(isValid = false, errorMessage = message)
    }
}
