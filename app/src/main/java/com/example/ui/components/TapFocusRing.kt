package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TapFocusState
import com.example.ui.theme.PixelYellow
import kotlin.math.roundToInt

@Composable
fun TapFocusRing(
    tapState: TapFocusState,
    screenWidthPx: Float,
    screenHeightPx: Float,
    onExposureChanged: (Float) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scaleAnim = remember { Animatable(1.35f) }
    val alphaAnim = remember { Animatable(1.0f) }

    LaunchedEffect(tapState.timestamp) {
        scaleAnim.snapTo(1.35f)
        alphaAnim.snapTo(1.0f)
        scaleAnim.animateTo(1.0f, animationSpec = tween(220))
    }

    val density = LocalDensity.current
    val ringSizeDp = 76.dp
    val ringSizePx = with(density) { ringSizeDp.toPx() }

    val centerX = tapState.xPercent * screenWidthPx
    val centerY = tapState.yPercent * screenHeightPx

    val leftPx = (centerX - ringSizePx / 2f).coerceIn(10f, screenWidthPx - ringSizePx - 40f)
    val topPx = (centerY - ringSizePx / 2f).coerceIn(10f, screenHeightPx - ringSizePx - 20f)

    Box(modifier = modifier.fillMaxSize()) {
        // Focus Ring
        Box(
            modifier = Modifier
                .offset { IntOffset(leftPx.roundToInt(), topPx.roundToInt()) }
                .size(ringSizeDp)
                .testTag("tap_focus_ring")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val currentScale = scaleAnim.value
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.width / 2f - 4f) * currentScale

                // Outer circle
                drawCircle(
                    color = PixelYellow.copy(alpha = alphaAnim.value * 0.9f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                // 4 Focus tick marks
                val tickLen = 6.dp.toPx()
                // Top
                drawLine(
                    color = PixelYellow,
                    start = Offset(center.x, center.y - radius - 2.dp.toPx()),
                    end = Offset(center.x, center.y - radius + tickLen),
                    strokeWidth = 2.dp.toPx()
                )
                // Bottom
                drawLine(
                    color = PixelYellow,
                    start = Offset(center.x, center.y + radius + 2.dp.toPx()),
                    end = Offset(center.x, center.y + radius - tickLen),
                    strokeWidth = 2.dp.toPx()
                )
                // Left
                drawLine(
                    color = PixelYellow,
                    start = Offset(center.x - radius - 2.dp.toPx(), center.y),
                    end = Offset(center.x - radius + tickLen, center.y),
                    strokeWidth = 2.dp.toPx()
                )
                // Right
                drawLine(
                    color = PixelYellow,
                    start = Offset(center.x + radius + 2.dp.toPx(), center.y),
                    end = Offset(center.x + radius - tickLen, center.y),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Vertical Exposure / Brightness Slider next to the ring
        val sliderX = leftPx + ringSizePx + 8f
        val sliderY = topPx - 20f

        Box(
            modifier = Modifier
                .offset { IntOffset(sliderX.roundToInt(), sliderY.roundToInt()) }
                .size(width = 44.dp, height = 120.dp)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        // Dragging up increases EV, dragging down decreases EV
                        val deltaEv = -dragAmount / 25f
                        val newEv = (tapState.exposureValue + deltaEv).coerceIn(-3.0f, 3.0f)
                        onExposureChanged(newEv)
                    }
                }
                .testTag("exposure_brightness_slider"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background track
                val lineX = size.width / 2f
                drawLine(
                    color = Color(0x66FFFFFF),
                    start = Offset(lineX, 10f),
                    end = Offset(lineX, size.height - 10f),
                    strokeWidth = 2.dp.toPx()
                )

                // Center zero notch
                drawLine(
                    color = Color.White,
                    start = Offset(lineX - 4.dp.toPx(), size.height / 2f),
                    end = Offset(lineX + 4.dp.toPx(), size.height / 2f),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Sun icon positioned according to exposureValue
            val evFraction = (tapState.exposureValue + 3.0f) / 6.0f // 0 to 1
            // Inverted for vertical (1 is top, 0 is bottom)
            val iconOffsetYDp = with(density) { (1f - evFraction) * 80f }.dp

            Box(
                modifier = Modifier
                    .offset(y = iconOffsetYDp - 40.dp)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Regola luminosità",
                    tint = PixelYellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            // EV numerical indicator
            if (kotlin.math.abs(tapState.exposureValue) > 0.1f) {
                Text(
                    text = String.format("%+.1f", tapState.exposureValue),
                    color = PixelYellow,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}
