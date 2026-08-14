package com.hotelwii.feature.recepcion.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ⚡ CacheVentas.kt — Caché local ultrarrápido (< 1ms) para Hospedajes activos, Precuentas y Check-out.
 */
class CacheVentas private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    private val _ventasActivasFlow = MutableStateFlow<List<ModeloVenta>>(emptyList())
    val ventasActivasFlow: StateFlow<List<ModeloVenta>> = _ventasActivasFlow.asStateFlow()

    fun guardarVentasActivas(empresaId: String, lista: List<ModeloVenta>) {
        if (empresaId.isBlank()) return
        try {
            _ventasActivasFlow.value = lista
            val jsonStr = json.encodeToString(lista)
            store.save("ventas_activas_$empresaId", jsonStr)
        } catch (_: Exception) {}
    }

    fun obtenerVentasActivas(empresaId: String): List<ModeloVenta> {
        if (empresaId.isBlank()) return emptyList()
        val jsonStr = store.get("ventas_activas_$empresaId")
        if (jsonStr.isBlank()) return emptyList()
        return try {
            val lista = json.decodeFromString<List<ModeloVenta>>(jsonStr)
            _ventasActivasFlow.value = lista
            lista
        } catch (_: Exception) {
            emptyList()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheVentas? = null

        fun getInstance(context: Context): CacheVentas {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheVentas(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
