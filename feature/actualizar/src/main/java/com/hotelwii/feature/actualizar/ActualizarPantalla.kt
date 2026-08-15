package com.hotelwii.feature.actualizar

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hotelwii.core.kidev.FadeMain
import com.hotelwii.core.kidev.wiStore
import com.hotelwii.feature.actualizar.tabs.Actualizar
import com.hotelwii.feature.actualizar.tabs.Ajustes
import com.hotelwii.feature.actualizar.tabs.Permisos
import kotlinx.coroutines.launch
import java.io.File

/**
 * ActualizarPantalla — Controlador del Módulo Actualizar conectado a Seo.kt y Principal.kt
 */
@Composable
fun ActualizarPantalla(
    tabActivaIndex: Int = 0,
    context: Context = LocalContext.current
) {
    val coroutineScope = rememberCoroutineScope()
    val store = remember { wiStore(context) }

    val versionInstalada = remember { ActualizarMotor.getInstalledVersionName(context) }
    var infoRemota by remember { mutableStateOf<WiVersionInfo?>(null) }
    var isComprobando by remember { mutableStateOf(false) }
    var isDescargando by remember { mutableStateOf(false) }
    var progresoDescarga by remember { mutableStateOf(0f) }
    var textoDescarga by remember { mutableStateOf("") }
    var apkDescargado by remember { mutableStateOf<File?>(null) }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun ejecutarComprobacion() {
        if (isComprobando || isDescargando) return
        isComprobando = true
        mensajeError = null

        coroutineScope.launch {
            val resultado = ActualizarMotor.checkUpdate(context)
            resultado.fold(
                onSuccess = { info ->
                    infoRemota = info
                    isComprobando = false
                },
                onFailure = { err ->
                    mensajeError = err.localizedMessage ?: "No se pudo conectar con Cloudflare R2"
                    isComprobando = false
                }
            )
        }
    }

    fun iniciarDescargaEInstalacion(info: WiVersionInfo) {
        if (isDescargando) return
        isDescargando = true
        progresoDescarga = 0f
        textoDescarga = "Conectando con servidor Cloudflare R2..."
        mensajeError = null

        coroutineScope.launch {
            val resultado = ActualizarMotor.downloadApk(
                context = context,
                info = info,
                onProgress = { ratio, texto ->
                    progresoDescarga = ratio
                    textoDescarga = texto
                }
            )

            resultado.fold(
                onSuccess = { file ->
                    apkDescargado = file
                    isDescargando = false

                    // Si modo cero fricción está activo, abrir instalador directamente
                    val ceroFriccion = store.getBool(ActualizarMotor.KEY_CERO_FRICCION, true)
                    if (ceroFriccion) {
                        ActualizarMotor.installApk(context, file)
                    }
                },
                onFailure = { err ->
                    mensajeError = "Error en la descarga: ${err.localizedMessage ?: "Fallo de red"}"
                    isDescargando = false
                }
            )
        }
    }

    // Comprobación automática al iniciar si está habilitada
    LaunchedEffect(Unit) {
        val autoCheck = store.getBool(ActualizarMotor.KEY_AUTO_CHECK, true)
        if (autoCheck) {
            ejecutarComprobacion()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        FadeMain(targetState = tabActivaIndex) { page ->
            when (page) {
                0 -> Actualizar(
                    versionInstalada = versionInstalada,
                    infoRemota = infoRemota,
                    isComprobando = isComprobando,
                    isDescargando = isDescargando,
                    progresoDescarga = progresoDescarga,
                    textoDescarga = textoDescarga,
                    apkDescargado = apkDescargado,
                    mensajeError = mensajeError,
                    onComprobar = { ejecutarComprobacion() },
                    onDescargar = { info -> iniciarDescargaEInstalacion(info) },
                    onInstalar = { file -> ActualizarMotor.installApk(context, file) }
                )
                1 -> Permisos(context = context)
                2 -> Ajustes(context = context)
            }
        }
    }
}
