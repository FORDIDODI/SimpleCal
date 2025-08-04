package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.simplecal.ui.theme.MediumGray

@Composable
fun CalculatorBasicLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit
) {
    Calculator(
        state = state,
        history = history,
        onAction = onAction,
        buttonSpacing = 8.dp,
        modifier = Modifier
            .fillMaxSize()
            .background(MediumGray)
            .padding(25.dp)
    )
}
