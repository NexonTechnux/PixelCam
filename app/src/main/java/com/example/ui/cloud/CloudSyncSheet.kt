package com.example.ui.cloud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cloud.CloudAccount
import com.example.model.CloudBackupService
import com.example.ui.theme.PixelBlue
import com.example.ui.theme.PixelGreen
import com.example.ui.theme.PixelYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudSyncSheet(
    account: CloudAccount,
    isSyncing: Boolean,
    syncProgress: Float,
    onToggleAutoBackup: (Boolean) -> Unit,
    onSelectService: (CloudBackupService) -> Unit,
    onSyncAll: () -> Unit,
    onToggleSignIn: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF16181D)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("cloud_sync_bottom_sheet"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(PixelBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = PixelBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Backup Google Cloud",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Salvataggio nativo su Google Foto & Drive",
                            color = Color(0x99FFFFFF),
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi", tint = Color.White)
                }
            }

            // Google Account Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF20232A))
                    .padding(16.dp)
            ) {
                if (account.isSignedIn) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(PixelBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = account.displayName.take(1).uppercase(),
                                        color = Color.Black,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text(
                                        text = account.displayName,
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = account.email,
                                        color = Color(0x99FFFFFF),
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PixelGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Connesso",
                                    color = PixelGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Divider(color = Color(0x11FFFFFF))

                        // Storage Usage Meter
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Spazio Google One",
                                    color = Color(0x88FFFFFF),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${String.format("%.1f", account.usedStorageGb)} GB di ${account.totalStorageGb.toInt()} GB usati",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            LinearProgressIndicator(
                                progress = { (account.usedStorageGb / account.totalStorageGb).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PixelBlue,
                                trackColor = Color(0xFF2F323A)
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                            Text("Accedi con Google", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onToggleSignIn,
                            colors = ButtonDefaults.buttonColors(containerColor = PixelYellow, contentColor = Color.Black)
                        ) {
                            Text("Accedi")
                        }
                    }
                }
            }

            // Destination service (Google Foto vs Google Drive)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "DESTINAZIONE DI SALVATAGGIO",
                    color = Color(0x77FFFFFF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CloudBackupService.values().forEach { srv ->
                        val isChosen = account.activeService == srv
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isChosen) Color(0xFF262E3D) else Color(0xFF20232A))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (srv == CloudBackupService.GOOGLE_PHOTOS) Icons.Default.PhotoLibrary else Icons.Default.CloudQueue,
                                        contentDescription = null,
                                        tint = if (isChosen) PixelBlue else Color(0x88FFFFFF),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = srv.label,
                                        color = if (isChosen) Color.White else Color(0x88FFFFFF),
                                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                }
                                if (isChosen) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PixelBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Auto-backup toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF20232A))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Backup Automatico", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Carica automaticamente nuovi scatti e video", color = Color(0x88FFFFFF), fontSize = 11.sp)
                }

                Switch(
                    checked = account.autoBackupEnabled,
                    onCheckedChange = onToggleAutoBackup,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = PixelYellow
                    )
                )
            }

            // Sync All Button
            Button(
                onClick = onSyncAll,
                enabled = !isSyncing && account.isSignedIn,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PixelYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("sync_all_now_btn")
            ) {
                if (isSyncing) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                        Text("Sincronizzazione in corso... ${(syncProgress * 100).toInt()}%", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text("Sincronizza ora tutti i contenuti", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
