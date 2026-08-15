package com.hotelwii.feature.personal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.auth.data.CacheSmile
import com.hotelwii.feature.empresas.data.CacheEmpresa
import com.hotelwii.feature.personal.api.PersonalApi
import com.hotelwii.feature.personal.data.CachePersonal
import com.hotelwii.feature.personal.data.ModeloPersonal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalUiState(
    val personales: List<ModeloPersonal> = emptyList(),
    val personalEnEdicion: ModeloPersonal? = null,
    val tabActivaIndex: Int = 0,
    val cargando: Boolean = false,
    val mensajeExito: String? = null,
    val mensajeError: String? = null
)

/**
 * 👥 PersonalViewModel.kt — Gestor de estado reactivo Local-First (0 ms) para el equipo del hotel.
 */
class PersonalViewModel(application: Application) : AndroidViewModel(application) {

    private val cachePersonal = CachePersonal.getInstance(application)
    private val cacheEmpresa = CacheEmpresa.getInstance(application)
    private val cacheSmile = CacheSmile.getInstance(application)
    private val api = PersonalApi

    private val _uiState = MutableStateFlow(PersonalUiState())
    val uiState: StateFlow<PersonalUiState> = _uiState.asStateFlow()

    private val smileId: String
        get() = cacheSmile.getSmileGuardado()?.id ?: ""

    private val empresaId: String
        get() = cacheEmpresa.obtenerEmpresaActiva()?.id ?: ""

    init {
        cargarDatosLocalFirst()
    }

    fun cargarDatosLocalFirst() {
        val empId = empresaId

        // 1. Carga instantánea desde Memoria RAM / Disco (< 1ms)
        val listaLocal = cachePersonal.obtenerListaPersonal(empId)
        _uiState.update {
            it.copy(personales = listaLocal)
        }

        // 2. Refresco en segundo plano con Supabase
        if (empId.isNotBlank()) {
            viewModelScope.launch {
                val resultado = api.obtenerPersonalPorEmpresa(empId)
                resultado.fold(
                    onSuccess = { listaRemota ->
                        cachePersonal.guardarListaPersonal(empId, listaRemota)
                        _uiState.update {
                            it.copy(personales = listaRemota)
                        }
                    },
                    onFailure = {
                        // Se mantienen los datos locales
                    }
                )
            }
        }
    }

    fun guardarPersonal(personal: ModeloPersonal) {
        val empId = empresaId
        val sId = smileId

        val dto = personal.copy(
            smileId = if (personal.smileId.isNotBlank()) personal.smileId else sId,
            empresaId = if (personal.empresaId.isNotBlank()) personal.empresaId else empId
        )

        // 1. LocalFirst instantáneo (0 ms)
        val listaActual = _uiState.value.personales.toMutableList()
        val index = listaActual.indexOfFirst {
            (!dto.id.isNullOrBlank() && it.id == dto.id) ||
            (dto.numDoc.isNotBlank() && it.numDoc == dto.numDoc)
        }

        if (index >= 0) {
            listaActual[index] = dto
        } else {
            listaActual.add(0, dto)
        }

        _uiState.update {
            it.copy(
                personales = listaActual,
                personalEnEdicion = null,
                tabActivaIndex = 0, // Volver a la pestaña Equipo
                mensajeExito = "Colaborador guardado correctamente."
            )
        }
        cachePersonal.guardarListaPersonal(empId, listaActual)

        // 2. Sincronización en 2do plano con Supabase
        viewModelScope.launch {
            val res = api.guardarPersonal(dto)
            res.fold(
                onSuccess = {
                    cargarDatosLocalFirst()
                },
                onFailure = { error ->
                    _uiState.update { it.copy(mensajeError = error.message) }
                }
            )
        }
    }

    fun conmutarActivo(idOCodigo: String, activo: Boolean) {
        val empId = empresaId
        val listaActual = _uiState.value.personales.toMutableList()
        val index = listaActual.indexOfFirst { it.id == idOCodigo || it.numDoc == idOCodigo }
        if (index < 0) return

        val modificado = listaActual[index].copy(activo = activo)
        listaActual[index] = modificado

        // 1. LocalFirst instantáneo (0 ms)
        _uiState.update {
            it.copy(
                personales = listaActual,
                mensajeExito = if (activo) "Colaborador habilitado." else "Colaborador en descanso/inactivo."
            )
        }
        cachePersonal.guardarListaPersonal(empId, listaActual)

        // 2. Sync en segundo plano
        viewModelScope.launch {
            if (!modificado.id.isNullOrBlank()) {
                api.conmutarEstadoActivo(modificado.id, activo)
            }
        }
    }

    fun eliminarPersonal(idOCodigo: String) {
        val empId = empresaId
        val listaActual = _uiState.value.personales.filterNot { it.id == idOCodigo || it.numDoc == idOCodigo }

        // 1. LocalFirst instantáneo (< 1ms)
        _uiState.update {
            it.copy(
                personales = listaActual,
                mensajeExito = "Colaborador eliminado."
            )
        }
        cachePersonal.guardarListaPersonal(empId, listaActual)

        // 2. Sync Supabase delete
        viewModelScope.launch {
            api.eliminarPersonal(idOCodigo)
        }
    }

    fun seleccionarParaEditar(personal: ModeloPersonal) {
        _uiState.update {
            it.copy(
                personalEnEdicion = personal,
                tabActivaIndex = 1 // Cambiar a pestaña Nuevo/Editar
            )
        }
    }

    fun cancelarEdicion() {
        _uiState.update {
            it.copy(
                personalEnEdicion = null,
                tabActivaIndex = 0
            )
        }
    }

    fun seleccionarTab(index: Int) {
        _uiState.update {
            it.copy(tabActivaIndex = index)
        }
    }

    fun limpiarMensajes() {
        _uiState.update {
            it.copy(mensajeExito = null, mensajeError = null)
        }
    }
}
