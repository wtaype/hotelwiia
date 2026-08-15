package com.hotelwii.feature.personal.data

import android.content.Context
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 👥 CachePersonal.kt — Motor de Caché Local-First (0 ms) para el equipo de colaboradores.
 *
 * Llaves Estandarizadas:
 * - wiPersonal    -> Trabajador activo / seleccionado en turno (ModeloPersonal)
 * - wiPersonales  -> Lista de todos los colaboradores del hotel (List<ModeloPersonal>)
 */
class CachePersonal private constructor(context: Context) {

    private val store = wiStore(context)
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        const val KEY_PERSONAL = "wiPersonal"
        const val KEY_PERSONALES = "wiPersonales"

        @Volatile
        private var INSTANCE: CachePersonal? = null

        fun getInstance(context: Context): CachePersonal {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CachePersonal(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val _personalActivoFlow = MutableStateFlow<ModeloPersonal?>(obtenerPersonalGuardado())
    val personalActivoFlow: StateFlow<ModeloPersonal?> = _personalActivoFlow.asStateFlow()

    private val _personalesFlow = MutableStateFlow<List<ModeloPersonal>>(obtenerListaGuardada())
    val personalesFlow: StateFlow<List<ModeloPersonal>> = _personalesFlow.asStateFlow()

    private fun obtenerPersonalGuardado(): ModeloPersonal? {
        val raw = store.get(KEY_PERSONAL) ?: return null
        return try {
            json.decodeFromString<ModeloPersonal>(raw)
        } catch (_: Exception) {
            null
        }
    }

    private fun obtenerListaGuardada(empresaId: String = ""): List<ModeloPersonal> {
        val key = if (empresaId.isNotBlank()) "${KEY_PERSONALES}_$empresaId" else KEY_PERSONALES
        val raw = store.get(key) ?: store.get(KEY_PERSONALES) ?: return emptyList()
        return try {
            json.decodeFromString<List<ModeloPersonal>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun guardarPersonalActivo(personal: ModeloPersonal?) {
        _personalActivoFlow.value = personal
        if (personal != null) {
            try {
                store.save(KEY_PERSONAL, json.encodeToString(personal))
            } catch (_: Exception) {}
        } else {
            store.remove(KEY_PERSONAL)
        }
    }

    fun obtenerPersonalActivo(): ModeloPersonal? {
        return _personalActivoFlow.value ?: obtenerPersonalGuardado()
    }

    fun guardarListaPersonal(empresaId: String, lista: List<ModeloPersonal>) {
        _personalesFlow.value = lista
        try {
            val jsonStr = json.encodeToString(lista)
            store.save(KEY_PERSONALES, jsonStr)
            if (empresaId.isNotBlank()) {
                store.save("${KEY_PERSONALES}_$empresaId", jsonStr)
            }
        } catch (_: Exception) {}
    }

    fun obtenerListaPersonal(empresaId: String = ""): List<ModeloPersonal> {
        if (_personalesFlow.value.isNotEmpty()) return _personalesFlow.value
        val lista = obtenerListaGuardada(empresaId)
        _personalesFlow.value = lista
        return lista
    }

    fun obtenerPersonalesActivos(empresaId: String = ""): List<ModeloPersonal> {
        return obtenerListaPersonal(empresaId).filter { it.activo && !it.enDescanso && !it.enVacaciones }
    }

    fun limpiar() {
        _personalActivoFlow.value = null
        _personalesFlow.value = emptyList()
        store.remove(KEY_PERSONAL)
        store.remove(KEY_PERSONALES)
    }
}
