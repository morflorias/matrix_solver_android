package com.example.matrixsolver.domain.model

import java.util.UUID

data class CalculationRecord(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val operationType: OperationType,
    val matrixSize: Int,
    val matrixA: List<List<Double>>,
    val matrixB: List<List<Double>>? = null,
    val scalar: Double? = null,
    val result: CalculationResult
)

enum class OperationType(val displayName: String, val displayNameRu: String) {
    TRANSPOSITION("Transposition", "Транспонирование"),
    DETERMINANT("Determinant", "Определитель"),
    ADDITION("Addition", "Сложение"),
    SUBTRACTION("Subtraction", "Вычитание"),
    MULTIPLICATION_BY_NUMBER("Multiplication by number", "Умножение на число"),
    MATRIX_MULTIPLICATION("Matrix multiplication", "Умножение матриц"),
    INVERSE_MATRIX("Inverse matrix", "Обратная матрица"),
    TRACE("Trace", "След матрицы"),
    MINOR("Minor", "Минор матрицы");

    companion object {
        fun fromName(name: String): OperationType {
            return try {
                valueOf(name)
            } catch (e: Exception) {
                entries.find { it.displayName == name || it.displayNameRu == name } ?: TRANSPOSITION
            }
        }
    }
}

sealed class CalculationResult {
    data class MatrixResult(val matrix: List<List<Double>>) : CalculationResult()
    data class ScalarResult(val value: Double) : CalculationResult()
    data class Error(val message: String) : CalculationResult()
}
