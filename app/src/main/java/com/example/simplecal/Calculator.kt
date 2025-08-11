package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecal.ui.theme.LocalTheme
import com.example.simplecal.ui.theme.ThemeMode

// Fungsi untuk mengurai text yang diedit dan mengupdate state
private fun parseAndUpdateStateFromText(text: String, viewModel: CalculatorViewModel) {
    viewModel.updateStateFromText(text)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calculator(
    state: CalculatorState,
    history: List<String>,
    buttonSpacing: Dp = 8.dp,
    modifier: Modifier = Modifier,
    onAction: (CalculatorAction) -> Unit,
    viewModel: CalculatorViewModel,
    onToggleScientific: () -> Unit,
    isScientificMode: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(16.dp)
    ) {
        // Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2.ifEmpty { "" },
                color = Color.White,
                fontSize = 64.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Buttons Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(5f),
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            // Row 1: Clear, +/-, %, /
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                CalculatorButton(
                    symbol = "C",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFFA5A5A5), CircleShape),
                    textColor = Color.Black,
                    onClick = { onAction(CalculatorAction.Clear) }
                )
                CalculatorButton(
                    symbol = "±",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFFA5A5A5), CircleShape),
                    textColor = Color.Black,
                    onClick = { onAction(CalculatorAction.ToggleSign) }
                )
                CalculatorButton(
                    symbol = "%",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFFA5A5A5), CircleShape),
                    textColor = Color.Black,
                    onClick = { onAction(CalculatorAction.Percent) }
                )
                CalculatorButton(
                    symbol = "÷",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFFFF9F0A), CircleShape),
                    textColor = Color.White,
                    onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }
                )
            }

            // Rows for numbers 7-9, 4-6, 1-3
            val numberRows = listOf(
                listOf("7", "8", "9"),
                listOf("4", "5", "6"),
                listOf("1", "2", "3")
            )

            numberRows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    row.forEach { number ->
                        CalculatorButton(
                            symbol = number,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(Color(0xFF333333), CircleShape),
                            textColor = Color.White,
                            onClick = { onAction(CalculatorAction.Number(number.toInt())) }
                        )
                    }
                    // Operation button at the end of each row
                    val operation = when (row[0]) {
                        "7" -> CalculatorOperation.Multiply
                        "4" -> CalculatorOperation.Subtract
                        else -> CalculatorOperation.Add
                    }
                    CalculatorButton(
                        symbol = operation.symbol,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(Color(0xFFFF9F0A), CircleShape),
                        textColor = Color.White,
                        onClick = { onAction(CalculatorAction.Operation(operation)) }
                    )
                }
            }

            // Bottom row: 0, ., =
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                // Zero button (wider)
                CalculatorButton(
                    symbol = "0",
                    modifier = Modifier
                        .weight(2f)
                        .aspectRatio(2f, false)
                        .background(Color(0xFF333333), CircleShape),
                    textColor = Color.White,
                    onClick = { onAction(CalculatorAction.Number(0)) }
                )
                // Decimal point
                CalculatorButton(
                    symbol = ".",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFF333333), CircleShape),
                    textColor = Color.White,
                    onClick = { onAction(CalculatorAction.Decimal) }
                )
                // Equals button
                CalculatorButton(
                    symbol = "=",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(Color(0xFFFF9F0A), CircleShape),
                    textColor = Color.White,
                    onClick = { onAction(CalculatorAction.Calculate) }
                )
            }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Text(
            text = symbol,
            color = textColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal
        )
    }
}