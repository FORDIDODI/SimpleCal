package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val buttonColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Black
    }
    
    val borderColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Yellow
    }
    
    val textColor = if (isHighlighted) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        Color.White
    }
    
    Box(
        modifier = modifier
            .width(60.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(buttonColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 20.sp,
            color = textColor,
            maxLines = 1
        )
    }
}
