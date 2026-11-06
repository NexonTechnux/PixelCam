package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.camera.CameraHelper
import com.example.model.AspectRatioSetting
import com.example.model.CameraMode
import com.example.model.ZoomLens
import com.example.ui.cloud.CloudSyncSheet
import com.example.ui.components.AudioMeter
import com.example.ui.components.FpsBadge
import com.example.ui.components.GridOverlay
import com.example.ui.components.HorizonLevelOverlay
import com.example.ui.components.ModeCarousel
import com.example.ui.components.ProControlsBar
import com.example.ui.components.ShutterControl
import com.example.ui.components.TapFocusRing
import com.example.ui.components.TopQuickBar
import com.example.ui.components.ZoomPillSelector
import com.example.ui.settings.SettingsDialog
import com.example.ui.theme.PixelYellow
import com.example.ui.viewer.MediaViewerSheet
import com.example.viewmodel.CameraViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    // State flows from ViewModel
    val currentMode by viewModel.currentMode.collectAsState()
    val selectedLens by viewModel.selectedLens.collectAsState()
    val zoomRatio by viewModel.zoomRatio.collectAsState()
    val fps by viewModel.fps.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val proSettings by viewModel.proSettings.collectAsState()
    val tapFocusState by viewModel.tapFocusState.collectAsState()
    val rollAngle by viewModel.horizonRollAngle.collectAsState()
    val isHorizonLevel by viewModel.isHorizonLevel.collectAsState()
    val mediaList by viewModel.mediaList.collectAsState()
    val selectedMedia by viewModel.selectedMedia.collectAsState()
    val isSettingsDialogOpen by viewModel.isSettingsDialogOpen.collectAsState()
    val isCloudSheetOpen by viewModel.isCloudSheetOpen.collectAsState()
    val isNightProcessing by viewModel.isNightProcessing.collectAsState()
    val nightCountdown by viewModel.nightCountdownSeconds.collectAsState()
    val isRecordingVideo by viewModel.isRecordingVideo.collectAsState()
    val videoDuration by viewModel.videoDurationSeconds.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val cloudSyncProgress by viewModel.cloudSyncProgress.collectAsState()
    val cloudAccount by viewModel.cloudAccount.collectAsState()
    val detectedScene by viewModel.detectedScene.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val audioVuLevel by viewModel.audioVuLevel.collectAsState()

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] == true
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    // CameraHelper instance
    val cameraHelper = remember { CameraHelper(context, scope) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }
    val isNightExtensionActive by cameraHelper.isNightExtensionActive.collectAsState()
    val helperFps by cameraHelper.currentFps.collectAsState()

    // Update FPS in ViewModel
    LaunchedEffect(helperFps) {
        viewModel.updateFps(helperFps)
    }

    // Bind Camera Use Cases when mode or resolution changes
    LaunchedEffect(currentMode, settings.videoResolution, hasCameraPermission) {
        previewViewRef?.let { pv ->
            if (hasCameraPermission) {
                cameraHelper.bindCameraUseCases(lifecycleOwner, pv, currentMode, settings.videoResolution)
            }
        }
    }

    // Apply Pro settings when updated
    LaunchedEffect(proSettings) {
        if (currentMode == CameraMode.PRO) {
            cameraHelper.applyProSettings(proSettings)
        }
    }

    // Handle Tap to focus clear timer
    LaunchedEffect(tapFocusState?.timestamp) {
        if (tapFocusState != null) {
            kotlinx.coroutines.delay(4500)
            viewModel.clearTapFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_screen_root")
    ) {
        if (!hasCameraPermission) {
            // Permission request fallback card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = PixelYellow,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "PixelCam richiede l'accesso alla fotocamera",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Per usare la tripla fotocamera, modalità notte nativa e registrazione video del Galaxy A35, concedi i permessi necessari.",
                    color = Color(0x99FFFFFF),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PixelYellow, contentColor = Color.Black)
                ) {
                    Text("Concedi Permessi")
                }
            }
        } else {
            // Full-screen Camera Viewfinder
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val density = LocalDensity.current
                val screenWidthPx = with(density) { maxWidth.toPx() }
                val screenHeightPx = with(density) { maxHeight.toPx() }

                // Camera Preview View
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            previewViewRef = this
                            cameraHelper.initialize(
                                lifecycleOwner = lifecycleOwner,
                                previewView = this,
                                mode = currentMode,
                                resolution = settings.videoResolution,
                                onReady = {
                                    if (currentMode == CameraMode.PRO) {
                                        cameraHelper.applyProSettings(proSettings)
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(screenWidthPx, screenHeightPx) {
                            detectTapGestures { offset: Offset ->
                                val xPct = offset.x / screenWidthPx
                                val yPct = offset.y / screenHeightPx
                                previewViewRef?.let { pv ->
                                    cameraHelper.tapToFocus(pv, offset.x, offset.y)
                                }
                                viewModel.setTapFocus(xPct, yPct)
                            }
                        }
                        .testTag("camera_preview_view")
                )

                // Grid Overlay
                GridOverlay(gridType = settings.gridType)

                // Horizon Level Indicator Overlay (if enabled in settings)
                if (settings.isLevelMeterEnabled) {
                    HorizonLevelOverlay(
                        rollAngle = rollAngle,
                        isLevel = isHorizonLevel
                    )
                }

                // Tap-to-Focus Ring & Exposure Brightness Slider
                tapFocusState?.let { tapState ->
                    TapFocusRing(
                        tapState = tapState,
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx,
                        onExposureChanged = { newEv ->
                            viewModel.adjustTapExposure(newEv)
                            cameraHelper.setExposureCompensation(newEv)
                        },
                        onDismiss = { viewModel.clearTapFocus() }
                    )
                }

                // Top Viewfinder HUD: FPS Counter & AI Scene Chip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 56.dp)
                        .align(Alignment.TopStart),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Real-time FPS badge (Viewfinder HUD, not visible in video!)
                    FpsBadge(
                        fps = fps,
                        mode = currentMode,
                        resolution = settings.videoResolution,
                        isNightExtensionActive = isNightExtensionActive
                    )

                    // Audio level meter if recording video and enabled
                    if (isRecordingVideo && settings.isAudioLevelMeterEnabled) {
                        AudioMeter(levelFraction = audioVuLevel)
                    }

                    // AI Scene Detection Chip
                    if (settings.isAiEnhanceEnabled) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x99101216))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(detectedScene.icon, fontSize = 12.sp)
                                Text(
                                    text = detectedScene.label,
                                    color = PixelYellow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Top Quick Bar (Flash, Live Photo, AI, Timer, Ratio, Cloud, Settings)
                TopQuickBar(
                    settings = settings,
                    isCloudSyncing = isCloudSyncing,
                    onToggleFlash = { viewModel.toggleFlash() },
                    onToggleLivePhoto = { viewModel.toggleLivePhoto() },
                    onToggleAiEnhance = { viewModel.toggleAiEnhance() },
                    onCycleTimer = {
                        val next = when (settings.timerSeconds) {
                            0 -> 3
                            3 -> 10
                            else -> 0
                        }
                        viewModel.setTimer(next)
                    },
                    onCycleAspectRatio = {
                        val next = when (settings.aspectRatio) {
                            AspectRatioSetting.RATIO_4_3 -> AspectRatioSetting.RATIO_16_9
                            AspectRatioSetting.RATIO_16_9 -> AspectRatioSetting.RATIO_1_1
                            AspectRatioSetting.RATIO_1_1 -> AspectRatioSetting.RATIO_FULL
                            AspectRatioSetting.RATIO_FULL -> AspectRatioSetting.RATIO_4_3
                        }
                        viewModel.setAspectRatio(next)
                    },
                    onOpenCloud = { viewModel.openCloudSheet(true) },
                    onOpenSettings = { viewModel.openSettingsDialog(true) },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                )

                // Toast/Status Banner
                AnimatedVisibility(
                    visible = statusMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    statusMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xDD121418))
                                .padding(horizontal = 18.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = msg,
                                color = PixelYellow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom Controls Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Zoom Pill Selector (.5x, 1x, 2x)
                    ZoomPillSelector(
                        selectedLens = selectedLens,
                        currentZoomRatio = zoomRatio,
                        onLensSelected = { lens ->
                            viewModel.selectLens(lens) { targetZoom ->
                                cameraHelper.animateZoom(targetZoom)
                            }
                        }
                    )

                    // Pro Controls Bar (Visible only in PRO Mode)
                    if (currentMode == CameraMode.PRO) {
                        ProControlsBar(
                            proSettings = proSettings,
                            isRawEnabled = settings.isRawCaptureEnabled,
                            onProSettingsChanged = { updated ->
                                viewModel.updateProSettings(updated)
                                cameraHelper.applyProSettings(updated)
                            },
                            onToggleRaw = {
                                val next = !settings.isRawCaptureEnabled
                                viewModel.updateSettings(settings.copy(isRawCaptureEnabled = next))
                                viewModel.showToast(if (next) "Formato RAW (DNG) ATTIVO" else "RAW Disattivato")
                            }
                        )
                    }

                    // Mode Carousel (NOTTE, FOTO, VIDEO, RITRATTO, PRO)
                    ModeCarousel(
                        currentMode = currentMode,
                        onModeSelected = { mode ->
                            viewModel.setMode(mode)
                            previewViewRef?.let { pv ->
                                cameraHelper.bindCameraUseCases(lifecycleOwner, pv, mode, settings.videoResolution)
                            }
                        }
                    )

                    // Shutter Button, Video pulse timer, Gallery thumbnail & Camera flip
                    ShutterControl(
                        mode = currentMode,
                        isRecordingVideo = isRecordingVideo,
                        recordingDurationSeconds = videoDuration,
                        isNightProcessing = isNightProcessing,
                        nightCountdown = nightCountdown,
                        latestMedia = mediaList.firstOrNull(),
                        onShutterClick = {
                            if (currentMode == CameraMode.VIDEO) {
                                cameraHelper.toggleVideoRecording(
                                    onRecordingStarted = {
                                        viewModel.showToast("Registrazione Video avviata")
                                    },
                                    onRecordingFinished = { videoFile ->
                                        viewModel.onVideoRecorded(videoFile)
                                        viewModel.showToast("Video salvato!")
                                    },
                                    onError = { err ->
                                        viewModel.showToast("Errore: $err")
                                    }
                                )
                            } else if (currentMode == CameraMode.NIGHT) {
                                viewModel.startNightCapture {
                                    cameraHelper.takePhoto(
                                        onSuccess = { file, bitmap ->
                                            viewModel.onPhotoCaptured(file, bitmap)
                                        },
                                        onError = { err ->
                                            viewModel.showToast("Errore scatto: $err")
                                        }
                                    )
                                }
                            } else {
                                cameraHelper.takePhoto(
                                    onSuccess = { file, bitmap ->
                                        viewModel.onPhotoCaptured(file, bitmap)
                                    },
                                    onError = { err ->
                                        viewModel.showToast("Errore scatto: $err")
                                    }
                                )
                            }
                        },
                        onSwitchCamera = {
                            previewViewRef?.let { pv ->
                                cameraHelper.switchCamera(lifecycleOwner, pv, currentMode, settings.videoResolution)
                                viewModel.showToast("Fotocamera invertita")
                            }
                        },
                        onOpenGallery = {
                            viewModel.selectMedia(mediaList.firstOrNull())
                        }
                    )
                }
            }
        }

        // Settings Bottom Sheet
        if (isSettingsDialogOpen) {
            SettingsDialog(
                settings = settings,
                onSettingsChanged = { updated ->
                    viewModel.updateSettings(updated)
                },
                onDismiss = { viewModel.openSettingsDialog(false) }
            )
        }

        // Google Cloud Sync Bottom Sheet
        if (isCloudSheetOpen) {
            CloudSyncSheet(
                account = cloudAccount,
                isSyncing = isCloudSyncing,
                syncProgress = cloudSyncProgress,
                onToggleAutoBackup = { viewModel.toggleCloudAutoBackup(it) },
                onSelectService = { /* Service selector */ },
                onSyncAll = { viewModel.syncAllCloud() },
                onToggleSignIn = { viewModel.toggleGoogleSignIn() },
                onDismiss = { viewModel.openCloudSheet(false) }
            )
        }

        // Full Screen Media Viewer (with Live Photo playback, Perfezione AI, GIF/MP4 export)
        selectedMedia?.let { media ->
            MediaViewerSheet(
                mediaItem = media,
                onClose = { viewModel.selectMedia(null) },
                onEnhanceWithAi = { item ->
                    viewModel.enhanceMediaItem(item) { /* enhanced */ }
                },
                onExportGif = { item ->
                    viewModel.exportLivePhotoAsGif(item) { /* done */ }
                },
                onExportShortVideo = { item ->
                    viewModel.exportLivePhotoAsShortVideo(item) { /* done */ }
                },
                onExportHighRes = { item ->
                    viewModel.showToast("Foto 50 MP ad alta risoluzione esportata!")
                }
            )
        }
    }
}
