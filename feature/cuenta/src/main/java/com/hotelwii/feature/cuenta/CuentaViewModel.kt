package com.hotelwii.feature.cuenta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.auth.data.CacheSmile
import com.hotelwii.feature.auth.data.SmileModelo
import com.hotelwii.feature.cuenta.api.CuentaApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CuentaUiState(
    val smile: SmileModelo? = null,
    val isLoading: Boolean = false,
    val mensajeExito: String? = null,
    val error: String? = null,
    val temaActivo: String = "paz",
    val nombreEdit: String = "",
    val apellidosEdit: String = "",
    val usuarioEdit: String = "",
    val emailEdit: String = "",
    val bioEdit: String = "",
    val segmentoEdit: String = "",
    val mostrarModalFuturo: Boolean = false,
    val tituloModalFuturo: String = "",
    val mensajeModalFuturo: String = ""
)

/**
 * 🧠 CuentaViewModel.kt — Gestor de estado para el módulo de cuenta, edición de perfil y temas.
 */
class CuentaViewModel(application: Application) : AndroidViewModel(application) {
    private val cacheSmile = CacheSmile.getInstance(application)
    private val api = CuentaApi()

    private val _uiState = MutableStateFlow(CuentaUiState())
    val uiState: StateFlow<CuentaUiState> = _uiState.asStateFlow()

    init {
        val smileInicial = cacheSmile.getSmileGuardado()
        _uiState.update { state ->
            state.copy(
                smile = smileInicial,
                temaActivo = smileInicial?.tema ?: "paz",
                nombreEdit = smileInicial?.nombre ?: "",
                apellidosEdit = smileInicial?.apellidos ?: "",
                usuarioEdit = smileInicial?.usuario ?: "",
                emailEdit = smileInicial?.email ?: "",
                bioEdit = smileInicial?.bio ?: "",
                segmentoEdit = smileInicial?.segmento ?: "negocios"
            )
        }

        viewModelScope.launch {
            cacheSmile.sesionActivaFlow.collect { smileActualizado ->
                if (smileActualizado != null) {
                    _uiState.update { state ->
                        state.copy(
                            smile = smileActualizado,
                            temaActivo = smileActualizado.tema,
                            nombreEdit = smileActualizado.nombre,
                            apellidosEdit = smileActualizado.apellidos,
                            usuarioEdit = smileActualizado.usuario,
                            emailEdit = smileActualizado.email,
                            bioEdit = smileActualizado.bio ?: "",
                            segmentoEdit = smileActualizado.segmento
                        )
                    }
                }
            }
        }
    }

    fun onNombreChange(v: String) = _uiState.update { it.copy(nombreEdit = v) }
    fun onApellidosChange(v: String) = _uiState.update { it.copy(apellidosEdit = v) }
    fun onUsuarioChange(v: String) = _uiState.update { it.copy(usuarioEdit = v.lowercase().trim()) }
    fun onBioChange(v: String) = _uiState.update { it.copy(bioEdit = v) }
    fun onSegmentoChange(v: String) = _uiState.update { it.copy(segmentoEdit = v) }

    fun seleccionarTema(nombreTema: String, onTemaCambiado: (String) -> Unit = {}) {
        val smileActual = _uiState.value.smile ?: return
        val smileNuevo = smileActual.copy(tema = nombreTema.lowercase())

        // 1. Optimistic UI: Guardar localmente (<2ms)
        cacheSmile.guardarSesion(smileNuevo)
        _uiState.update { it.copy(temaActivo = nombreTema.lowercase()) }
        onTemaCambiado(nombreTema.lowercase())

        // 2. Sync Supabase
        viewModelScope.launch {
            api.actualizarTema(smileActual.id, nombreTema)
        }
    }

    fun guardarPerfil() {
        val smileActual = _uiState.value.smile ?: return
        val state = _uiState.value

        _uiState.update { it.copy(isLoading = true, mensajeExito = null, error = null) }

        val smileActualizado = smileActual.copy(
            nombre = state.nombreEdit,
            apellidos = state.apellidosEdit,
            usuario = state.usuarioEdit,
            bio = state.bioEdit.ifBlank { null },
            segmento = state.segmentoEdit
        )

        // 1. Optimistic UI: Guardar localmente
        cacheSmile.guardarSesion(smileActualizado)

        // 2. Sync Supabase
        viewModelScope.launch {
            val res = api.actualizarPerfil(
                id = smileActual.id,
                nombre = state.nombreEdit,
                apellidos = state.apellidosEdit,
                usuario = state.usuarioEdit,
                email = smileActual.email,
                bio = state.bioEdit.ifBlank { null },
                tema = smileActual.tema
            )

            if (res.isSuccess) {
                _uiState.update { it.copy(isLoading = false, mensajeExito = "Perfil actualizado exitosamente") }
            } else {
                _uiState.update { it.copy(isLoading = false, mensajeExito = "Perfil guardado localmente") }
            }
        }
    }

    fun abrirModalFuturo(titulo: String, mensaje: String) {
        _uiState.update {
            it.copy(
                mostrarModalFuturo = true,
                tituloModalFuturo = titulo,
                mensajeModalFuturo = mensaje
            )
        }
    }

    fun cerrarModalFuturo() {
        _uiState.update { it.copy(mostrarModalFuturo = false) }
    }

    fun cerrarSesion(onCerrarSesion: () -> Unit) {
        cacheSmile.cerrarSesion()
        onCerrarSesion()
    }
}
