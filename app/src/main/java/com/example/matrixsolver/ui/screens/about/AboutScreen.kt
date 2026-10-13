package com.example.matrixsolver.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixsolver.R
import com.example.matrixsolver.ui.components.GradientButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onFeedbackClick: () -> Unit
) {
    val gradientColors = listOf(
        Color(0xFF4A7C59),
        Color(0xFF5B8A72),
        Color(0xFF81C784)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.about)) },
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
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(colors = gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxSize(),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Text(
                        text = "${stringResource(R.string.version)} 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.theory_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                TheoryCard(
                    title = stringResource(R.string.transposition),
                    description = stringResource(R.string.transpose_theory),
                    formula = "Aᵀ[i][j] = A[j][i]",
                    example = "A = [1 2]    Aᵀ = [1 3]\n    [3 4]         [2 4]"
                )

                TheoryCard(
                    title = stringResource(R.string.determinant),
                    description = stringResource(R.string.determinant_theory),
                    formula = "det(A) = ad − bc",
                    example = "A = [a b]    det = ad − bc\n    [c d]"
                )

                TheoryCard(
                    title = stringResource(R.string.addition),
                    description = stringResource(R.string.addition_theory),
                    formula = "C[i][j] = A[i][j] + B[i][j]",
                    example = "[1 2]   [5 6]   [6  8 ]\n[3 4] + [7 8] = [10 12]"
                )

                TheoryCard(
                    title = stringResource(R.string.subtraction),
                    description = stringResource(R.string.subtraction_theory),
                    formula = "C[i][j] = A[i][j] − B[i][j]",
                    example = "[5 6]   [1 2]   [4 4]\n[7 8] − [3 4] = [4 4]"
                )

                TheoryCard(
                    title = stringResource(R.string.multiplication_by_number),
                    description = stringResource(R.string.mult_scalar_theory),
                    formula = "C[i][j] = k · A[i][j]",
                    example = "2 · [1 2] = [2 4]\n    [3 4]   [6 8]"
                )

                TheoryCard(
                    title = stringResource(R.string.matrix_multiplication),
                    description = stringResource(R.string.mult_matrix_theory),
                    formula = "C[i][j] = Σ(A[i][k] · B[k][j])",
                    example = "[1 2] × [5 6] = [19 22]\n[3 4]   [7 8]   [43 50]"
                )

                TheoryCard(
                    title = stringResource(R.string.inverse_matrix),
                    description = stringResource(R.string.inverse_theory),
                    formula = "A · A⁻¹ = E",
                    example = "A⁻¹ = 1/det(A) · [ d −b]\n                  [−c  a]"
                )

                TheoryCard(
                    title = stringResource(R.string.trace),
                    description = stringResource(R.string.trace_theory),
                    formula = "tr(A) = Σ aᵢᵢ",
                    example = "A = [1 2]    tr(A) = 1 + 4 = 5\n    [3 4]"
                )

                TheoryCard(
                    title = stringResource(R.string.minor),
                    description = stringResource(R.string.minor_theory),
                    formula = "Mᵢⱼ = det(A без i-й строки и j-го столбца)",
                    example = "A = [1 2 3]    M₁₁ = |4 5| = 4·8−5·7 = −3\n    [4 5 6]            |7 8|\n    [7 8 9]"
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = stringResource(R.string.send_feedback),
                    onClick = onFeedbackClick
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun TheoryCard(
    title: String,
    description: String,
    formula: String,
    example: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Formula box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = formula,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Example box
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                    .padding(12.dp)
            ) {
                Text(
                    text = example,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 18.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
