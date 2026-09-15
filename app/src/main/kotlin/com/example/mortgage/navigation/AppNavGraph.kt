package com.example.mortgage.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mortgage.ui.calculator.CalculatorScreen
import com.example.mortgage.ui.calculator.CalculatorViewModel
import com.example.mortgage.ui.comparison.ComparisonScreen
import com.example.mortgage.ui.comparison.ComparisonViewModel
import com.example.mortgage.ui.saved.SavedSimulationsScreen
import com.example.mortgage.ui.saved.SavedSimulationsViewModel

private const val CalculatorRoute = "calculator"

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = CalculatorRoute) {
        composable(CalculatorRoute) {
            val viewModel: CalculatorViewModel = hiltViewModel()
            CalculatorScreen(viewModel = viewModel, onOpenSaved = { navController.navigate("saved") })
        }
        composable("saved") {
            val viewModel: SavedSimulationsViewModel = hiltViewModel()
            SavedSimulationsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCompare = { first, second -> navController.navigate("compare/$first/$second") }
            )
        }
        composable("compare/{firstId}/{secondId}") { entry ->
            val viewModel: ComparisonViewModel = hiltViewModel()
            ComparisonScreen(
                viewModel = viewModel,
                firstId = entry.arguments?.getString("firstId")?.toLongOrNull() ?: 0,
                secondId = entry.arguments?.getString("secondId")?.toLongOrNull() ?: 0,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
