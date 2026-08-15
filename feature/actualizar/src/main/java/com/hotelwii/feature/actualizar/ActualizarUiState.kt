package com.hotelwii.feature.actualizar

import com.hotelwii.feature.actualizar.api.ModeloVersion
import java.io.File

/**
 * 📊 ActualizarUiState — Estados reactivos inmutables del Centro Inteligente de Actualizaciones
 */
sealed interface ActualizarUiState {
    /** Estado inicial en reposo con la versión actual instalada */
    data class Inactivo(val versionActual: String, val versionCode: Int) : ActualizarUiState

    /** Consultando la nube de Cloudflare R2 */
    data object Comprobando : ActualizarUiState

    /** La aplicación cuenta con la versión más reciente */
    data class AlDia(val versionActual: String) : ActualizarUiState

    /** Existe una nueva versión lista para descargarse */
    data class ActualizacionDisponible(val info: ModeloVersion) : ActualizarUiState

    /** Descarga activa con métricas de progreso en tiempo real */
    data class Descargando(val progreso: Float, val mbDescargados: String) : ActualizarUiState

    /** Archivo APK verificado y listo para invocar el instalador nativo */
    data class ListoParaInstalar(val apkFile: File, val versionName: String) : ActualizarUiState

    /** Error de red o descarga con mensaje claro de recuperación */
    data class Error(val mensaje: String) : ActualizarUiState
}
