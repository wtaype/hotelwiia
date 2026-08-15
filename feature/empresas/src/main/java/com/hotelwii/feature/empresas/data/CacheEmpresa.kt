package com.hotelwii.feature.empresas.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ⚡ CacheEmpresa.kt — Motor de caché local ultrarrápido (< 1ms) con soporte Local-First.
 * Persiste en WiStore usando las llaves estándar "wiEmpresa_activa" y "wiEmpresa_lista".
 * Mantiene la reactividad en RAM en 0 ms con StateFlow para toda la app.
 */
class CacheEmpresa private constructor(context: Context) {
    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    private val _empresaActivaFlow = MutableStateFlow<ModeloEmpresa?>(obtenerEmpresaActiva())
    val empresaActivaFlow: StateFlow<ModeloEmpresa?> = _empresaActivaFlow.asStateFlow()

    private val _empresasListaFlow = MutableStateFlow<List<ModeloEmpresa>>(obtenerListaEmpresas())
    val empresasListaFlow: StateFlow<List<ModeloEmpresa>> = _empresasListaFlow.asStateFlow()

    private val _empresaActivaNombreFlow = MutableStateFlow(getNombreEmpresaActiva())
    val empresaActivaNombreFlow: StateFlow<String> = _empresaActivaNombreFlow.asStateFlow()

    /**
     * Guarda la lista completa de empresas en RAM y WiStore ("wiEmpresa_lista").
     */
    fun guardarListaEmpresas(lista: List<ModeloEmpresa>, smileId: String = "") {
        _empresasListaFlow.value = lista
        try {
            val jsonStr = json.encodeToString(lista)
            store.save(KEY_EMPRESA_LISTA, jsonStr)
            if (smileId.isNotBlank()) {
                store.save("${KEY_EMPRESA_LISTA}_$smileId", jsonStr)
            }
        } catch (_: Exception) {}
    }

    fun guardarListaEmpresas(smileId: String, lista: List<ModeloEmpresa>) {
        guardarListaEmpresas(lista, smileId)
    }

    /**
     * Obtiene la lista de empresas guardadas en WiStore o memoria.
     */
    fun obtenerListaEmpresas(smileId: String = ""): List<ModeloEmpresa> {
        val jsonStr = if (smileId.isNotBlank()) {
            val personal = store.get("${KEY_EMPRESA_LISTA}_$smileId")
            personal.ifBlank { store.get(KEY_EMPRESA_LISTA) }
        } else {
            store.get(KEY_EMPRESA_LISTA)
        }

        if (jsonStr.isBlank()) return emptyList()
        return try {
            json.decodeFromString<List<ModeloEmpresa>>(jsonStr)
        } catch (_: Exception) {
            emptyList()
        }
    }

    /**
     * Guarda y selecciona la empresa activa en RAM y WiStore ("wiEmpresa_activa").
     */
    fun guardarEmpresaActiva(empresa: ModeloEmpresa, smileId: String = "") {
        _empresaActivaFlow.value = empresa
        _empresaActivaNombreFlow.value = empresa.nombreComercial.ifBlank { "Hotel Wii" }

        try {
            val jsonStr = json.encodeToString(empresa)
            store.save(KEY_EMPRESA_ACTIVA, jsonStr)
            if (smileId.isNotBlank()) {
                store.save("${KEY_EMPRESA_ACTIVA}_$smileId", jsonStr)
            }
        } catch (_: Exception) {}
    }

    /**
     * Obtiene la empresa activa actual.
     */
    fun obtenerEmpresaActiva(smileId: String = ""): ModeloEmpresa? {
        val jsonStr = if (smileId.isNotBlank()) {
            val personal = store.get("${KEY_EMPRESA_ACTIVA}_$smileId")
            personal.ifBlank { store.get(KEY_EMPRESA_ACTIVA) }
        } else {
            store.get(KEY_EMPRESA_ACTIVA)
        }

        if (jsonStr.isBlank()) return null
        return try {
            json.decodeFromString<ModeloEmpresa>(jsonStr)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Retorna el nombre comercial de la empresa activa para el Header (0 ms).
     */
    fun getNombreEmpresaActiva(): String {
        val activa = _empresaActivaFlow.value ?: obtenerEmpresaActiva()
        if (activa != null && activa.nombreComercial.isNotBlank()) {
            return activa.nombreComercial
        }
        return "Hotel Wii"
    }

    /**
     * Limpia la caché de empresas al cerrar sesión.
     */
    fun limpiarCache() {
        _empresaActivaFlow.value = null
        _empresasListaFlow.value = emptyList()
        _empresaActivaNombreFlow.value = "Hotel Wii"
        store.save(KEY_EMPRESA_ACTIVA, "")
        store.save(KEY_EMPRESA_LISTA, "")
    }

    companion object {
        private const val KEY_EMPRESA_ACTIVA = "wiEmpresa_activa"
        private const val KEY_EMPRESA_LISTA = "wiEmpresa_lista"

        @Volatile
        private var INSTANCE: CacheEmpresa? = null

        fun getInstance(context: Context): CacheEmpresa {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CacheEmpresa(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
