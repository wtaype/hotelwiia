package com.hotelwii.feature.imprimir.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

/**
 * ⚡ CacheImprimir.kt — Motor de caché local-first (< 1ms) y deduplicador de impresiones.
 * Maneja la persistencia en RAM y WiStore para prevenir duplicados y permitir cola offline.
 */
class CacheImprimir private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    // Conjunto en memoria para deduplicación ultra-rápida (0 ms)
    private val idsProcesados = ConcurrentHashMap.newKeySet<String>()

    private val _historialImpresionesFlow = MutableStateFlow<List<ModeloImpresion>>(obtenerHistorial())
    val historialImpresionesFlow: StateFlow<List<ModeloImpresion>> = _historialImpresionesFlow.asStateFlow()

    private val _esReceptorNubeActivoFlow = MutableStateFlow(esReceptorActivo())
    val esReceptorNubeActivoFlow: StateFlow<Boolean> = _esReceptorNubeActivoFlow.asStateFlow()

    /**
     * Verifica si un trabajo ya fue procesado para evitar impresiones repetidas.
     */
    fun yaFueProcesado(id: String): Boolean {
        if (id.isBlank()) return false
        if (idsProcesados.contains(id)) return true
        val guardados = store.get("impresiones_procesadas_ids")
        return guardados.contains(id)
    }

    /**
     * Registra un trabajo como procesado en RAM y WiStore.
     */
    fun registrarProcesado(id: String) {
        if (id.isBlank()) return
        idsProcesados.add(id)
        val guardados = store.get("impresiones_procesadas_ids")
        val lista = guardados.split(",").filter { it.isNotBlank() }.takeLast(100).toMutableList()
        if (!lista.contains(id)) {
            lista.add(id)
            store.save("impresiones_procesadas_ids", lista.joinToString(","))
        }
    }

    /**
     * Guarda un trabajo exitoso en el historial local (últimos 20 tickets).
     */
    fun agregarHistorial(impresion: ModeloImpresion) {
        val actual = obtenerHistorial().toMutableList()
        actual.removeAll { it.id == impresion.id }
        actual.add(0, impresion)
        val recortada = actual.take(20)
        try {
            val jsonStr = json.encodeToString(recortada)
            store.save("impresiones_historial_cache", jsonStr)
            _historialImpresionesFlow.value = recortada
        } catch (_: Exception) {}
    }

    fun obtenerHistorial(): List<ModeloImpresion> {
        val jsonStr = store.get("impresiones_historial_cache")
        if (jsonStr.isBlank()) return emptyList()
        return try {
            json.decodeFromString<List<ModeloImpresion>>(jsonStr)
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Estado del switch: si este dispositivo actúa como receptor de nube para la ticketera física.
     */
    fun setReceptorActivo(activo: Boolean) {
        store.save("impresora_receptor_nube_activo", if (activo) "true" else "false")
        _esReceptorNubeActivoFlow.value = activo
    }

    fun esReceptorActivo(): Boolean {
        val raw = store.get("impresora_receptor_nube_activo")
        return raw.ifBlank { "true" } == "true" // Activo por defecto en counter
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheImprimir? = null

        fun getInstance(context: Context): CacheImprimir {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheImprimir(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
