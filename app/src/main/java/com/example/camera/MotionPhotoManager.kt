package com.example.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.max
import kotlin.math.min

object MotionPhotoManager {

    /**
     * Extracts frames from a 3-second motion photo video and generates an animated GIF
     */
    suspend fun exportAsGif(
        context: Context,
        motionVideoPath: String,
        outputGifFile: File,
        targetFps: Int = 10,
        maxDimension: Int = 360
    ): Boolean = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(motionVideoPath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 3000L

            val frameCount = min(30, (durationMs / 1000f * targetFps).toInt())
            val frameIntervalUs = (durationMs * 1000L) / frameCount

            val frames = mutableListOf<Bitmap>()
            for (i in 0 until frameCount) {
                val timeUs = i * frameIntervalUs
                val rawBitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                if (rawBitmap != null) {
                    val scale = maxDimension.toFloat() / max(rawBitmap.width, rawBitmap.height)
                    val scaledW = (rawBitmap.width * scale).toInt()
                    val scaledH = (rawBitmap.height * scale).toInt()
                    val scaled = Bitmap.createScaledBitmap(rawBitmap, scaledW, scaledH, true)
                    frames.add(scaled)
                }
            }

            if (frames.isEmpty()) return@withContext false

            FileOutputStream(outputGifFile).use { out ->
                encodeGif(frames, out, delayMs = 1000 / targetFps)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            retriever.release()
        }
    }

    /**
     * Exports the 3-second motion clip as an independent MP4 video
     */
    suspend fun exportAsShortVideo(
        sourceVideoPath: String,
        outputVideoFile: File
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val src = File(sourceVideoPath)
            if (!src.exists()) return@withContext false
            src.copyTo(outputVideoFile, overwrite = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Lightweight GIF89a encoder for exporting animated motion photos
     */
    private fun encodeGif(frames: List<Bitmap>, out: OutputStream, delayMs: Int) {
        // GIF89a header
        out.write("GIF89a".toByteArray(Charsets.US_ASCII))

        val width = frames[0].width
        val height = frames[0].height

        // Logical Screen Descriptor
        writeShort(out, width)
        writeShort(out, height)
        // Global Color Table flag (0x80) + 7 (256 colors)
        out.write(0xF7)
        out.write(0) // Background color index
        out.write(0) // Pixel aspect ratio

        // Global color table: standard 256-color palette (6x7x6 color cube + grays)
        val palette = createColorPalette()
        out.write(palette)

        // Netscape application extension for looping infinitely
        out.write(0x21) // Extension introducer
        out.write(0xFF) // App extension label
        out.write(11)   // Block size
        out.write("NETSCAPE2.0".toByteArray(Charsets.US_ASCII))
        out.write(3)    // Sub-block length
        out.write(1)    // Sub-block ID
        writeShort(out, 0) // Loop count = 0 (infinite)
        out.write(0)    // Block terminator

        for (frame in frames) {
            // Graphic Control Extension
            out.write(0x21)
            out.write(0xF9)
            out.write(4) // Block size
            out.write(0x00) // Packed field: disposal method = 0
            writeShort(out, delayMs / 10) // Delay time in 1/100ths of second
            out.write(0) // Transparent color index
            out.write(0) // Block terminator

            // Image Descriptor
            out.write(0x2C)
            writeShort(out, 0) // Left
            writeShort(out, 0) // Top
            writeShort(out, width)
            writeShort(out, height)
            out.write(0) // No Local Color Table

            // Quantize image to palette indices
            val pixels = IntArray(width * height)
            frame.getPixels(pixels, 0, width, 0, 0, width, height)
            val indexedPixels = ByteArray(pixels.size)
            for (i in pixels.indices) {
                indexedPixels[i] = findClosestColorIndex(pixels[i], palette)
            }

            // Encode raster data using simple uncompressed/sub-block LZW
            writeLzwData(out, indexedPixels)
        }

        // GIF Trailer
        out.write(0x3B)
        out.flush()
    }

    private fun writeShort(out: OutputStream, value: Int) {
        out.write(value and 0xFF)
        out.write((value shr 8) and 0xFF)
    }

    private fun createColorPalette(): ByteArray {
        val table = ByteArray(256 * 3)
        var idx = 0
        // 6x7x6 color cube = 252 colors
        for (r in 0 until 6) {
            for (g in 0 until 7) {
                for (b in 0 until 6) {
                    if (idx < 252 * 3) {
                        table[idx++] = (r * 255 / 5).toByte()
                        table[idx++] = (g * 255 / 6).toByte()
                        table[idx++] = (b * 255 / 5).toByte()
                    }
                }
            }
        }
        // Last 4 colors: grays/black/white
        val grays = intArrayOf(0, 85, 170, 255)
        for (g in grays) {
            if (idx < 256 * 3) {
                table[idx++] = g.toByte()
                table[idx++] = g.toByte()
                table[idx++] = g.toByte()
            }
        }
        return table
    }

    private fun findClosestColorIndex(rgb: Int, palette: ByteArray): Byte {
        val r = (rgb shr 16) and 0xFF
        val g = (rgb shr 8) and 0xFF
        val b = rgb and 0xFF

        val rIdx = (r * 5 + 127) / 255
        val gIdx = (g * 6 + 127) / 255
        val bIdx = (b * 5 + 127) / 255
        val index = rIdx * 42 + gIdx * 6 + bIdx
        return (index and 0xFF).toByte()
    }

    private fun writeLzwData(out: OutputStream, data: ByteArray) {
        val minCodeSize = 8
        out.write(minCodeSize) // Minimum code size = 8 bits for 256 colors

        val clearCode = 1 shl minCodeSize // 256
        val endOfInfoCode = clearCode + 1 // 257

        val bitStream = BitOutputStream()
        var curCodeSize = minCodeSize + 1

        // Send Clear Code
        bitStream.writeBits(clearCode, curCodeSize)

        // Write literals
        for (b in data) {
            val code = b.toInt() and 0xFF
            bitStream.writeBits(code, curCodeSize)
        }

        // Send End of Information code
        bitStream.writeBits(endOfInfoCode, curCodeSize)
        bitStream.flush()

        val bytes = bitStream.toByteArray()
        var offset = 0
        while (offset < bytes.size) {
            val chunkSize = min(254, bytes.size - offset)
            out.write(chunkSize)
            out.write(bytes, offset, chunkSize)
            offset += chunkSize
        }
        out.write(0) // Block terminator
    }

    private class BitOutputStream {
        private val buffer = ByteArrayOutputStream()
        private var curByte = 0
        private var curBit = 0

        fun writeBits(value: Int, count: Int) {
            for (i in 0 until count) {
                val bit = (value shr i) and 1
                curByte = curByte or (bit shl curBit)
                curBit++
                if (curBit == 8) {
                    buffer.write(curByte)
                    curByte = 0
                    curBit = 0
                }
            }
        }

        fun flush() {
            if (curBit > 0) {
                buffer.write(curByte)
                curByte = 0
                curBit = 0
            }
        }

        fun toByteArray(): ByteArray = buffer.toByteArray()
    }
}
