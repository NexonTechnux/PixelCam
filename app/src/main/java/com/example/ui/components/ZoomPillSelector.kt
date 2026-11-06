package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ZoomLens
import com.example.ui.theme.PixelYellow

@Composable
fun ZoomPillSelector(
    selectedLens: ZoomLens,
    currentZoomRatio: Float,
    onLensSelected: (ZoomLens) -> Unit,
    modifier: Modifier = Modifier
) {
    val lenses = ZoomLens.values()
    val buttonWidth = 42.dp
    val spacing = 4.dp
    val padding = 4.dp

    val selectedIndex = lenses.indexOf(selectedLens).coerceAtLeast(0)
    val targetOffset = padding + (buttonWidth + spacing) * selectedIndex
    val animatedOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "zoom_pill_offset"
    )

    Box(
        modifier = modifier
            .testTag("zoom_selector_container")
            .clip(RoundedCornerShape(26.dp))
            .background(Color(0x991E2025))
            .padding(padding)
    ) {
        // Sliding highlight circle
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .size(buttonWidth)
                .clip(CircleShape)
                .background(Color(0xFF32353E))
        )

        // The 3 circles: .5, 1x, 2x
        Row {
            lenses.forEachIndexed { index, lens ->
                val isSelected = selectedLens == lens
                Box(
                    modifier = Modifier
                        .size(buttonWidth)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onLensSelected(lens)
                        }
                        .testTag("zoom_button_${lens.label}"),
                    contentAlignment = Alignment.Center
                ) {
                    val displayText = if (isSelected && kotlin.math.abs(currentZoomRatio - lens.ratio) > 0.1f) {
                        String.format("%.1fx", currentZoomRatio)
                    } else {
                        lens.label
                    }

                    Text(
                        text = displayText,
                        color = if (isSelected) PixelYellow else Color(0xFFD0D2D8),
                        fontSize = if (isSelected) 13.sp else 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
                if (index < lenses.size - 1) {
                    Box(modifier = Modifier.width(spacing))
                }
            }
        }
    }
}
