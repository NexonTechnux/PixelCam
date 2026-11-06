package com.example.model

enum class CameraMode(val label: String) {
    NIGHT("NOTTE"),
    PHOTO("FOTO"),
    VIDEO("VIDEO"),
    PORTRAIT("RITRATTO"),
    PRO("PRO")
}

enum class ZoomLens(val ratio: Float, val label: String, val cameraDescription: String) {
    ULTRA_WIDE(0.5f, ".5", "Ultra-Grandangolo 8MP (123°)"),
    WIDE(1.0f, "1x", "Grandangolo Principale 50MP OIS"),
    TELE_CROP(2.0f, "2x", "Zoom Ottico/Sensore 2x Alta Risoluzione")
}

enum class VideoResolution(val label: String, val fps: Int, val description: String) {
    HD_720P_30("720p 30 fps", 30, "HD Standard fluido"),
    FHD_1080P_30("1080p 30 fps", 30, "Full HD bilanciato"),
    FHD_1080P_60("1080p 60 fps", 60, "Full HD ultra fluido"),
    UHD_4K_30("4K 30 fps", 30, "Ultra HD massima nitidezza")
}

enum class GridType(val label: String) {
    NONE("Disattivata"),
    RULE_OF_THIRDS("3x3 (Regola dei terzi)"),
    GOLDEN_RATIO("Sezione aurea"),
    CENTER_CROSS("Croce centrale")
}

enum class AntiBandingMode(val label: String) {
    AUTO("Automatico"),
    HZ_50("50 Hz (Europa / Italia)"),
    HZ_60("60 Hz (USA / Asia)")
}

enum class AspectRatioSetting(val label: String, val ratio: Float) {
    RATIO_4_3("4:3", 4f / 3f),
    RATIO_16_9("16:9", 16f / 9f),
    RATIO_1_1("1:1", 1f),
    RATIO_FULL("Pieno", 20f / 9f)
}

enum class FlashModeSetting(val label: String) {
    OFF("Spento"),
    AUTO("Automatico"),
    ON("Attivo"),
    TORCH("Torcia fissa")
}

enum class CloudBackupService(val label: String) {
    GOOGLE_PHOTOS("Google Foto"),
    GOOGLE_DRIVE("Google Drive")
}

enum class CloudSyncStatus {
    SYNCED,
    PENDING,
    UPLOADING,
    ERROR
}

data class ProSettings(
    val iso: Int = 0, // 0 = Auto, 50, 100, 200, 400, 800, 1600, 3200
    val shutterSpeedIndex: Int = 0, // 0 = Auto, 1/4000s ... 5s
    val focusDistance: Float = -1f, // -1f = Auto AF, 0.0f (infinito) to 1.0f (macro)
    val evCompensation: Float = 0f, // -3.0 EV to +3.0 EV
    val wbKelvin: Int = 0, // 0 = Auto AWB, 2000K to 8500K
    val wbTint: Float = 0f // -1f to +1f
) {
    companion object {
        val ISO_PRESETS = listOf(0, 50, 100, 200, 400, 800, 1600, 3200)
        val SHUTTER_PRESETS = listOf(
            "AUTO" to 0L,
            "1/4000" to 250_000L,
            "1/2000" to 500_000L,
            "1/1000" to 1_000_000L,
            "1/500" to 2_000_000L,
            "1/250" to 4_000_000L,
            "1/125" to 8_000_000L,
            "1/60" to 16_666_666L,
            "1/30" to 33_333_333L,
            "1/15" to 66_666_666L,
            "1/4s" to 250_000_000L,
            "1s" to 1_000_000_000L,
            "2s" to 2_000_000_000L,
            "5s" to 5_000_000_000L
        )
        val WB_PRESETS = listOf(
            "AUTO" to 0,
            "3000K (Tungsteno)" to 3000,
            "4000K (Fluorescente)" to 4000,
            "5500K (Luce Sole)" to 5500,
            "6500K (Nuvoloso)" to 6500,
            "7500K (Ombra)" to 7500
        )
    }
}

data class CameraSettings(
    val videoResolution: VideoResolution = VideoResolution.FHD_1080P_60,
    val gridType: GridType = GridType.RULE_OF_THIRDS,
    val isLevelMeterEnabled: Boolean = true,
    val isRawCaptureEnabled: Boolean = false,
    val isHighRes50MpEnabled: Boolean = false,
    val isAiEnhanceEnabled: Boolean = true,
    val isLivePhotoEnabled: Boolean = false,
    val isVideoStabilizationEnabled: Boolean = true,
    val isAudioLevelMeterEnabled: Boolean = true,
    val antiBandingMode: AntiBandingMode = AntiBandingMode.AUTO,
    val isWatermarkEnabled: Boolean = false,
    val watermarkText: String = "Galaxy A35 • PixelCam",
    val isShutterSoundEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val aspectRatio: AspectRatioSetting = AspectRatioSetting.RATIO_4_3,
    val timerSeconds: Int = 0,
    val flashMode: FlashModeSetting = FlashModeSetting.AUTO,
    val cloudSyncEnabled: Boolean = true,
    val cloudBackupService: CloudBackupService = CloudBackupService.GOOGLE_PHOTOS,
    val cloudAccountEmail: String = "user.pixel@gmail.com",
    val cloudAccountName: String = "Semir Jetaime",
    val isCloudSignedIn: Boolean = true
)

data class MediaItem(
    val id: String,
    val uri: String,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isVideo: Boolean = false,
    val isLivePhoto: Boolean = false,
    val motionVideoPath: String? = null,
    val isRawDng: Boolean = false,
    val rawDngPath: String? = null,
    val isAiEnhanced: Boolean = false,
    val originalUri: String? = null,
    val cloudSyncStatus: CloudSyncStatus = CloudSyncStatus.SYNCED,
    val width: Int = 4096,
    val height: Int = 3072,
    val durationMs: Long = 0L,
    val sizeBytes: Long = 0L,
    val exposureTime: String = "1/125s",
    val iso: String = "ISO 100",
    val focalLength: String = "24mm f/1.8",
    val title: String = ""
)

data class TapFocusState(
    val xPercent: Float,
    val yPercent: Float,
    val exposureValue: Float = 0f, // -3.0 to +3.0
    val isLocked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
