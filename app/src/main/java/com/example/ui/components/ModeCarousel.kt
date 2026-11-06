package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraMode
import com.example.ui.theme.PixelYellow

@Composable
fun ModeCarousel(
    currentMode: CameraMode,
    onModeSelected: (CameraMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = CameraMode.values()
    val listState = rememberLazyListState()

    LaunchedEffect(currentMode) {
        val index = modes.indexOf(currentMode).coerceAtLeast(0)
        listState.animateScrollToItem(index)
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .testTag("mode_carousel"),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(modes) { mode ->
            val isSelected = mode == currentMode
            Column(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onModeSelected(mode)
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("mode_tab_${mode.name}"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = mode.label,
                    color = if (isSelected) PixelYellow else Color(0x99FFFFFF),
                    fontSize = if (isSelected) 13.sp else 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    letterSpacing = 0.8.sp
                )

                // Pixel style active dot
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) PixelYellow else Color.Transparent)
                )
            }
        }
    }
}
