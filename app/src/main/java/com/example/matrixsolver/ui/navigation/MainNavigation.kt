package com.example.matrixsolver.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.matrixsolver.ui.screens.about.AboutScreen
import com.example.matrixsolver.ui.screens.calculator.CalculatorScreen
import com.example.matrixsolver.ui.screens.calculator.CalculatorViewModel
import com.example.matrixsolver.ui.screens.feedback.FeedbackScreen
import com.example.matrixsolver.ui.screens.history.HistoryScreen
import com.example.matrixsolver.ui.screens.history.HistoryViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    val calculatorViewModel: CalculatorViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Calculator.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Calculator.route) {
                CalculatorScreen(viewModel = calculatorViewModel)
            }
            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = historyViewModel,
                    onRepeatCalculation = { record ->
                        calculatorViewModel.loadFromHistory(record)
                        navController.navigate(Screen.Calculator.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.About.route) {
                AboutScreen(
                    onFeedbackClick = {
                        navController.navigate(Screen.Feedback.route)
                    }
                )
            }
            composable(Screen.Feedback.route) {
                FeedbackScreen()
            }
        }
    }
}
