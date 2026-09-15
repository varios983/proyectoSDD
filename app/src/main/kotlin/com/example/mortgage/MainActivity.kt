package com.example.mortgage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import com.example.mortgage.navigation.AppNavGraph
import com.example.mortgage.ui.theme.MortgageTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MortgageTheme {
                AppNavGraph()
            }
        }
    }
}
