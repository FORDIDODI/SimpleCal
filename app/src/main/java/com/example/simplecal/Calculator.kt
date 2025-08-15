package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorBasicLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        // Delete button row only
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onAction(CalculatorAction.Delete) }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Backspace,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Main calculator buttons
        Spacer(modifier = Modifier.height(8.dp))
        CalculatorButtonLayout(
            state = state,
            onAction = onAction
        )
    }
}

@Composable
fun CalculatorButtonLayout(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val buttonModifier = Modifier
            .weight(1f)
            .aspectRatio(1f)

        val buttonRows = listOf(
            listOf("7", "8", "9", "/"),
            listOf("4", "5", "6", "x"),
            listOf("1", "2", "3", "-"),
            listOf("0", ".", "=", "+")
        )

        for (row in buttonRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (label in row) {
                    CalculatorButton(
                        symbol = label,
                        modifier = buttonModifier,
                        onClick = {
                            when (label) {
                                "+" -> onAction(CalculatorAction.Operation(CalculatorOperation.Add))
                                "-" -> onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
                                "x" -> onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
                                "/" -> onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
                                "=" -> onAction(CalculatorAction.Calculate)
                                "." -> onAction(CalculatorAction.Decimal)
                                else -> onAction(CalculatorAction.Number(label.toInt()))
                            }
                        }
                    )
                }
            }
        }
    }
}
