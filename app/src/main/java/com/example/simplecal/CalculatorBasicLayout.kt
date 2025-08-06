package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.example.simplecal.ui.theme.MediumGray

@Composable
fun CalculatorBasicLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current)
    val bottomPadding = with(LocalDensity.current) { navigationBarHeight.toDp() }
    var showScientific by remember { mutableStateOf(false) }
    
    val scientificFunctions = listOf(
        ScientificFunction("sin", CalculatorAction.Sin, "Sine"),
        ScientificFunction("cos", CalculatorAction.Cos, "Cosine"),
        ScientificFunction("tan", CalculatorAction.Tan, "Tangent"),
        ScientificFunction("log", CalculatorAction.Log, "Logarithm"),
        ScientificFunction("ln", CalculatorAction.Ln, "Natural Logarithm"),
        ScientificFunction("e", CalculatorAction.NumberE, "Euler's Number"),
        ScientificFunction("π", CalculatorAction.NumberPi, "Pi"),
        ScientificFunction("√", CalculatorAction.Sqrt, "Square Root"),
        ScientificFunction("x²", CalculatorAction.Square, "Square"),
        ScientificFunction("1/x", CalculatorAction.Reciprocal, "Reciprocal"),
        ScientificFunction("x!", CalculatorAction.Factorial, "Factorial"),
        ScientificFunction("x^y", CalculatorAction.Power, "Power")
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MediumGray)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Compact toggle button row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onToggleMode,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Straighten,
                    contentDescription = "Toggle Scientific Mode",
                    tint = Color.White
                )
            }
        }

        // Main calculator content
        Calculator(
            state = state,
            history = history,
            onAction = onAction,
            buttonSpacing = 8.dp,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            viewModel = viewModel,
            onToggleScientific = { showScientific = !showScientific },
            isScientificMode = showScientific
        )

        // Scientific functions grid (shown when toggled)
        if (showScientific) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                    .weight(0.6f, fill = false),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(scientificFunctions) { function ->
                    ScientificButton(
                        symbol = function.symbol,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(2.dp),
                        onClick = { onAction(function.action) }
                    )
                }
            }
        }

        // Add minimal bottom padding for navigation bar
        Spacer(modifier = Modifier.height(if (bottomPadding > 0.dp) 8.dp else 0.dp))
    }
}
