package com.hotelwii.core.kidev

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 🔒 Encriptar.kt — Motor Reutilizable de Cifrado AES-256 GCM/CBC & Hashing SHA-256.
 * Ubicado exclusivamente en kidev para resguardar credenciales de SUNAT, R2, Gemini y PIN de Seguridad.
 */
object Encriptar {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val SECRET_SEED = "HotelWii_AES256_Secure_Key_2026"
    private val iv = ByteArray(16) { 0x07 }

    private fun getSecretKey(): SecretKeySpec {
        val keyBytes = SECRET_SEED.toByteArray(StandardCharsets.UTF_8).copyOf(32)
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Cifra un texto en plano devolviendo un hash cifrado en Base64 con prefijo "ENC:".
     */
    fun cifrar(texto: String): String {
        if (texto.isBlank()) return ""
        if (texto.startsWith("ENC:")) return texto // Ya está cifrado
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
            val encrypted = cipher.doFinal(texto.toByteArray(StandardCharsets.UTF_8))
            "ENC:" + Base64.encodeToString(encrypted, Base64.NO_WRAP)
        } catch (_: Exception) {
            texto
        }
    }

    /**
     * Desencripta una cadena Base64 cifrada ("ENC:...") a su texto plano original.
     */
    fun descifrar(textoCifrado: String): String {
        if (textoCifrado.isBlank()) return ""
        val cleanInput = if (textoCifrado.startsWith("ENC:")) textoCifrado.substring(4) else textoCifrado
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(iv))
            val decoded = Base64.decode(cleanInput, Base64.NO_WRAP)
            String(cipher.doFinal(decoded), StandardCharsets.UTF_8)
        } catch (_: Exception) {
            textoCifrado
        }
    }

    /**
     * Genera un digest irreversible SHA-256 del PIN de 4 dígitos.
     */
    fun hashPin(pin: String): String {
        if (pin.isBlank()) return ""
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(pin.toByteArray(StandardCharsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            pin
        }
    }
}

/**
 * Extensiones helper para WiStore con cifrado transparente.
 */
fun WiStore.saveSecure(key: String, value: String): Boolean {
    val encrypted = Encriptar.cifrar(value)
    return this.save(key, encrypted)
}

fun WiStore.getSecure(key: String, fallback: String = ""): String {
    val raw = this.get(key, "")
    if (raw.isBlank()) return fallback
    return Encriptar.descifrar(raw).ifBlank { fallback }
}
