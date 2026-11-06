package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Timer10
import androidx.compose.material.icons.filled.Timer3
import androidx.compose.material.icons.outlined.MotionPhotosOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioSetting
import com.example.model.CameraSettings
import com.example.model.FlashModeSetting
import com.example.ui.theme.PixelBlue
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelYellow

@Composable
fun TopQuickBar(
    settings: CameraSettings,
    isCloudSyncing: Boolean,
    onToggleFlash: () -> Unit,
    onToggleLivePhoto: () -> Unit,
    onToggleAiEnhance: () -> Unit,
    onCycleTimer: () -> Unit,
    onCycleAspectRatio: () -> Unit,
    onOpenCloud: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flash Mode
        IconButton(
            onClick = onToggleFlash,
            modifier = Modifier.testTag("top_bar_flash_btn")
        ) {
            val icon = when (settings.flashMode) {
                FlashModeSetting.OFF -> Icons.Default.FlashOff
                FlashModeSetting.AUTO -> Icons.Default.FlashAuto
                FlashModeSetting.ON, FlashModeSetting.TORCH -> Icons.Default.FlashOn
            }
            val tint = if (settings.flashMode != FlashModeSetting.OFF) PixelYellow else Color.White
            Icon(imageVector = icon, contentDescription = "Flash", tint = tint)
        }

        // Live Photo / Motion Photo Toggle
        IconButton(
            onClick = onToggleLivePhoto,
            modifier = Modifier.testTag("top_bar_live_photo_btn")
        ) {
            if (settings.isLivePhotoEnabled) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PixelYellow.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MotionPhotosOn,
                        contentDescription = "Live Photo Attiva",
                        tint = PixelYellow
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Outlined.MotionPhotosOff,
                    contentDescription = "Live Photo Disattivata",
                    tint = Color.White
                )
            }
        }

        // Perfezione AI Toggle
        IconButton(
            onClick = onToggleAiEnhance,
            modifier = Modifier.testTag("top_bar_ai_enhance_btn")
        ) {
            if (settings.isAiEnhanceEnabled) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PixelYellow.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Perfezione AI Attiva",
                        tint = PixelYellow
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Perfezione AI Spenta",
                    tint = Color(0x88FFFFFF)
                )
            }
        }

        // Timer
        IconButton(
            onClick = onCycleTimer,
            modifier = Modifier.testTag("top_bar_timer_btn")
        ) {
            val (icon, tint) = when (settings.timerSeconds) {
                3 -> Icons.Default.Timer3 to PixelYellow
                10 -> Icons.Default.Timer10 to PixelYellow
                else -> Icons.Default.Timer to Color.White
            }
            Icon(imageVector = icon, contentDescription = "Timer", tint = tint)
        }

        // Aspect Ratio Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x661E2025))
                .clickable { onCycleAspectRatio() }
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .testTag("top_bar_ratio_btn"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = settings.aspectRatio.label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Google Cloud Sync shortcut
        IconButton(
            onClick = onOpenCloud,
            modifier = Modifier.testTag("top_bar_cloud_btn")
        ) {
            if (isCloudSyncing) {
                Icon(
                    imageVector = Icons.Default.CloudSync,
                    contentDescription = "Sincronizzazione in corso",
                    tint = PixelBlue
                )
            } else {
                Icon(
                    imageVector = Icons.Default.CloudDone,
                    contentDescription = "Google Cloud",
                    tint = if (settings.cloudSyncEnabled) PixelGreen else Color.White
                )
            }
        }

        // Settings gear button
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.testTag("top_bar_settings_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Impostazioni Fotocamera",
                tint = Color.White
            )
        }
    }
}
