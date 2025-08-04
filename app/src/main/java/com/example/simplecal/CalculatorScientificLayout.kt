package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simplecal.ui.theme.MediumGray

@Composable

fun CalculatorScientificLayout(
    state: CalculatorState,
    history: List<String>,
    onAction : (CalculatorAction) -> Unit
)
{
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MediumGray)
            .padding(25.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column (
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScientificButton("√", onClick = { /* belum di-implement */ })
            ScientificButton("x²", onClick = { /* belum di-implement */ })
            ScientificButton("1/x", onClick = { /* belum di-implement */ })
            ScientificButton("π", onClick = { onAction(CalculatorAction.NumberPi) })
            // Tambah lagi sesuai keinginan
        }

        Calculator(
            state = state,
            history = history,
            onAction = onAction,
            buttonSpacing = 8.dp,
            modifier = Modifier.weight(2f)
        )
    }
}