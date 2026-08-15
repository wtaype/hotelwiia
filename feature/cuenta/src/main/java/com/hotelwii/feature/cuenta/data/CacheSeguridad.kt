package com.hotelwii.feature.cuenta.data

import android.content.Context
import com.hotelwii.core.kidev.Encriptar
import com.hotelwii.core.kidev.getSecure
import com.hotelwii.core.kidev.saveSecure
import com.hotelwii.core.kidev.wiStore
import com.hotelwii.feature.cuenta.api.SeguridadModelo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 🔒 CacheSeguridad.kt — Gestor de Caché Local-First para la Bóveda de Seguridad.
 * Persiste las credenciales y el PIN cifrados con AES-256 en wiStore usando la llave "wiSeguridad".
 * Ofrece lectura inmediata en memoria RAM (0 ms) mediante StateFlow reactivo.
 */
class CacheSeguridad private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    private val _seguridadFlow = MutableStateFlow<SeguridadModelo?>(obtenerSeguridad())
    val seguridadFlow: StateFlow<SeguridadModelo?> = _seguridadFlow.asStateFlow()

    /**
     * Guarda el modelo de seguridad completo con cifrado AES-256 en wiStore y actualiza RAM.
     */
    fun guardarSeguridad(modelo: SeguridadModelo) {
        _seguridadFlow.value = modelo
        try {
            val rawJson = json.encodeToString(modelo)
            store.saveSecure(KEY_SEGURIDAD, rawJson)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Obtiene el modelo de seguridad descifrado desde wiStore.
     */
    fun obtenerSeguridad(): SeguridadModelo? {
        val rawJson = store.getSecure(KEY_SEGURIDAD, "")
        if (rawJson.isBlank()) return null
        return try {
            json.decodeFromString<SeguridadModelo>(rawJson)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Actualiza o crea el PIN de 4 dígitos (hasheado SHA-256) preservando las demás credenciales.
     */
    fun guardarPin(pin: String, userId: String = "") {
        val hashedPin = if (pin.isBlank()) "" else Encriptar.hashPin(pin)
        val actual = _seguridadFlow.value ?: SeguridadModelo(userId = userId)
        val actualizado = actual.copy(
            userId = if (userId.isNotBlank()) userId else actual.userId,
            pinSeguridad = hashedPin
        )
        guardarSeguridad(actualizado)
    }

    /**
     * Valida si el PIN ingresado coincide con el PIN configurado (o si aún no hay PIN).
     */
    fun validarPin(pinIngresado: String): Boolean {
        val pinGuardado = _seguridadFlow.value?.pinSeguridad ?: ""
        if (pinGuardado.isBlank()) return true
        val hashed = Encriptar.hashPin(pinIngresado)
        return pinIngresado == pinGuardado || hashed == pinGuardado
    }

    /**
     * Verifica si la bóveda tiene un PIN activo configurado.
     */
    fun tienePinConfigurado(): Boolean {
        val pin = _seguridadFlow.value?.pinSeguridad ?: ""
        return pin.isNotBlank()
    }

    /**
     * Restablece el PIN a vacío (útil tras validar contraseña de cuenta).
     */
    fun restablecerPin() {
        val actual = _seguridadFlow.value ?: return
        guardarSeguridad(actual.copy(pinSeguridad = ""))
    }

    /**
     * Limpia la caché local de seguridad al cerrar sesión.
     */
    fun limpiarCache() {
        _seguridadFlow.value = null
        store.save(KEY_SEGURIDAD, "")
        store.save(KEY_SESION, "")
    }

    companion object {
        private const val KEY_SEGURIDAD = "wiSeguridad"
        private const val KEY_SESION = "wiSesion"

        @Volatile
        private var instance: CacheSeguridad? = null

        fun getInstance(context: Context): CacheSeguridad =
            instance ?: synchronized(this) {
                instance ?: CacheSeguridad(context.applicationContext).also { instance = it }
            }
    }
}
