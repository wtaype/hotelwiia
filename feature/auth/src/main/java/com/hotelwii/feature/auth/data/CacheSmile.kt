package com.hotelwii.feature.auth.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

/**
 * ⚡ CacheSmile.kt — Motor de gestión y caché de la sesión única de usuario (wiSmile).
 * Maneja la persistencia de SmileModelo utilizando savels/getls genéricos de WiStore.
 */
class CacheSmile private constructor(context: Context) {
    private val store = wiStore(context)
    private val jsonConfig = Json { ignoreUnknownKeys = true }

    private val _sesionActivaFlow = MutableStateFlow<SmileModelo?>(getSmileGuardado())
    val sesionActivaFlow: StateFlow<SmileModelo?> = _sesionActivaFlow.asStateFlow()

    fun guardarSesion(smile: SmileModelo) {
        try {
            val jsonStr = jsonConfig.encodeToString(smile)
            store.savels("wiSmile", jsonStr, horas = null)
        } catch (_: Exception) {
            val jsonObject = JSONObject().apply {
                put("id", smile.id)
                put("usuario", smile.usuario)
                put("email", smile.email)
                put("nombre", smile.nombre)
                put("apellidos", smile.apellidos)
                put("avatar", smile.avatar ?: "")
                put("tema", smile.tema)
            }
            store.savels("wiSmile", jsonObject.toString(), horas = null)
        }
        store.save("wiToken", smile.id)
        _sesionActivaFlow.value = smile
    }

    fun getSmileGuardado(): SmileModelo? {
        val raw = store.getls("wiSmile") ?: return null
        if (raw.isBlank()) return null
        return try {
            jsonConfig.decodeFromString<SmileModelo>(raw)
        } catch (_: Exception) {
            try {
                val json = JSONObject(raw)
                val id = json.optString("id", "")
                if (id.isBlank()) null
                else SmileModelo(
                    id = id,
                    usuario = json.optString("usuario", ""),
                    email = json.optString("email", ""),
                    nombre = json.optString("nombre", ""),
                    apellidos = json.optString("apellidos", ""),
                    avatar = json.optString("avatar", "").ifBlank { null },
                    tema = json.optString("tema", "paz")
                )
            } catch (_: Exception) {
                null
            }
        }
    }

    fun hasSesion(): Boolean {
        return getSmileGuardado() != null || store.get("wiToken").isNotEmpty()
    }

    fun getInicialesNombre(): String {
        val smile = _sesionActivaFlow.value ?: getSmileGuardado()
        val nom = smile?.nombre?.ifBlank { smile.usuario } ?: "W"
        val partes = nom.trim().split(" ").filter { it.isNotBlank() }
        return when {
            partes.size >= 2 -> "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
            partes.size == 1 -> partes[0].take(2).uppercase()
            else -> "W"
        }
    }

    fun cerrarSesion() {
        store.remove("wiSmile", "wiToken", "usuario", "email", "empresa", "nombre", "apellidos", "avatar")
        _sesionActivaFlow.value = null
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheSmile? = null

        fun getInstance(context: Context): CacheSmile {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheSmile(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
