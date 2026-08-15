package com.hotelwii.feature.actualizar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.actualizar.api.ActualizarApi
import com.hotelwii.feature.actualizar.api.ModeloVersion
import com.hotelwii.feature.actualizar.lib.HashValidador
import com.hotelwii.feature.actualizar.lib.InstaladorApk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * ⚡ ActualizarViewModel — Coordinador reactivo del ciclo de vida de actualización OTA en HotelWii
 */
class ActualizarViewModel(application: Application) : AndroidViewModel(application) {

    val versionInstalada: String = ActualizarApi.getInstalledVersionName(application)
    val versionCodeInstalado: Int = ActualizarApi.getInstalledVersionCode(application)

    private val _uiState = MutableStateFlow<ActualizarUiState>(
        ActualizarUiState.Inactivo(versionInstalada, versionCodeInstalado)
    )
    val uiState: StateFlow<ActualizarUiState> = _uiState.asStateFlow()

    /**
     * Comprueba de manera asíncrona si existe una nueva versión en Cloudflare R2
     */
    fun verificarNovedades() {
        if (_uiState.value is ActualizarUiState.Comprobando || _uiState.value is ActualizarUiState.Descargando) {
            return
        }

        _uiState.value = ActualizarUiState.Comprobando

        viewModelScope.launch {
            val resultado = ActualizarApi.consultarVersionRemota(getApplication())
            resultado.fold(
                onSuccess = { infoRemota ->
                    if (infoRemota != null) {
                        _uiState.value = ActualizarUiState.ActualizacionDisponible(infoRemota)
                    } else {
                        _uiState.value = ActualizarUiState.AlDia(versionInstalada)
                    }
                },
                onFailure = { error ->
                    _uiState.value = ActualizarUiState.Error(
                        error.localizedMessage ?: "No se pudo conectar con el servidor de actualizaciones."
                    )
                }
            )
        }
    }

    /**
     * Descarga el APK en segundo plano con notificación de progreso y verificación SHA-256
     */
    fun descargarEInstalar(info: ModeloVersion) {
        _uiState.value = ActualizarUiState.Descargando(
            progreso = 0f,
            mbDescargados = "Iniciando descarga..."
        )

        viewModelScope.launch {
            val resultadoDescarga = ActualizarApi.descargarApk(
                context = getApplication(),
                apkUrl = info.apkUrl,
                onProgress = { ratio, mbTexto ->
                    _uiState.value = ActualizarUiState.Descargando(
                        progreso = ratio,
                        mbDescargados = mbTexto
                    )
                }
            )

            resultadoDescarga.fold(
                onSuccess = { apkFile ->
                    // Validar integridad SHA-256 si la nube proporcionó el hash
                    val esValido = HashValidador.validar(apkFile, info.sha256)
                    if (!esValido) {
                        _uiState.value = ActualizarUiState.Error(
                            "La integridad del archivo no coincide. Intenta nuevamente."
                        )
                        return@fold
                    }

                    _uiState.value = ActualizarUiState.ListoParaInstalar(
                        apkFile = apkFile,
                        versionName = info.versionName
                    )

                    // Invocar automáticamente el instalador nativo
                    InstaladorApk.instalar(getApplication(), apkFile)
                },
                onFailure = { err ->
                    _uiState.value = ActualizarUiState.Error(
                        "Error al descargar la actualización: ${err.localizedMessage ?: "Fallo de red"}"
                    )
                }
            )
        }
    }

    /**
     * Invoca manualmente el instalador del APK descargado
     */
    fun ejecutarInstalador(apkFile: File) {
        InstaladorApk.instalar(getApplication(), apkFile)
    }

    /**
     * Restablece el estado para volver a comprobar
     */
    fun restablecer() {
        _uiState.value = ActualizarUiState.Inactivo(versionInstalada, versionCodeInstalado)
    }
}
