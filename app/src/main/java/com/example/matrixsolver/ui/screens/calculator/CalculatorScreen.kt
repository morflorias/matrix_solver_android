package com.example.matrixsolver.ui.screens.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matrixsolver.R
import com.example.matrixsolver.domain.model.OperationType
import com.example.matrixsolver.ui.components.MatrixInput
import com.example.matrixsolver.ui.components.ResultDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var operationExpanded by remember { mutableStateOf(false) }
    
    var matrixARowsText by remember { mutableStateOf("2") }
    var matrixAColsText by remember { mutableStateOf("2") }
    var matrixBRowsText by remember { mutableStateOf("2") }
    var matrixBColsText by remember { mutableStateOf("2") }
    
    var isARowsFocused by remember { mutableStateOf(false) }
    var isAColsFocused by remember { mutableStateOf(false) }
    var isBRowsFocused by remember { mutableStateOf(false) }
    var isBColsFocused by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.matrixARows) {
        if (!isARowsFocused) matrixARowsText = uiState.matrixARows.toString()
    }
    LaunchedEffect(uiState.matrixACols) {
        if (!isAColsFocused) matrixAColsText = uiState.matrixACols.toString()
    }
    LaunchedEffect(uiState.matrixBRows) {
        if (!isBRowsFocused) matrixBRowsText = uiState.matrixBRows.toString()
    }
    LaunchedEffect(uiState.matrixBCols) {
        if (!isBColsFocused) matrixBColsText = uiState.matrixBCols.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.matrix_calculator)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Operation selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.select_operation),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                ExposedDropdownMenuBox(
                    expanded = operationExpanded,
                    onExpandedChange = { operationExpanded = it },
                    modifier = Modifier.width(200.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.selectedOperation.displayNameRu,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = operationExpanded) },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    ExposedDropdownMenu(
                        expanded = operationExpanded,
                        onDismissRequest = { operationExpanded = false }
                    ) {
                        OperationType.entries.forEach { operation ->
                            DropdownMenuItem(
                                text = { Text(operation.displayNameRu) },
                                onClick = {
                                    viewModel.updateOperation(operation)
                                    operationExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Scalar input
            if (uiState.selectedOperation == OperationType.MULTIPLICATION_BY_NUMBER) {
                OutlinedTextField(
                    value = uiState.scalar,
                    onValueChange = { viewModel.updateScalar(it) },
                    label = { Text(stringResource(R.string.scalar)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Minor row/col input
            if (uiState.selectedOperation == OperationType.MINOR) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.minorRow.toString(),
                        onValueChange = { viewModel.updateMinorRow(it) },
                        label = { Text("Строка") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.minorCol.toString(),
                        onValueChange = { viewModel.updateMinorCol(it) },
                        label = { Text("Столбец") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        singleLine = true
                    )
                }
            }

            // Matrix A selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.matrix_a),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SizeInputField(
                                value = matrixARowsText,
                                onValueChange = { matrixARowsText = it },
                                onCommit = {
                                    val rows = matrixARowsText.toIntOrNull()
                                    if (rows != null && rows > 0) {
                                        viewModel.updateMatrixARows(rows)
                                    }
                                },
                                onFocusChanged = { isARowsFocused = it },
                                modifier = Modifier.width(60.dp)
                            )

                            Text("×", style = MaterialTheme.typography.bodyMedium)

                            SizeInputField(
                                value = matrixAColsText,
                                onValueChange = { matrixAColsText = it },
                                onCommit = {
                                    val cols = matrixAColsText.toIntOrNull()
                                    if (cols != null && cols > 0) {
                                        viewModel.updateMatrixACols(cols)
                                    }
                                },
                                onFocusChanged = { isAColsFocused = it },
                                modifier = Modifier.width(60.dp)
                            )
                        }
                    }

                    MatrixInput(
                        label = "",
                        rows = uiState.matrixARows,
                        cols = uiState.matrixACols,
                        values = uiState.matrixA,
                        onValueChange = { row, col, value ->
                            viewModel.updateMatrixA(row, col, value)
                        }
                    )
                }
            }

            // Matrix B selector (for binary operations)
            if (uiState.selectedOperation in listOf(
                    OperationType.ADDITION,
                    OperationType.SUBTRACTION,
                    OperationType.MATRIX_MULTIPLICATION
                )
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.matrix_b),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SizeInputField(
                                    value = matrixBRowsText,
                                    onValueChange = { matrixBRowsText = it },
                                    onCommit = {
                                        val rows = matrixBRowsText.toIntOrNull()
                                        if (rows != null && rows > 0) {
                                            viewModel.updateMatrixBRows(rows)
                                        }
                                    },
                                    onFocusChanged = { isBRowsFocused = it },
                                    modifier = Modifier.width(60.dp)
                                )

                                Text("×", style = MaterialTheme.typography.bodyMedium)

                                SizeInputField(
                                    value = matrixBColsText,
                                    onValueChange = { matrixBColsText = it },
                                    onCommit = {
                                        val cols = matrixBColsText.toIntOrNull()
                                        if (cols != null && cols > 0) {
                                            viewModel.updateMatrixBCols(cols)
                                        }
                                    },
                                    onFocusChanged = { isBColsFocused = it },
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                        }

                        MatrixInput(
                            label = "",
                            rows = uiState.matrixBRows,
                            cols = uiState.matrixBCols,
                            values = uiState.matrixB,
                            onValueChange = { row, col, value ->
                                viewModel.updateMatrixB(row, col, value)
                            }
                        )
                    }
                }
            }

            // Error message
            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Calculate button
            Button(
                onClick = { viewModel.calculate() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(R.string.calculate))
            }

            // Result
            if (uiState.result != null) {
                ResultDisplay(
                    result = uiState.result,
                    isMatrix = uiState.isResultMatrix
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SizeInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onCommit: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { onFocusChanged(it.isFocused) },
        keyboardOptions = KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onCommit() }
        ),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Bold
        )
    )
}
