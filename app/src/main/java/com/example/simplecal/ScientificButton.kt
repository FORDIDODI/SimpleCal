package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScientificButton(
    symbol: String,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false,
    onClick: () -> Unit
) {
    val buttonColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                color = buttonColor,
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = 1.dp,
                color = if (isHighlighted) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            color = textColor,
            maxLines = 1
        )
    }
}
