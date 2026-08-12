package com.hotelwii.feature.auth.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ⚡ CacheSmile.kt — Motor de caché local ultrarrápido (< 2ms) para la sesión Smile de HotelWii.
 */
class CacheSmile private constructor(context: Context) {
    private val store = wiStore(context)

    private val _sesionActivaFlow = MutableStateFlow<SmileModelo?>(getSmileGuardado())
    val sesionActivaFlow: StateFlow<SmileModelo?> = _sesionActivaFlow.asStateFlow()

    fun guardarSesion(smile: SmileModelo) {
        store.saveSmile(
            id = smile.id,
            usuario = smile.usuario,
            email = smile.email,
            nombre = smile.nombre,
            apellidos = smile.apellidos,
            avatar = smile.avatar ?: ""
        )
        _sesionActivaFlow.value = smile
    }

    fun getSmileGuardado(): SmileModelo? {
        val json = store.getSmileJson() ?: return null
        val id = json.optString("id", "")
        if (id.isBlank()) return null

        val avatarVal = json.optString("avatar", "")

        return SmileModelo(
            id = id,
            usuario = json.optString("usuario", ""),
            email = json.optString("email", ""),
            nombre = json.optString("nombre", ""),
            apellidos = json.optString("apellidos", ""),
            avatar = avatarVal.ifBlank { null }
        )
    }

    fun cerrarSesion() {
        store.cerrarSesion()
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
