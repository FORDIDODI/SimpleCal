package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.simplecal.ui.theme.MediumGray
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun CalculatorScientificLayout(
    state: CalculatorState,
    history: List<String>,
    onAction : (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MediumGray)
            .padding(8.dp)
    ) {
        // Baris tombol toggle mode
        Row(
            modifier = Modifier.fillMaxWidth(),
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
        Spacer(modifier = Modifier.height(8.dp))
        if (isPortrait) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Scientific di kiri (1 kolom)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificButton("sin", onClick = { onAction(CalculatorAction.Sin) })
                    ScientificButton("cos", onClick = { onAction(CalculatorAction.Cos) })
                    ScientificButton("tan", onClick = { onAction(CalculatorAction.Tan) })
                    ScientificButton("ln", onClick = { onAction(CalculatorAction.Ln) })
                    ScientificButton("log", onClick = { onAction(CalculatorAction.Log) })
                    ScientificButton("xʸ", onClick = { onAction(CalculatorAction.Power) })
                    ScientificButton("1/x", onClick = { onAction(CalculatorAction.Reciprocal) })
                }
                // Basic di kanan (4 kolom)
                Calculator(
                    state = state,
                    history = history,
                    onAction = onAction,
                    buttonSpacing = 8.dp,
                    modifier = Modifier.weight(3f),
                    viewModel = viewModel
                )
            }
        } else {
            // Landscape: scientific tetap di kiri (kolom), basic di kanan (4 kolom)
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ScientificButton("sin", onClick = { onAction(CalculatorAction.Sin) })
                    ScientificButton("cos", onClick = { onAction(CalculatorAction.Cos) })
                    ScientificButton("tan", onClick = { onAction(CalculatorAction.Tan) })
                    ScientificButton("ln", onClick = { onAction(CalculatorAction.Ln) })
                    ScientificButton("log", onClick = { onAction(CalculatorAction.Log) })
                    ScientificButton("xʸ", onClick = { onAction(CalculatorAction.Power) })
                    ScientificButton("1/x", onClick = { onAction(CalculatorAction.Reciprocal) })
                }
                Calculator(
                    state = state,
                    history = history,
                    onAction = onAction,
                    buttonSpacing = 8.dp,
                    modifier = Modifier.weight(3f),
                    viewModel = viewModel
                )
            }
        }
    }
}