package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CameraMode
import com.example.model.VideoResolution
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelYellow

@Composable
fun FpsBadge(
    fps: Float,
    mode: CameraMode,
    resolution: VideoResolution,
    isNightExtensionActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .testTag("fps_badge_container")
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x99101216))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Green dot indicator
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (fps >= 25f) PixelGreen else PixelYellow)
        )

        // FPS text (live preview only, not stamped into videos)
        Text(
            text = "${String.format("%.1f", fps)} FPS",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.testTag("fps_counter_text")
        )

        Text(
            text = "•",
            color = Color(0x66FFFFFF),
            fontSize = 12.sp
        )

        // Mode / Resolution label
        val label = when (mode) {
            CameraMode.VIDEO -> resolution.label
            CameraMode.NIGHT -> if (isNightExtensionActive) "Notte Nativa A35" else "Notte AI"
            CameraMode.PRO -> "RAW • PRO"
            CameraMode.PORTRAIT -> "Ritratto 50MP"
            CameraMode.PHOTO -> "HDR+ 50MP"
        }

        Text(
            text = label,
            color = Color(0xFFE2E2E6),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
