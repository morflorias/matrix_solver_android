package com.example.matrixsolver.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Calculator : Screen("calculator", "Калькулятор", Icons.Filled.Calculate)
    object History : Screen("history", "История", Icons.Filled.List)
    object About : Screen("about", "Справка", Icons.Filled.Info)
    object Feedback : Screen("feedback", "Отзыв", Icons.Filled.Send)
}

val bottomNavItems = listOf(
    Screen.Calculator,
    Screen.History,
    Screen.About,
    Screen.Feedback
)
