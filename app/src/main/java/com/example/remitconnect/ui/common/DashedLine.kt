package com.example.remitconnect.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DashedLine(
    color: Color,
    strokeWidth: Float = 1f,
    dashLength: Float = 10f,
    gapLength: Float = 10f
) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .size(1.dp)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f),
            cap = StrokeCap.Round
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DashedLinePreview() {
    DashedLine(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
}
