package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simplecal.ui.theme.MediumGray

@Composable
fun CalculatorBasicLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Tombol pengganti orientasi ke scientific
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onToggleMode) {
                Icon(
                    imageVector = Icons.Filled.Straighten, // ikon penggaris
                    contentDescription = "Scientific Mode",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onToggleMode) {
                Icon(
                    imageVector = Icons.Filled.Calculate, // ikon kalkulator
                    contentDescription = "Basic Mode",
                    tint = Color.White
                )
            }
        }

        // Layout kalkulator basic biasa
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
}
