package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
        // Row 1
        ScientificFunction("sin", CalculatorAction.Sin, "Sine"),
        ScientificFunction("cos", CalculatorAction.Cos, "Cosine"),
        ScientificFunction("tan", CalculatorAction.Tan, "Tangent"),
        ScientificFunction("log", CalculatorAction.Log, "Logarithm"),
        
        // Row 2
        ScientificFunction("ln", CalculatorAction.Ln, "Natural Logarithm"),
        ScientificFunction("e", CalculatorAction.NumberE, "Euler's Number"),
        ScientificFunction("π", CalculatorAction.NumberPi, "Pi"),
        ScientificFunction("√", CalculatorAction.Sqrt, "Square Root"),
        
        // Row 3
        ScientificFunction("x²", CalculatorAction.Square, "Square"),
        ScientificFunction("x³", CalculatorAction.Cube, "Cube"),
        ScientificFunction("x^y", CalculatorAction.Power, "Power"),
        ScientificFunction("1/x", CalculatorAction.Reciprocal, "Reciprocal"),
        
        // Row 4
        ScientificFunction("x!", CalculatorAction.Factorial, "Factorial"),
        ScientificFunction("(", CalculatorAction.Parentheses, "Open Parenthesis"),
        ScientificFunction(")", CalculatorAction.CloseParenthesis, "Close Parenthesis"),
        ScientificFunction("±", CalculatorAction.ToggleSign, "Toggle Sign")
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

        // Main content row with calculator and scientific functions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            // Main calculator (takes 2/3 of the width)
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
            ) {
                Calculator(
                    state = state,
                    history = history,
                    onAction = onAction,
                    buttonSpacing = 4.dp,
                    modifier = Modifier.fillMaxSize(),
                    viewModel = viewModel,
                    onToggleScientific = { showScientific = !showScientific },
                    isScientificMode = showScientific
                )
            }

            // Spacer between calculator and scientific functions
            Spacer(modifier = Modifier.width(4.dp))

            // Scientific functions (takes 1/3 of the width)
            if (showScientific) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            color = Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(4.dp)
                ) {
                    // Scientific functions grid (4 columns)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(scientificFunctions) { function ->
                            ScientificButton(
                                symbol = function.symbol,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(1.dp),
                                onClick = { onAction(function.action) }
                            )
                        }
                    }
                }
            }
        }

        // Add minimal bottom padding for navigation bar
        Spacer(modifier = Modifier.height(if (bottomPadding > 0.dp) 8.dp else 0.dp))
    }
}
