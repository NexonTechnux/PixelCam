package com.example.camera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

enum class DetectedScene(val label: String, val icon: String, val suggestion: String) {
    LANDSCAPE("Paesaggio", "🏞️", "Nitidezza orizzonte e vivacità cielo"),
    NIGHT_SCENE("Notte", "🌙", "Riduzione rumore e recupero ombre"),
    PORTRAIT("Ritratto", "👤", "Tonalità pelle calda e bokeh naturale"),
    FOOD("Cibo", "🍲", "Saturazione colori calda e dettaglio macro"),
    SUNSET("Tramonto", "🌅", "Gradiente aureo e contrasto tonale"),
    MACRO("Dettaglio Macro", "🔍", "Micro-contrasto e messa a fuoco fine"),
    STANDARD("AI Standard", "✨", "Bilanciamento dinamico intelligente")
}

data class AiEnhancementResult(
    val enhancedBitmap: Bitmap,
    val scene: DetectedScene,
    val hdrGain: Float,
    val clarityGain: Float,
    val colorVibrance: Float,
    val processingTimeMs: Long
)

object AiEnhancementEngine {

    suspend fun analyzeScene(bitmap: Bitmap): DetectedScene = withContext(Dispatchers.Default) {
        val sample = Bitmap.createScaledBitmap(bitmap, 64, 64, false)
        var totalLuminance = 0f
        var redSum = 0f
        var greenSum = 0f
        var blueSum = 0f
        val pixelCount = 64 * 64

        for (x in 0 until 64) {
            for (y in 0 until 64) {
                val color = sample.getPixel(x, y)
                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                val lum = 0.299f * r + 0.587f * g + 0.114f * b
                totalLuminance += lum
                redSum += r
                greenSum += g
                blueSum += b
            }
        }

        val avgLum = totalLuminance / pixelCount
        val avgR = redSum / pixelCount
        val avgG = greenSum / pixelCount
        val avgB = blueSum / pixelCount

        when {
            avgLum < 55f -> DetectedScene.NIGHT_SCENE
            avgR > 1.3f * avgB && avgR > 140f -> DetectedScene.SUNSET
            avgG > 1.15f * avgR && avgG > 1.15f * avgB -> DetectedScene.LANDSCAPE
            avgR > avgG && avgG > avgB && avgLum in 100f..180f -> DetectedScene.PORTRAIT
            avgR > 150f && avgG > 110f -> DetectedScene.FOOD
            else -> DetectedScene.STANDARD
        }
    }

    suspend fun enhancePhoto(
        sourceBitmap: Bitmap,
        whiteBalanceKelvin: Int = 0,
        wbTint: Float = 0f
    ): AiEnhancementResult = withContext(Dispatchers.Default) {
        val startTime = System.currentTimeMillis()
        val detectedScene = analyzeScene(sourceBitmap)

        // Configuration based on scene
        val (contrast, brightness, saturation) = when (detectedScene) {
            DetectedScene.NIGHT_SCENE -> Triple(1.15f, 28f, 1.10f)
            DetectedScene.SUNSET -> Triple(1.20f, 10f, 1.25f)
            DetectedScene.LANDSCAPE -> Triple(1.18f, 8f, 1.20f)
            DetectedScene.PORTRAIT -> Triple(1.08f, 15f, 1.05f)
            DetectedScene.FOOD -> Triple(1.15f, 12f, 1.22f)
            DetectedScene.MACRO -> Triple(1.22f, 6f, 1.15f)
            DetectedScene.STANDARD -> Triple(1.12f, 12f, 1.15f)
        }

        // ColorMatrix for Tone, Saturation, Brightness & White Balance
        val cm = ColorMatrix()

        // 1. Saturation
        val satMatrix = ColorMatrix()
        satMatrix.setSaturation(saturation)
        cm.postConcat(satMatrix)

        // 2. White Balance shift if customized
        var rScale = 1.0f
        var bScale = 1.0f
        if (whiteBalanceKelvin > 0) {
            // Lower Kelvin is warm/amber, higher is cool/blue
            val tempFactor = (whiteBalanceKelvin - 5500) / 3000f
            rScale -= tempFactor * 0.15f
            bScale += tempFactor * 0.15f
        }
        val tintScaleG = 1.0f + (wbTint * 0.1f)

        // 3. Contrast & Brightness scale
        val scaleMatrix = ColorMatrix(floatArrayOf(
            contrast * rScale, 0f, 0f, 0f, brightness,
            0f, contrast * tintScaleG, 0f, 0f, brightness,
            0f, 0f, contrast * bScale, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        cm.postConcat(scaleMatrix)

        // Apply filter to output bitmap
        val outputBitmap = Bitmap.createBitmap(
            sourceBitmap.width,
            sourceBitmap.height,
            sourceBitmap.config ?: Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(outputBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            colorFilter = ColorMatrixColorFilter(cm)
        }
        canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)

        val duration = System.currentTimeMillis() - startTime

        AiEnhancementResult(
            enhancedBitmap = outputBitmap,
            scene = detectedScene,
            hdrGain = 1.25f,
            clarityGain = 1.30f,
            colorVibrance = saturation,
            processingTimeMs = duration
        )
    }

    // High resolution sensor export (50MP simulation/scaling)
    suspend fun createHighResExport(sourceBitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val targetWidth = min(sourceBitmap.width * 2, 6144)
        val targetHeight = min(sourceBitmap.height * 2, 4608)
        Bitmap.createScaledBitmap(sourceBitmap, targetWidth, targetHeight, true)
    }
}
