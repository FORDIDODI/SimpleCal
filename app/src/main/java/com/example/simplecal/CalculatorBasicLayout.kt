package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorBasicLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit,
    viewModel: CalculatorViewModel,
    scientificFunctions: List<ScientificFunction> = emptyList()
) {
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(LocalDensity.current)
    val bottomPadding = with(LocalDensity.current) { navigationBarHeight.toDp() }
    var showScientific by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = bottomPadding)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Basic Calculator Layout (takes remaining space)
            Column(
                modifier = Modifier
                    .weight(if (showScientific) 0.6f else 1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // History Display
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    reverseLayout = true
                ) {
                    items(history) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }

                // Expression Display
                Text(
                    text = state.expression,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 8.dp),
                    fontWeight = FontWeight.Light,
                    fontSize = 80.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1
                )

                // Buttons
                val buttonRows = listOf(
                    listOf(CalculatorAction.Clear, CalculatorAction.ToggleSign, CalculatorAction.Operation(CalculatorOperation.Percentage), CalculatorAction.Operation(CalculatorOperation.Divide)),
                    listOf(CalculatorAction.Number(7), CalculatorAction.Number(8), CalculatorAction.Number(9), CalculatorAction.Operation(CalculatorOperation.Multiply)),
                    listOf(CalculatorAction.Number(4), CalculatorAction.Number(5), CalculatorAction.Number(6), CalculatorAction.Operation(CalculatorOperation.Subtract)),
                    listOf(CalculatorAction.Number(1), CalculatorAction.Number(2), CalculatorAction.Number(3), CalculatorAction.Operation(CalculatorOperation.Add)),
                    listOf(CalculatorAction.Number(0), CalculatorAction.Decimal, CalculatorAction.Delete, CalculatorAction.Calculate)
                )

                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        row.forEach { action ->
                            CalculatorButton(
                                symbol = action.symbol,
                                modifier = Modifier
                                    .weight(if (action is CalculatorAction.Number && action.number == 0) 2f else 1f)
                                    .aspectRatio(if (action is CalculatorAction.Number && action.number == 0) 2f else 1f),
                                onClick = { onAction(action) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Scientific Panel
            if (showScientific) {
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
                        .padding(8.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.align(Alignment.BottomCenter)

                    ) {
                        items(scientificFunctions) { function ->
                            CalculatorButton(
                                symbol = function.symbol,
                                modifier = Modifier.aspectRatio(1.5f),
                                onClick = { onAction(function.action) }
                            )
                        }
                    }
                }
            }
        }

        // Toggle button for scientific panel
        IconButton(
            onClick = { showScientific = !showScientific },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Straighten,
                contentDescription = "Toggle Scientific Panel",
                tint = if (showScientific) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
