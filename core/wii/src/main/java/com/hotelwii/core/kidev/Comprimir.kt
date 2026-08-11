// Comprimir.kt — Compresor de imágenes para Android (reducir peso de 10MB a ~250KB)
package com.hotelwii.core.kidev

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

/**
 * Resultado de la compresión con estadísticas de tamaño
 */
data class WiCompressedResult(
    val bitmap: Bitmap,
    val bytes: ByteArray,
    val originalSizeKb: Long,
    val compressedSizeKb: Long
)

object WiComprimir {

    /**
     * Comprime un Bitmap ajustando sus dimensiones máximas y su calidad de compresión JPEG.
     */
    fun comprimir(
        originalBitmap: Bitmap,
        maxDimension: Int = 1080,
        calidad: Int = 75
    ): WiCompressedResult {
        val streamOriginal = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, streamOriginal)
        val origKb = streamOriginal.toByteArray().size / 1024L

        // Redimensionamiento proporcional
        val width = originalBitmap.width
        val height = originalBitmap.height
        val bitmapRedimensionado = if (width > maxDimension || height > maxDimension) {
            val ratio = width.toFloat() / height.toFloat()
            val newWidth: Int
            val newHeight: Int
            if (width > height) {
                newWidth = maxDimension
                newHeight = (maxDimension / ratio).toInt()
            } else {
                newHeight = maxDimension
                newWidth = (maxDimension * ratio).toInt()
            }
            Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
        } else {
            originalBitmap
        }

        // Compresión de calidad
        val streamComprimido = ByteArrayOutputStream()
        bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, calidad, streamComprimido)
        val bytes = streamComprimido.toByteArray()
        val compKb = bytes.size / 1024L

        val resultBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        return WiCompressedResult(
            bitmap = resultBitmap,
            bytes = bytes,
            originalSizeKb = origKb,
            compressedSizeKb = compKb
        )
    }
}
