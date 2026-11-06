package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelRed
import com.example.ui.theme.PixelYellow

@Composable
fun AudioMeter(
    levelFraction: Float,
    modifier: Modifier = Modifier
) {
    val animatedLevel by animateFloatAsState(targetValue = levelFraction.coerceIn(0f, 1f), label = "vu_level")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x99101216))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("audio_vu_meter"),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Audio input",
            tint = Color.White,
            modifier = Modifier.size(14.dp)
        )

        // VU meter bar
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF282A30))
        ) {
            val barColor = when {
                animatedLevel > 0.85f -> PixelRed
                animatedLevel > 0.65f -> PixelYellow
                else -> PixelGreen
            }

            Box(
                modifier = Modifier
                    .width((48 * animatedLevel).dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}
