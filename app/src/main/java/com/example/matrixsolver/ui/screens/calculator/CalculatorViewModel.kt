package com.example.matrixsolver.ui.screens.calculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixsolver.data.HistoryRepository
import com.example.matrixsolver.domain.model.CalculationRecord
import com.example.matrixsolver.domain.model.CalculationResult
import com.example.matrixsolver.domain.model.OperationType
import com.example.matrixsolver.domain.matrix.MatrixOperations
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val matrixARows: Int = 2,
    val matrixACols: Int = 2,
    val matrixBRows: Int = 2,
    val matrixBCols: Int = 2,
    val matrixA: List<List<String>> = List(2) { List(2) { "" } },
    val matrixB: List<List<String>> = List(2) { List(2) { "" } },
    val scalar: String = "1",
    val minorRow: Int = 0,
    val minorCol: Int = 0,
    val selectedOperation: OperationType = OperationType.TRANSPOSITION,
    val result: Any? = null,
    val isResultMatrix: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val historyRepository = HistoryRepository(application)

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun updateMatrixARows(rows: Int) {
        if (rows < 1) {
            _uiState.update { it.copy(error = "Количество строк должно быть не менее 1") }
            return
        }
        if (rows > 25) {
            _uiState.update { it.copy(error = "Максимальный размер матрицы - 25x25") }
            return
        }
        _uiState.update { state ->
            state.copy(
                matrixARows = rows,
                matrixA = List(rows) { r -> List(state.matrixACols) { c -> 
                    if (r < state.matrixA.size && c < state.matrixA.getOrNull(r)?.size.orZero()) 
                        state.matrixA[r][c] else "" 
                } },
                error = null
            )
        }
    }

    fun updateMatrixACols(cols: Int) {
        if (cols < 1) {
            _uiState.update { it.copy(error = "Количество столбцов должно быть не менее 1") }
            return
        }
        if (cols > 25) {
            _uiState.update { it.copy(error = "Максимальный размер матрицы - 25x25") }
            return
        }
        _uiState.update { state ->
            state.copy(
                matrixACols = cols,
                matrixA = List(state.matrixARows) { r -> List(cols) { c -> 
                    if (r < state.matrixA.size && c < state.matrixA.getOrNull(r)?.size.orZero()) 
                        state.matrixA[r][c] else "" 
                } },
                error = null
            )
        }
    }

    fun updateMatrixBRows(rows: Int) {
        if (rows < 1) {
            _uiState.update { it.copy(error = "Количество строк должно быть не менее 1") }
            return
        }
        if (rows > 25) {
            _uiState.update { it.copy(error = "Максимальный размер матрицы - 25x25") }
            return
        }
        _uiState.update { state ->
            state.copy(
                matrixBRows = rows,
                matrixB = List(rows) { r -> List(state.matrixBCols) { c -> 
                    if (r < state.matrixB.size && c < state.matrixB.getOrNull(r)?.size.orZero()) 
                        state.matrixB[r][c] else "" 
                } },
                error = null
            )
        }
    }

    fun updateMatrixBCols(cols: Int) {
        if (cols < 1) {
            _uiState.update { it.copy(error = "Количество столбцов должно быть не менее 1") }
            return
        }
        if (cols > 25) {
            _uiState.update { it.copy(error = "Максимальный размер матрицы - 25x25") }
            return
        }
        _uiState.update { state ->
            state.copy(
                matrixBCols = cols,
                matrixB = List(state.matrixBRows) { r -> List(cols) { c -> 
                    if (r < state.matrixB.size && c < state.matrixB.getOrNull(r)?.size.orZero()) 
                        state.matrixB[r][c] else "" 
                } },
                error = null
            )
        }
    }

    private fun Int?.orZero(): Int = this ?: 0

    fun updateMatrixA(row: Int, col: Int, value: String) {
        _uiState.update { state ->
            val newMatrix = state.matrixA.mapIndexed { r, cols ->
                cols.mapIndexed { c, v -> if (r == row && c == col) value else v }
            }
            state.copy(matrixA = newMatrix, error = null)
        }
    }

    fun updateMatrixB(row: Int, col: Int, value: String) {
        _uiState.update { state ->
            val newMatrix = state.matrixB.mapIndexed { r, cols ->
                cols.mapIndexed { c, v -> if (r == row && c == col) value else v }
            }
            state.copy(matrixB = newMatrix, error = null)
        }
    }

    fun updateScalar(value: String) {
        _uiState.update { it.copy(scalar = value) }
    }

    fun updateMinorRow(value: String) {
        val row = value.toIntOrNull()
        if (row != null) {
            _uiState.update { it.copy(minorRow = row.coerceIn(0, it.matrixARows - 1)) }
        }
    }

    fun updateMinorCol(value: String) {
        val col = value.toIntOrNull()
        if (col != null) {
            _uiState.update { it.copy(minorCol = col.coerceIn(0, it.matrixACols - 1)) }
        }
    }

    fun updateOperation(operation: OperationType) {
        _uiState.update { it.copy(selectedOperation = operation, result = null, error = null) }
    }

    fun calculate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            delay(300)

            try {
                val state = _uiState.value
                
                val matrixA = parseMatrix(state.matrixA)
                val hasMatrixB = state.matrixB.any { row -> row.any { it.isNotBlank() } }
                val matrixB = if (hasMatrixB) parseMatrix(state.matrixB) else null
                val scalar = state.scalar.toDoubleOrNull() ?: 1.0

                when (state.selectedOperation) {
                    OperationType.DETERMINANT -> {
                        if (matrixA.size != matrixA[0].size) {
                            throw IllegalArgumentException("Определитель можно вычислить только для квадратной матрицы")
                        }
                    }
                    OperationType.INVERSE_MATRIX -> {
                        if (matrixA.size != matrixA[0].size) {
                            throw IllegalArgumentException("Обратную матрицу можно найти только для квадратной матрицы")
                        }
                    }
                    OperationType.TRACE -> {
                        if (matrixA.size != matrixA[0].size) {
                            throw IllegalArgumentException("След матрицы можно вычислить только для квадратной матрицы")
                        }
                    }
                    OperationType.MINOR -> {
                        if (matrixA.size != matrixA[0].size) {
                            throw IllegalArgumentException("Минор матрицы можно вычислить только для квадратной матрицы")
                        }
                        if (matrixA.size < 2) {
                            throw IllegalArgumentException("Минор требует матрицу минимум 2x2")
                        }
                    }
                    OperationType.ADDITION, OperationType.SUBTRACTION -> {
                        if (matrixB == null) throw IllegalArgumentException("Введите матрицу B")
                        if (matrixA.size != matrixB.size || matrixA[0].size != matrixB[0].size) {
                            throw IllegalArgumentException("Матрицы должны быть одинакового размера")
                        }
                    }
                    OperationType.MATRIX_MULTIPLICATION -> {
                        if (matrixB == null) throw IllegalArgumentException("Введите матрицу B")
                        if (matrixA[0].size != matrixB.size) {
                            throw IllegalArgumentException("Количество столбцов матрицы A должно равняться количеству строк матрицы B")
                        }
                    }
                    else -> {}
                }

                var resultValue: Any? = null
                var isMatrix = false

                when (state.selectedOperation) {
                    OperationType.TRANSPOSITION -> {
                        resultValue = MatrixOperations.transpose(matrixA)
                        isMatrix = true
                    }
                    OperationType.DETERMINANT -> {
                        resultValue = MatrixOperations.determinant(matrixA)
                        isMatrix = false
                    }
                    OperationType.ADDITION -> {
                        resultValue = MatrixOperations.add(matrixA, matrixB!!)
                        isMatrix = true
                    }
                    OperationType.SUBTRACTION -> {
                        resultValue = MatrixOperations.subtract(matrixA, matrixB!!)
                        isMatrix = true
                    }
                    OperationType.MULTIPLICATION_BY_NUMBER -> {
                        resultValue = MatrixOperations.multiplyByScalar(matrixA, scalar)
                        isMatrix = true
                    }
                    OperationType.MATRIX_MULTIPLICATION -> {
                        resultValue = MatrixOperations.multiplyMatrices(matrixA, matrixB!!)
                        isMatrix = true
                    }
                    OperationType.INVERSE_MATRIX -> {
                        resultValue = MatrixOperations.inverse(matrixA)
                        isMatrix = true
                    }
                    OperationType.TRACE -> {
                        resultValue = MatrixOperations.trace(matrixA)
                        isMatrix = false
                    }
                    OperationType.MINOR -> {
                        resultValue = MatrixOperations.minor(matrixA, state.minorRow, state.minorCol)
                        isMatrix = true
                    }
                }

                _uiState.update {
                    it.copy(
                        result = resultValue,
                        isResultMatrix = isMatrix,
                        isLoading = false
                    )
                }

                try {
                    val calculationResult = if (isMatrix) {
                        @Suppress("UNCHECKED_CAST")
                        CalculationResult.MatrixResult(resultValue as List<List<Double>>)
                    } else {
                        CalculationResult.ScalarResult(resultValue as Double)
                    }

                    val record = CalculationRecord(
                        operationType = state.selectedOperation,
                        matrixSize = state.matrixARows,
                        matrixA = matrixA,
                        matrixB = matrixB,
                        scalar = scalar,
                        result = calculationResult
                    )
                    historyRepository.addRecord(record)
                } catch (e: Exception) {
                    // Silently ignore
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        result = null,
                        isResultMatrix = false,
                        isLoading = false,
                        error = e.message ?: "Ошибка вычисления"
                    )
                }
            }
        }
    }

    private fun parseMatrix(matrix: List<List<String>>): List<List<Double>> {
        return matrix.map { row ->
            row.map { value ->
                if (value.isBlank()) 0.0 else value.toDoubleOrNull() ?: 0.0
            }
        }
    }

    fun loadFromHistory(record: CalculationRecord) {
        _uiState.update { state ->
            val rowsA = record.matrixA.size
            val colsA = record.matrixA.getOrNull(0)?.size ?: 2
            val rowsB = record.matrixB?.size ?: rowsA
            val colsB = record.matrixB?.getOrNull(0)?.size ?: colsA
            
            state.copy(
                matrixARows = rowsA,
                matrixACols = colsA,
                matrixBRows = rowsB,
                matrixBCols = colsB,
                matrixA = record.matrixA.map { row -> row.map { it.toString() } },
                matrixB = record.matrixB?.map { row -> row.map { it.toString() } } ?: List(rowsB) { List(colsB) { "0" } },
                scalar = record.scalar?.toString() ?: "1",
                selectedOperation = record.operationType,
                result = when (val res = record.result) {
                    is CalculationResult.MatrixResult -> res.matrix
                    is CalculationResult.ScalarResult -> res.value
                    is CalculationResult.Error -> res.message
                },
                isResultMatrix = record.result is CalculationResult.MatrixResult
            )
        }
    }
}
