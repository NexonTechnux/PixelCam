package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelYellow
import kotlin.math.roundToInt

@Composable
fun HorizonLevelOverlay(
    rollAngle: Float,
    isLevel: Boolean,
    modifier: Modifier = Modifier
) {
    val levelColor = if (isLevel) PixelGreen else PixelYellow.copy(alpha = 0.85f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("horizon_level_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val lineLength = 120.dp.toPx()
            val gap = 18.dp.toPx()

            // Left static level guide
            drawLine(
                color = Color(0x66FFFFFF),
                start = Offset(center.x - lineLength - gap, center.y),
                end = Offset(center.x - gap, center.y),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Right static level guide
            drawLine(
                color = Color(0x66FFFFFF),
                start = Offset(center.x + gap, center.y),
                end = Offset(center.x + lineLength + gap, center.y),
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Center static crosshair dot
            drawCircle(
                color = if (isLevel) PixelGreen else Color.White,
                radius = 3.dp.toPx(),
                center = center
            )

            // Dynamic rotating horizon bar
            rotate(degrees = -rollAngle, pivot = center) {
                // Dynamic line left
                drawLine(
                    color = levelColor,
                    start = Offset(center.x - lineLength - gap, center.y),
                    end = Offset(center.x - gap, center.y),
                    strokeWidth = if (isLevel) 2.5.dp.toPx() else 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Dynamic line right
                drawLine(
                    color = levelColor,
                    start = Offset(center.x + gap, center.y),
                    end = Offset(center.x + lineLength + gap, center.y),
                    strokeWidth = if (isLevel) 2.5.dp.toPx() else 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Show angle readout if slightly tilted
        val angleInt = rollAngle.roundToInt()
        if (kotlin.math.abs(angleInt) in 1..45) {
            Text(
                text = "${angleInt}°",
                color = PixelYellow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
