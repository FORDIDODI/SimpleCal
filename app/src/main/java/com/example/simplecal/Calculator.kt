package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecal.ui.theme.LightGray
import com.example.simplecal.ui.theme.Orange

@Composable
fun Calculator(
    state: CalculatorState,
    history: List<String>,
    buttonSpacing: Dp = 8.dp,
    modifier: Modifier = Modifier,
    onAction: (CalculatorAction) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            // History tetap, tapi hasil utama tampil di bawahnya, tidak overlap
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f),
                reverseLayout = true
            ) {
                items(history) { entry ->
                    Text(
                        text = entry,
                        color = Color.LightGray,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            // Hasil utama, font size adaptif, maxLines 1, overflow ellipsis
            Text(
                text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                fontWeight = FontWeight.Light,
                fontSize = 48.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Grid tombol basic, selalu 4 kolom (portrait) atau 5 kolom (landscape)
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
            val buttonRows = if (isPortrait) 5 else 4
            val buttonCols = if (isPortrait) 4 else 5
            val buttons = listOf(
                listOf("C", "%", "Del", "/"),
                listOf("7", "8", "9", "x"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("±", "0", ".", "=")
            )
            val actions = listOf(
                listOf(
                    { onAction(CalculatorAction.Clear) },
                    { onAction(CalculatorAction.Percent) },
                    { onAction(CalculatorAction.Delete) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }
                ),
                listOf(
                    { onAction(CalculatorAction.Number(7)) },
                    { onAction(CalculatorAction.Number(8)) },
                    { onAction(CalculatorAction.Number(9)) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) }
                ),
                listOf(
                    { onAction(CalculatorAction.Number(4)) },
                    { onAction(CalculatorAction.Number(5)) },
                    { onAction(CalculatorAction.Number(6)) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) }
                ),
                listOf(
                    { onAction(CalculatorAction.Number(1)) },
                    { onAction(CalculatorAction.Number(2)) },
                    { onAction(CalculatorAction.Number(3)) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Add)) }
                ),
                listOf(
                    { onAction(CalculatorAction.ToggleSign) },
                    { onAction(CalculatorAction.Number(0)) },
                    { onAction(CalculatorAction.Decimal) },
                    { onAction(CalculatorAction.Calculate) }
                )
            )
            for (row in 0 until buttonRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    for (col in 0 until buttonCols) {
                        val label = buttons.getOrNull(row)?.getOrNull(col) ?: ""
                        val action = actions.getOrNull(row)?.getOrNull(col) ?: {}
                        val color = when (label) {
                            "C", "Del" -> LightGray
                            "%", "/", "x", "-", "+", "=" -> Orange
                            else -> Color.DarkGray
                        }
                        CalculatorButton(
                            symbol = label,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .weight(1f)
                                .background(color),
                            onClick = action
                        )
                    }
                }
            }
        }
    }
}