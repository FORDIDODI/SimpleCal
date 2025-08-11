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

@Composable
fun CalculatorScientificLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit,
    viewModel: CalculatorViewModel
) {
    Calculator(
        state = state,
        history = history,
        onAction = onAction,
        buttonSpacing = 4.dp,
        modifier = Modifier.fillMaxSize(),
        viewModel = viewModel,
        onToggleScientific = onToggleMode,
        isScientificMode = true // Always true for the scientific layout
    )
}

// Data class for scientific function buttons
data class ScientificFunction(
    val symbol: String,
    val action: CalculatorAction,
    val description: String = ""
)