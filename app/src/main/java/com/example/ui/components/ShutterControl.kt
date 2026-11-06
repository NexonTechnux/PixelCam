package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.CameraMode
import com.example.model.CloudSyncStatus
import com.example.model.MediaItem
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelRed
import com.example.ui.theme.PixelYellow
import java.io.File

@Composable
fun ShutterControl(
    mode: CameraMode,
    isRecordingVideo: Boolean,
    recordingDurationSeconds: Int,
    isNightProcessing: Boolean,
    nightCountdown: Int,
    latestMedia: MediaItem?,
    onShutterClick: () -> Unit,
    onSwitchCamera: () -> Unit,
    onOpenGallery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Video recording timer pill overlay
        if (isRecordingVideo) {
            val pulseTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by pulseTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_alpha"
            )

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xCC000000))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("video_recording_timer"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(PixelRed.copy(alpha = pulseAlpha))
                )
                val mins = recordingDurationSeconds / 60
                val secs = recordingDurationSeconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Night mode countdown
        if (isNightProcessing) {
            Text(
                text = "Tieni fermo... ${nightCountdown}s",
                color = PixelYellow,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x99000000))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        // Bottom action row: [Gallery Preview] --- [Shutter Button] --- [Switch Camera]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Gallery Thumbnail Preview
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF26282E))
                    .border(2.dp, Color(0x44FFFFFF), CircleShape)
                    .clickable { onOpenGallery() }
                    .testTag("gallery_thumbnail_btn"),
                contentAlignment = Alignment.Center
            ) {
                if (latestMedia != null && File(latestMedia.filePath).exists()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(latestMedia.filePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Anteprima galleria",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                    )

                    // Small cloud sync indicator badge
                    if (latestMedia.cloudSyncStatus == CloudSyncStatus.SYNCED) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(2.dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(PixelGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = "Sincronizzato su Google",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp).align(Alignment.Center)
                            )
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Galleria",
                        tint = Color(0x99FFFFFF),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            // Center: Pixel Camera Shutter Button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(3.dp, Color.White, CircleShape)
                    .clickable { onShutterClick() }
                    .testTag("main_shutter_button"),
                contentAlignment = Alignment.Center
            ) {
                if (isNightProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(68.dp),
                        color = PixelYellow,
                        strokeWidth = 4.dp
                    )
                } else if (mode == CameraMode.VIDEO) {
                    if (isRecordingVideo) {
                        // Stop button: red square with rounded corners
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PixelRed)
                        )
                    } else {
                        // Record button: red circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PixelRed)
                        )
                    }
                } else {
                    // Photo shutter: solid white circle with subtle inner ring
                    Box(
                        modifier = Modifier
                            .size(66.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Right: Flip Camera (front/back)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF26282E))
                    .clickable { onSwitchCamera() }
                    .testTag("switch_camera_btn"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Cambia fotocamera",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
