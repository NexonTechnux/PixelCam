package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MotionPhotosOn
import androidx.compose.material.icons.filled.RawOn
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AntiBandingMode
import com.example.model.CameraSettings
import com.example.model.GridType
import com.example.model.VideoResolution
import com.example.ui.theme.PixelBlue
import com.example.ui.theme.PixelYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    settings: CameraSettings,
    onSettingsChanged: (CameraSettings) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF16181D),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(top = 16.dp, bottom = 24.dp)
                .testTag("settings_bottom_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PixelYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = PixelYellow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Impostazioni Fotocamera",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "PixelCam Pro • Galaxy A35 Edition",
                            color = Color(0x99FFFFFF),
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = Color.White
                    )
                }
            }

            Divider(color = Color(0x22FFFFFF), modifier = Modifier.padding(vertical = 8.dp))

            // Settings List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECTION: VIDEO RESOLUTION
                item {
                    SectionHeader("RISOLUZIONE VIDEO")
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF20232A))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        VideoResolution.values().forEach { res ->
                            val isSelected = settings.videoResolution == res
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFF2E333D) else Color.Transparent)
                                    .clickable { onSettingsChanged(settings.copy(videoResolution = res)) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = null,
                                        tint = if (isSelected) PixelYellow else Color(0x88FFFFFF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = res.label,
                                            color = Color.White,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = res.description,
                                            color = Color(0x88FFFFFF),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Text("ATTIVO", color = PixelYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // SECTION: FORMATTI & SCATTO AVANZATO
                item {
                    SectionHeader("FORMATI & INTELLIGENZA ARTIFICIALE")
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF20232A))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        SettingSwitchItem(
                            icon = Icons.Default.RawOn,
                            title = "Scatto RAW (DNG) + JPEG",
                            subtitle = "Salva dati grezzi non compressi per post-produzione",
                            checked = settings.isRawCaptureEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isRawCaptureEnabled = it)) }
                        )
                        Divider(color = Color(0x11FFFFFF))
                        SettingSwitchItem(
                            icon = Icons.Default.HighQuality,
                            title = "Modalità Sensore 50 MP",
                            subtitle = "Esporta foto alla massima risoluzione reale",
                            checked = settings.isHighRes50MpEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isHighRes50MpEnabled = it)) }
                        )
                        Divider(color = Color(0x11FFFFFF))
                        SettingSwitchItem(
                            icon = Icons.Default.AutoAwesome,
                            title = "Perfezione AI (HDR & Nitidezza)",
                            subtitle = "Migliora automaticamente ombre, volti e colori con AI",
                            checked = settings.isAiEnhanceEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isAiEnhanceEnabled = it)) }
                        )
                        Divider(color = Color(0x11FFFFFF))
                        SettingSwitchItem(
                            icon = Icons.Default.MotionPhotosOn,
                            title = "Foto in Movimento (Live Photo)",
                            subtitle = "Cattura 3 secondi di video attorno ad ogni scatto",
                            checked = settings.isLivePhotoEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isLivePhotoEnabled = it)) }
                        )
                    }
                }

                // SECTION: COMPOSIZIONE & ASSISTENZA
                item {
                    SectionHeader("COMPOSIZIONE & LIVELLA")
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF20232A))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        SettingSwitchItem(
                            icon = Icons.Default.Straighten,
                            title = "Livella a bolla (Indicatore Orizzonte)",
                            subtitle = "Guida visiva precisa con blocco verde al livello perfetto",
                            checked = settings.isLevelMeterEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isLevelMeterEnabled = it)) }
                        )
                        Divider(color = Color(0x11FFFFFF))
                        SettingSwitchItem(
                            icon = Icons.Default.Camera,
                            title = "Stabilizzazione Video (EIS)",
                            subtitle = "Movimenti ultra fluidi con stabilizzazione elettronica",
                            checked = settings.isVideoStabilizationEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isVideoStabilizationEnabled = it)) }
                        )
                        Divider(color = Color(0x11FFFFFF))
                        SettingSwitchItem(
                            icon = Icons.Default.Mic,
                            title = "Misuratore Audio in Tempo Reale",
                            subtitle = "Mostra indicatore VU meter durante i video",
                            checked = settings.isAudioLevelMeterEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isAudioLevelMeterEnabled = it)) }
                        )
                    }
                }

                // SECTION: GRIGLIA
                item {
                    SectionHeader("TIPO DI GRIGLIA")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GridType.values().forEach { gt ->
                            val isSel = settings.gridType == gt
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) PixelYellow else Color(0xFF20232A))
                                    .clickable { onSettingsChanged(settings.copy(gridType = gt)) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (gt) {
                                        GridType.NONE -> "Off"
                                        GridType.RULE_OF_THIRDS -> "3x3"
                                        GridType.GOLDEN_RATIO -> "Aurea"
                                        GridType.CENTER_CROSS -> "Croce"
                                    },
                                    color = if (isSel) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // SECTION: CLOUD BACKUP
                item {
                    SectionHeader("ARCHIVIAZIONE CLOUD")
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF20232A))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        SettingSwitchItem(
                            icon = Icons.Default.CloudDone,
                            title = "Backup Automatico Google Cloud",
                            subtitle = "Salva foto e video su Google Foto e Google Drive",
                            checked = settings.cloudSyncEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(cloudSyncEnabled = it)) }
                        )
                    }
                }

                // SECTION: PERSONALIZZAZIONE
                item {
                    SectionHeader("FEEDBACK & FILIGRANA")
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF20232A))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        SettingSwitchItem(
                            icon = Icons.Default.VolumeUp,
                            title = "Suono Otturatore",
                            subtitle = "Riproduci suono al click dello scatto",
                            checked = settings.isShutterSoundEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isShutterSoundEnabled = it)) }
                        )
                        Divider(color = Color(0x11FFFFFF))
                        SettingSwitchItem(
                            icon = Icons.Default.Camera,
                            title = "Filigrana Galaxy A35",
                            subtitle = "Aggiungi 'Scattato su Galaxy A35 • PixelCam'",
                            checked = settings.isWatermarkEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(isWatermarkEnabled = it)) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0x77FFFFFF),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) PixelYellow else Color(0x88FFFFFF),
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = Color(0x88FFFFFF),
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = PixelYellow,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF383B44)
            )
        )
    }
}
