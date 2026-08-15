package com.hotelwii.feature.recepcion.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ⚡ CacheHabitacion.kt — Motor de caché local ultrarrápido (< 1ms) con soporte Local-First.
 * Persiste las habitaciones en WiStore con las llaves estándar "wiHabitacion" y "wiHabitacion_lista".
 * Mantiene en memoria RAM el StateFlow reactivo en 0 ms para la recepción y el catálogo tarifario.
 */
class CacheHabitacion private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    private val _habitacionSeleccionadaFlow = MutableStateFlow<ModeloHabitacion?>(null)
    val habitacionSeleccionadaFlow: StateFlow<ModeloHabitacion?> = _habitacionSeleccionadaFlow.asStateFlow()

    private val _habitacionesFlow = MutableStateFlow<List<ModeloHabitacion>>(emptyList())
    val habitacionesFlow: StateFlow<List<ModeloHabitacion>> = _habitacionesFlow.asStateFlow()

    /**
     * Guarda la lista completa de habitaciones del hotel en RAM y WiStore ("wiHabitacion_lista").
     */
    fun guardarListaHabitaciones(lista: List<ModeloHabitacion>, empresaId: String = "") {
        _habitacionesFlow.value = lista
        try {
            val jsonStr = json.encodeToString(lista)
            store.save(KEY_HABITACION_LISTA, jsonStr)
            if (empresaId.isNotBlank()) {
                store.save("${KEY_HABITACION_LISTA}_$empresaId", jsonStr)
            }
        } catch (_: Exception) {}
    }

    /**
     * Sobrecarga compatible con parámetros invertidos (empresaId, lista).
     */
    fun guardarListaHabitaciones(empresaId: String, lista: List<ModeloHabitacion>) {
        guardarListaHabitaciones(lista, empresaId)
    }

    /**
     * Obtiene el catálogo de habitaciones guardadas en WiStore o memoria.
     */
    fun obtenerListaHabitaciones(empresaId: String = ""): List<ModeloHabitacion> {
        val jsonStr = if (empresaId.isNotBlank()) {
            val personal = store.get("${KEY_HABITACION_LISTA}_$empresaId")
            personal.ifBlank { store.get(KEY_HABITACION_LISTA) }
        } else {
            store.get(KEY_HABITACION_LISTA)
        }

        if (jsonStr.isBlank()) return emptyList()
        return try {
            val lista = json.decodeFromString<List<ModeloHabitacion>>(jsonStr)
            _habitacionesFlow.value = lista
            lista
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Guarda la habitación activa/seleccionada para Check-in o Edición ("wiHabitacion").
     */
    fun guardarHabitacionActiva(hab: ModeloHabitacion) {
        _habitacionSeleccionadaFlow.value = hab
        try {
            val jsonStr = json.encodeToString(hab)
            store.save(KEY_HABITACION, jsonStr)
        } catch (_: Exception) {}
    }

    /**
     * Obtiene la habitación seleccionada actualmente.
     */
    fun obtenerHabitacionActiva(): ModeloHabitacion? {
        val jsonStr = store.get(KEY_HABITACION)
        if (jsonStr.isBlank()) return null
        return try {
            json.decodeFromString<ModeloHabitacion>(jsonStr)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Limpia la caché al cerrar sesión o conmutar de cuenta.
     */
    fun limpiarCache() {
        _habitacionSeleccionadaFlow.value = null
        _habitacionesFlow.value = emptyList()
        store.save(KEY_HABITACION, "")
        store.save(KEY_HABITACION_LISTA, "")
    }

    companion object {
        private const val KEY_HABITACION = "wiHabitacion"
        private const val KEY_HABITACION_LISTA = "wiHabitacion_lista"

        @Volatile
        private var INSTANCE: CacheHabitacion? = null

        fun getInstance(context: Context): CacheHabitacion {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheHabitacion(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
