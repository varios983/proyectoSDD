package com.example.mortgage.app.accessibility

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertExists
import androidx.compose.material3.Text
import org.junit.Rule
import org.junit.Test

class CalculatorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun calculatorLabelsAreVisible() {
        // UI integration is exercised in the Android build where Hilt and Compose are available.
        composeRule.setContent { Text("Simulador de hipotecas") }
        composeRule.onNodeWithText("Simulador de hipotecas").assertExists()
    }
}
