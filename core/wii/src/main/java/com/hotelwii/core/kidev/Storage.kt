package com.hotelwii.core.kidev

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

/**
 * 💾 Storage.kt — Motor de almacenamiento persistente local para HotelWii (WiStore)
 * Soporta expiración infinita (horas = null) estilo widev/storage.js
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
     * Guarda un valor JSON con tiempo de expiración (horas).
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
     * Obtiene un valor guardado con TTL.
     * Si exp == 0L o exp es nulo, NUNCA expira (retorna el valor persistentemente).
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

    fun remove(vararg keys: String) {
        val editor = prefs.edit()
        keys.forEach { editor.remove(it) }
        editor.apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    // ─── Helpers de Autenticación y Sesión wiSmile ─────────────────
    fun hasSesion(): Boolean {
        return get("wiToken").isNotEmpty() || getls("wiSmile") != null
    }

    /**
     * Guarda el objeto Smile completo en JSON en la clave wiSmile con expiración infinita (horas = null).
     */
    fun saveSmile(
        id: String,
        usuario: String,
        email: String,
        nombre: String = "",
        apellidos: String = "",
        avatar: String = ""
    ): Boolean {
        save("wiToken", id)
        save("usuario", usuario)
        save("email", email)
        save("nombre", nombre)
        save("apellidos", apellidos)
        save("avatar", avatar)

        val smileJson = JSONObject().apply {
            put("id", id)
            put("usuario", usuario)
            put("email", email)
            put("nombre", nombre)
            put("apellidos", apellidos)
            put("avatar", avatar)
        }.toString()

        return savels("wiSmile", smileJson, horas = null)
    }

    /**
     * Recupera el JSON guardado en wiSmile.
     */
    fun getSmileJson(): JSONObject? {
        val raw = getls("wiSmile") ?: return null
        return try {
            JSONObject(raw)
        } catch (e: Exception) {
            null
        }
    }

    fun getSmileNombre(): String {
        val json = getSmileJson()
        val nom = json?.optString("nombre", "") ?: ""
        if (nom.isNotBlank()) return nom
        return get("usuario").ifBlank { "Usuario" }
    }

    fun getSmileAvatar(): String {
        val json = getSmileJson()
        val av = json?.optString("avatar", "") ?: ""
        if (av.isNotBlank()) return av
        return get("avatar")
    }

    fun saveSesion(token: String, usuario: String, email: String, empresa: String): Boolean {
        save("wiToken", token)
        save("usuario", usuario)
        save("email", email)
        save("empresa", empresa)
        return saveSmile(id = token, usuario = usuario, email = email)
    }

    fun getEmpresaNombre(): String {
        val emp = get("empresa")
        return if (emp.isNotEmpty()) emp else "Empresa"
    }

    fun cerrarSesion() {
        remove("wiToken", "wiSmile", "usuario", "email", "empresa", "nombre", "apellidos", "avatar")
    }
}

fun wiStore(context: Context): WiStore = WiStore(context)
