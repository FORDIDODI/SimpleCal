package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.FontWeight

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.White,
    isDeleteButton: Boolean = false,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit
) {
    val buttonTextColor = if (isDeleteButton) {
        MaterialTheme.colorScheme.onSurface
    } else {
        textColor
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isDeleteButton) 1.dp else 0.dp,
                color = if (isDeleteButton) Color.Yellow else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Text(
            text = symbol,
            fontSize = if (isDeleteButton) 28.sp else 32.sp,
            color = buttonTextColor,
            fontWeight = if (isDeleteButton) FontWeight.Normal else FontWeight.Normal,
            maxLines = 1
        )
    }
}
