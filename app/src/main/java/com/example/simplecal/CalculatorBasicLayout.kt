package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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

    val scientificFunctions: List<ScientificFunction> = listOf(
        ScientificFunction("sin", CalculatorAction.Sin, "Sine"),
        ScientificFunction("cos", CalculatorAction.Cos, "Cosine"),
        ScientificFunction("tan", CalculatorAction.Tan, "Tangent"),
        ScientificFunction("log", CalculatorAction.Log, "Logarithm"),
        ScientificFunction("ln", CalculatorAction.Ln, "Natural Log"),
        ScientificFunction("x²", CalculatorAction.Square, "Square"),
        ScientificFunction("x³", CalculatorAction.Cube, "Cube"),
        ScientificFunction("√", CalculatorAction.Sqrt, "Square Root"),

        ScientificFunction("xʸ", CalculatorAction.Power, "Power"),
        ScientificFunction("1/x", CalculatorAction.Reciprocal, "Reciprocal"),
        ScientificFunction("x!", CalculatorAction.Factorial, "Factorial"),
        ScientificFunction("e", CalculatorAction.NumberE, "Euler's Number"),
        ScientificFunction("π", CalculatorAction.NumberPi, "Pi"),
        ScientificFunction("(", CalculatorAction.Parentheses, "Open Parenthesis"),
        ScientificFunction(")", CalculatorAction.CloseParenthesis, "Close Parenthesis"),
        ScientificFunction("±", CalculatorAction.ToggleSign, "Toggle Sign")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MediumGray)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // Main calculator
        Box(
            modifier = Modifier
                .fillMaxWidth(if (showScientific) 0.7f else 1f)
                .fillMaxHeight()
                .align(Alignment.CenterStart)
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

        // Scientific panel
        if (showScientific) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .background(
                        color = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = { showScientific = false },
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.End)
                        .padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Straighten,
                        contentDescription = "Hide Scientific",
                        tint = Color.White
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(scientificFunctions) { function: ScientificFunction ->
                        ScientificButton(
                            symbol = function.symbol,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            onClick = { onAction(function.action) }
                        )
                    }
                }
            }
        }

        if (!showScientific) {
            IconButton(
                onClick = { showScientific = true },
                modifier = Modifier
                    .size(56.dp)
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Straighten,
                    contentDescription = "Show Scientific",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(
            modifier = Modifier
                .height(if (bottomPadding > 0.dp) 8.dp else 0.dp)
                .align(Alignment.BottomCenter)
        )
    }
}
