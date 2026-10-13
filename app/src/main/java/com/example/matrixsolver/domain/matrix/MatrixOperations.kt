package com.example.matrixsolver.domain.matrix

import com.example.matrixsolver.domain.model.CalculationResult
import kotlin.math.abs

object MatrixOperations {

    fun transpose(matrix: List<List<Double>>): List<List<Double>> {
        val rows = matrix.size
        val cols = if (rows > 0) matrix[0].size else 0
        return List(cols) { j -> List(rows) { i -> matrix[i][j] } }
    }

    fun determinant(matrix: List<List<Double>>): Double {
        val n = matrix.size
        if (n != matrix[0].size) {
            throw IllegalArgumentException("Matrix must be square")
        }
        return when (n) {
            1 -> matrix[0][0]
            2 -> matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
            3 -> {
                val a = matrix
                a[0][0] * (a[1][1] * a[2][2] - a[1][2] * a[2][1]) -
                a[0][1] * (a[1][0] * a[2][2] - a[1][2] * a[2][0]) +
                a[0][2] * (a[1][0] * a[2][1] - a[1][1] * a[2][0])
            }
            else -> calculateDeterminant(matrix)
        }
    }

    private fun calculateDeterminant(matrix: List<List<Double>>): Double {
        val n = matrix.size
        if (n == 0) return 0.0
        if (n == 1) return matrix[0][0]

        var det = 0.0
        for (j in 0 until n) {
            det += matrix[0][j] * cofactor(matrix, 0, j)
        }
        return det
    }

    private fun cofactor(matrix: List<List<Double>>, row: Int, col: Int): Double {
        val n = matrix.size
        val subMatrix = mutableListOf<List<Double>>()
        
        for (i in 0 until n) {
            if (i == row) continue
            val newRow = mutableListOf<Double>()
            for (j in 0 until n) {
                if (j == col) continue
                newRow.add(matrix[i][j])
            }
            subMatrix.add(newRow)
        }
        
        val sign = if ((row + col) % 2 == 0) 1.0 else -1.0
        return sign * determinant(subMatrix)
    }

    fun add(matrixA: List<List<Double>>, matrixB: List<List<Double>>): List<List<Double>> {
        val rows = matrixA.size
        val cols = matrixA[0].size
        return List(rows) { i ->
            List(cols) { j -> matrixA[i][j] + matrixB[i][j] }
        }
    }

    fun subtract(matrixA: List<List<Double>>, matrixB: List<List<Double>>): List<List<Double>> {
        val rows = matrixA.size
        val cols = matrixA[0].size
        return List(rows) { i ->
            List(cols) { j -> matrixA[i][j] - matrixB[i][j] }
        }
    }

    fun multiplyByScalar(matrix: List<List<Double>>, scalar: Double): List<List<Double>> {
        return matrix.map { row -> row.map { it * scalar } }
    }

    fun multiplyMatrices(matrixA: List<List<Double>>, matrixB: List<List<Double>>): List<List<Double>> {
        val rowsA = matrixA.size
        val colsA = matrixA[0].size
        val rowsB = matrixB.size
        val colsB = matrixB[0].size

        if (colsA != rowsB) {
            throw IllegalArgumentException("Matrix dimensions do not match for multiplication")
        }

        return List(rowsA) { i ->
            List(colsB) { j ->
                var sum = 0.0
                for (k in 0 until colsA) {
                    sum += matrixA[i][k] * matrixB[k][j]
                }
                sum
            }
        }
    }

    fun inverse(matrix: List<List<Double>>): List<List<Double>> {
        val n = matrix.size
        if (n != matrix[0].size) {
            throw IllegalArgumentException("Matrix must be square")
        }
        
        val det = determinant(matrix)

        if (abs(det) < 1e-10) {
            throw ArithmeticException("Matrix is singular (determinant is zero)")
        }

        return when (n) {
            1 -> listOf(listOf(1.0 / matrix[0][0]))
            2 -> {
                val a = matrix
                val invDet = 1.0 / det
                listOf(
                    listOf(a[1][1] * invDet, -a[0][1] * invDet),
                    listOf(-a[1][0] * invDet, a[0][0] * invDet)
                )
            }
            3 -> {
                val a = matrix
                val invDet = 1.0 / det

                val c00 = a[1][1] * a[2][2] - a[1][2] * a[2][1]
                val c01 = a[1][2] * a[2][0] - a[1][0] * a[2][2]
                val c02 = a[1][0] * a[2][1] - a[1][1] * a[2][0]

                val c10 = a[0][2] * a[2][1] - a[0][1] * a[2][2]
                val c11 = a[0][0] * a[2][2] - a[0][2] * a[2][0]
                val c12 = a[0][1] * a[2][0] - a[0][0] * a[2][1]

                val c20 = a[0][1] * a[1][2] - a[0][2] * a[1][1]
                val c21 = a[0][2] * a[1][0] - a[0][0] * a[1][2]
                val c22 = a[0][0] * a[1][1] - a[0][1] * a[1][0]

                listOf(
                    listOf(c00 * invDet, c10 * invDet, c20 * invDet),
                    listOf(c01 * invDet, c11 * invDet, c21 * invDet),
                    listOf(c02 * invDet, c12 * invDet, c22 * invDet)
                )
            }
            else -> calculateInverse(matrix, det)
        }
    }

    private fun calculateInverse(matrix: List<List<Double>>, det: Double): List<List<Double>> {
        val n = matrix.size
        val adjugate = MutableList(n) { MutableList(n) { 0.0 } }
        
        for (i in 0 until n) {
            for (j in 0 until n) {
                adjugate[j][i] = cofactor(matrix, i, j)
            }
        }
        
        return multiplyByScalar(adjugate, 1.0 / det)
    }

    fun trace(matrix: List<List<Double>>): Double {
        val n = minOf(matrix.size, matrix[0].size)
        var sum = 0.0
        for (i in 0 until n) {
            sum += matrix[i][i]
        }
        return sum
    }

    fun minor(matrix: List<List<Double>>, row: Int, col: Int): List<List<Double>> {
        val n = matrix.size
        if (n < 2) throw IllegalArgumentException("Minor requires at least 2x2 matrix")
        
        val minorMatrix = mutableListOf<List<Double>>()
        for (i in 0 until n) {
            if (i == row) continue
            val newRow = mutableListOf<Double>()
            for (j in 0 until n) {
                if (j == col) continue
                newRow.add(matrix[i][j])
            }
            minorMatrix.add(newRow)
        }
        return minorMatrix
    }

    fun minorMatrix(matrix: List<List<Double>>): List<List<Double>> {
        val n = matrix.size
        if (n != matrix[0].size) throw IllegalArgumentException("Minor matrix requires a square matrix")
        if (n < 2) throw IllegalArgumentException("Minor requires at least 2x2 matrix")
        
        return List(n) { i ->
            List(n) { j ->
                val subMatrix = minor(matrix, i, j)
                determinant(subMatrix)
            }
        }
    }

    fun createZeroMatrix(size: Int): List<List<Double>> {
        return List(size) { List(size) { 0.0 } }
    }
}
