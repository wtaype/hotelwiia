package com.hotelwii.core.kidev

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

/**
 * 💾 Store.kt — Gestor genérico de almacenamiento local (SharedPreferences) y caché RAM (LruCache).
 * 100% agnóstico y libre de lógica de dominio (estilo storage.js).
 */
class WiStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("HotelWii_store", Context.MODE_PRIVATE)

    fun save(key: String, value: String): Boolean {
        return prefs.edit().putString(key, value).commit()
    }

    fun get(key: String, fallback: String = ""): String {
        return prefs.getString(key, fallback) ?: fallback
    }

    fun saveBool(key: String, value: Boolean): Boolean {
        return prefs.edit().putBoolean(key, value).commit()
    }

    fun getBool(key: String, fallback: Boolean = false): Boolean {
        return prefs.getBoolean(key, fallback)
    }

    fun saveLong(key: String, value: Long): Boolean {
        return prefs.edit().putLong(key, value).commit()
    }

    fun getLong(key: String, fallback: Long = 0L): Long {
        return prefs.getLong(key, fallback)
    }

    /**
     * savels: Guarda un valor JSON con tiempo de expiración (horas).
     * Si horas == null o horas <= 0, exp = 0L (expiración infinita permanente estilo storage.js).
     */
    fun savels(key: String, jsonValue: String, horas: Long? = null): Boolean {
        val expiryMs = if (horas != null && horas > 0) System.currentTimeMillis() + (horas * 3600000) else 0L
        val json = JSONObject().apply {
            put("v", jsonValue)
            put("exp", expiryMs)
        }
        return save(key, json.toString())
    }

    /**
     * getls: Obtiene un valor guardado con TTL.
     * Si exp == 0L o exp es nulo, NUNCA expira (retorna el valor persistentemente estilo storage.js).
     */
    fun getls(key: String): String? {
        val raw = get(key, "")
        if (raw.isEmpty()) return null
        return try {
            val json = JSONObject(raw)
            val exp = json.optLong("exp", 0L)
            if (exp > 0L && System.currentTimeMillis() > exp) {
                remove(key)
                null
            } else {
                if (json.has("v")) json.getString("v") else raw
            }
        } catch (e: Exception) {
            raw
        }
    }

    /**
     * removels / remove: Elimina una o más claves del almacenamiento local.
     */
    fun remove(vararg keys: String) {
        val editor = prefs.edit()
        keys.forEach { editor.remove(it) }
        editor.apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    // ⚡ ─────────────────────────────────────────────────────────────
    // 🚀 CACHÉ EN MEMORIA RAM DE IMÁGENES / AVATARES (Latencia 0ms)
    // ⚡ ─────────────────────────────────────────────────────────────
    fun getAvatarBitmapRam(url: String): ImageBitmap? {
        if (url.isBlank()) return null
        return ramImageCache.get(url)
    }

    suspend fun getAvatarBitmap(url: String): ImageBitmap? {
        if (url.isBlank()) return null
        ramImageCache.get(url)?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val stream = URL(url).openStream()
                val bmp = BitmapFactory.decodeStream(stream)
                val imageBitmap = bmp?.asImageBitmap()
                if (imageBitmap != null) {
                    ramImageCache.put(url, imageBitmap)
                }
                imageBitmap
            } catch (e: Exception) {
                null
            }
        }
    }

    companion object {
        private val ramImageCache = LruCache<String, ImageBitmap>(50)

        fun limpiarCacheImagenes() {
            ramImageCache.evictAll()
        }
    }
}

fun wiStore(context: Context): WiStore = WiStore(context)
