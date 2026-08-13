package com.hotelwii.feature.empresas.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ⚡ CacheEmpresa.kt — Motor de caché local ultrarrápido (< 1ms) con soporte Local-First por smileId.
 * Guarda y recupera en RAM y WiStore la lista completa de hoteles y la empresa activa en 0ms.
 */
class CacheEmpresa private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    private val _empresaActivaNombreFlow = MutableStateFlow(getNombreEmpresaActiva())
    val empresaActivaNombreFlow: StateFlow<String> = _empresaActivaNombreFlow.asStateFlow()

    private val _empresaActivaFlow = MutableStateFlow<ModeloEmpresa?>(null)
    val empresaActivaFlow: StateFlow<ModeloEmpresa?> = _empresaActivaFlow.asStateFlow()

    fun guardarListaEmpresas(smileId: String, lista: List<ModeloEmpresa>) {
        if (smileId.isBlank()) return
        try {
            val jsonStr = json.encodeToString(lista)
            store.save("empresas_lista_$smileId", jsonStr)
        } catch (_: Exception) {}
    }

    fun obtenerListaEmpresas(smileId: String): List<ModeloEmpresa> {
        if (smileId.isBlank()) return emptyList()
        val jsonStr = store.get("empresas_lista_$smileId")
        if (jsonStr.isBlank()) return emptyList()
        return try {
            json.decodeFromString<List<ModeloEmpresa>>(jsonStr)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun guardarEmpresaActiva(empresa: ModeloEmpresa, smileId: String = "") {
        store.save("empresa", empresa.nombreComercial)
        _empresaActivaNombreFlow.value = empresa.nombreComercial.ifBlank { "Hotel Wii" }
        _empresaActivaFlow.value = empresa

        if (smileId.isNotBlank()) {
            try {
                val jsonStr = json.encodeToString(empresa)
                store.save("empresa_activa_$smileId", jsonStr)
            } catch (_: Exception) {}
        }
    }

    fun obtenerEmpresaActiva(smileId: String): ModeloEmpresa? {
        if (smileId.isBlank()) return null
        val jsonStr = store.get("empresa_activa_$smileId")
        if (jsonStr.isBlank()) return null
        return try {
            json.decodeFromString<ModeloEmpresa>(jsonStr)
        } catch (_: Exception) {
            null
        }
    }

    fun getNombreEmpresaActiva(): String {
        val emp = store.get("empresa")
        return if (emp.isNotBlank()) emp else "Hotel Wii"
    }

    companion object {
        @Volatile
        private var INSTANCE: CacheEmpresa? = null

        fun getInstance(context: Context): CacheEmpresa {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheEmpresa(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
