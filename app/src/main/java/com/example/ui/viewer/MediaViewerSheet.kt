package com.example.ui.viewer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.RawOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.MediaItem
import com.example.ui.theme.PixelBlue
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelYellow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MediaViewerSheet(
    mediaItem: MediaItem,
    onClose: () -> Unit,
    onEnhanceWithAi: (MediaItem) -> Unit,
    onExportGif: (MediaItem) -> Unit,
    onExportShortVideo: (MediaItem) -> Unit,
    onExportHighRes: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlayingLivePhoto by remember { mutableStateOf(false) }
    var isAiEnhancing by remember { mutableStateOf(false) }
    var showExifInfo by remember { mutableStateOf(false) }
    var splitSliderPosition by remember { mutableStateOf(0.5f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("media_viewer_screen")
    ) {
        // Main Image / Video Poster
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            if (mediaItem.isLivePhoto) {
                                isPlayingLivePhoto = true
                                tryAwaitRelease()
                                isPlayingLivePhoto = false
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(mediaItem.filePath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Visualizzatore media",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Live Photo playing indicator animation
            if (isPlayingLivePhoto) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x99000000))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = PixelYellow)
                        Text(
                            text = "Riproduzione Live Photo (3s)...",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Video Play Icon overlay if video
            if (mediaItem.isVideo && !isPlayingLivePhoto) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Riproduci video",
                        tint = Color.White,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        }

        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x66000000))
                    .testTag("viewer_close_btn")
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Indietro", tint = Color.White)
            }

            // Media Badge (LIVE / RAW / 50MP / VIDEO)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (mediaItem.isLivePhoto) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PixelYellow)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.MotionPhotosOn, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Text("LIVE PHOTO", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (mediaItem.isRawDng) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(PixelBlue)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("RAW DNG", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (mediaItem.isAiEnhanced) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2E333D))
                            .border(1.dp, PixelYellow, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PixelYellow, modifier = Modifier.size(12.dp))
                            Text("AI ENHANCED", color = PixelYellow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            IconButton(
                onClick = { showExifInfo = !showExifInfo },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0x66000000))
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Dettagli EXIF", tint = Color.White)
            }
        }

        // EXIF Details Card
        AnimatedVisibility(
            visible = showExifInfo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 80.dp)
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xEE16181D))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dettagli Scatto Galaxy A35", color = PixelYellow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(mediaItem.timestamp)),
                            color = Color(0x99FFFFFF),
                            fontSize = 11.sp
                        )
                    }
                    Divider(color = Color(0x22FFFFFF))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sensore:", color = Color(0x88FFFFFF), fontSize = 12.sp)
                        Text("50 MP ISOCELL GN3 • f/1.8 OIS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Esposizione & ISO:", color = Color(0x88FFFFFF), fontSize = 12.sp)
                        Text("${mediaItem.exposureTime} • ${mediaItem.iso}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Backup Google Cloud:", color = Color(0x88FFFFFF), fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = PixelGreen, modifier = Modifier.size(14.dp))
                            Text("Sincronizzato", color = PixelGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Bottom Action Bar: [Migliora con AI] | [Esporta GIF] | [Esporta Video MP4] | [50MP Export]
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color(0xCC000000))
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Live Photo special actions
            if (mediaItem.isLivePhoto) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Export as GIF
                    Button(
                        onClick = { onExportGif(mediaItem) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262A32), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_gif_btn")
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Gif, contentDescription = null, tint = PixelYellow, modifier = Modifier.size(20.dp))
                            Text("Esporta GIF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Export as Short Video MP4
                    Button(
                        onClick = { onExportShortVideo(mediaItem) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262A32), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_short_video_btn")
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = null, tint = PixelBlue, modifier = Modifier.size(18.dp))
                            Text("Video MP4 (3s)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Perfezione AI & Alta Risoluzione Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Perfezione AI button
                Button(
                    onClick = {
                        isAiEnhancing = true
                        onEnhanceWithAi(mediaItem)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mediaItem.isAiEnhanced) Color(0xFF383318) else PixelYellow,
                        contentColor = if (mediaItem.isAiEnhanced) PixelYellow else Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("perfezione_ai_btn")
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Text(
                            text = if (mediaItem.isAiEnhanced) "Perfezione AI Attiva ✨" else "Migliora con AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // High Res 50MP export
                Button(
                    onClick = { onExportHighRes(mediaItem) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262A32), contentColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("export_50mp_btn")
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Text("50 MP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
