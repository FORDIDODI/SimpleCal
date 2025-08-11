package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Data class for scientific function buttons
data class ScientificFunction(
    val symbol: String,
    val action: CalculatorAction
)

@Composable
fun CalculatorScientificLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit,
    viewModel: CalculatorViewModel
) {
    // This layout will now be responsible for providing the scientific functions
    // to the basic layout, which will handle the UI display.
    val scientificFunctions = listOf(
        ScientificFunction("ln", CalculatorAction.Ln),
        ScientificFunction("log", CalculatorAction.Log),
        ScientificFunction("sin", CalculatorAction.Sin),
        ScientificFunction("cos", CalculatorAction.Cos),
        ScientificFunction("tan", CalculatorAction.Tan),
        ScientificFunction("e", CalculatorAction.E),
        ScientificFunction("π", CalculatorAction.Pi),
        ScientificFunction("√", CalculatorAction.Sqrt),
        ScientificFunction("x²", CalculatorAction.Square),
        ScientificFunction("x³", CalculatorAction.Cube),
        ScientificFunction("xʸ", CalculatorAction.Operation(CalculatorOperation.Power)),
        ScientificFunction("x!", CalculatorAction.Factorial)
    )

    CalculatorBasicLayout(
        state = state,
        history = history,
        onAction = onAction,
        onToggleMode = onToggleMode,
        viewModel = viewModel,
        scientificFunctions = scientificFunctions
    )
}