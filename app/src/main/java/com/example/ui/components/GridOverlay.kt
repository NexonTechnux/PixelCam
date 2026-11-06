package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.GridType

@Composable
fun GridOverlay(
    gridType: GridType,
    modifier: Modifier = Modifier
) {
    if (gridType == GridType.NONE) return

    val gridColor = Color(0x33FFFFFF)

    Canvas(modifier = modifier.fillMaxSize().testTag("viewfinder_grid_overlay")) {
        val w = size.width
        val h = size.height

        when (gridType) {
            GridType.RULE_OF_THIRDS -> {
                // Vertical lines at 1/3 and 2/3
                drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), 1.dp.toPx())
                drawLine(gridColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), 1.dp.toPx())
                // Horizontal lines at 1/3 and 2/3
                drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), 1.dp.toPx())
                drawLine(gridColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), 1.dp.toPx())
            }
            GridType.GOLDEN_RATIO -> {
                val phi = 0.618f
                val v1 = w * (1f - phi)
                val v2 = w * phi
                val h1 = h * (1f - phi)
                val h2 = h * phi

                drawLine(gridColor, Offset(v1, 0f), Offset(v1, h), 1.dp.toPx())
                drawLine(gridColor, Offset(v2, 0f), Offset(v2, h), 1.dp.toPx())
                drawLine(gridColor, Offset(0f, h1), Offset(w, h1), 1.dp.toPx())
                drawLine(gridColor, Offset(0f, h2), Offset(w, h2), 1.dp.toPx())
            }
            GridType.CENTER_CROSS -> {
                val crossLen = 24.dp.toPx()
                drawLine(gridColor, Offset(w / 2f - crossLen, h / 2f), Offset(w / 2f + crossLen, h / 2f), 1.5.dp.toPx())
                drawLine(gridColor, Offset(w / 2f, h / 2f - crossLen), Offset(w / 2f, h / 2f + crossLen), 1.5.dp.toPx())
            }
            GridType.NONE -> {}
        }
    }
}
