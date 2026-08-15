package com.hotelwii.feature.actualizar.lib

import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

/**
 * 🔒 HashValidador — Verificador de integridad criptográfica SHA-256 para binarios APK
 */
object HashValidador {
    fun calcularSha256(archivo: File): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(archivo).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }

    fun validar(archivo: File, hashEsperado: String?): Boolean {
        if (hashEsperado.isNullOrBlank()) return true
        val hashCalculado = calcularSha256(archivo)
        return hashCalculado.equals(hashEsperado.trim(), ignoreCase = true)
    }
}
