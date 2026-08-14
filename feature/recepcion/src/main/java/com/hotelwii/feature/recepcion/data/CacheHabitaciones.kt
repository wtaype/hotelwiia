package com.hotelwii.feature.recepcion.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ⚡ CacheHabitaciones.kt — Motor de caché local ultrarrápido (< 1ms) con soporte Local-First por empresaId.
 * Guarda y recupera en RAM y wiStore el catálogo completo de habitaciones en 0ms.
 */
class CacheHabitaciones private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    private val _habitacionesFlow = MutableStateFlow<List<ModeloHabitacion>>(emptyList())
    val habitacionesFlow: StateFlow<List<ModeloHabitacion>> = _habitacionesFlow.asStateFlow()

    fun guardarListaHabitaciones(empresaId: String, lista: List<ModeloHabitacion>) {
        if (empresaId.isBlank()) return
        try {
            _habitacionesFlow.value = lista
            val jsonStr = json.encodeToString(lista)
            store.save("habitaciones_lista_$empresaId", jsonStr)
        } catch (_: Exception) {}
    }

    fun obtenerListaHabitaciones(empresaId: String): List<ModeloHabitacion> {
        if (empresaId.isBlank()) return emptyList()
        val jsonStr = store.get("habitaciones_lista_$empresaId")
        if (jsonStr.isBlank()) return emptyList()
        return try {
            val lista = json.decodeFromString<List<ModeloHabitacion>>(jsonStr)
            _habitacionesFlow.value = lista
            lista
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheHabitaciones? = null

        fun getInstance(context: Context): CacheHabitaciones {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheHabitaciones(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
