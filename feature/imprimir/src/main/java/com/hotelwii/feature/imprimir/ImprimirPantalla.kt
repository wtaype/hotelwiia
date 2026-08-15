package com.hotelwii.feature.imprimir

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.hotelwii.core.kidev.FadeMain
import com.hotelwii.feature.imprimir.tabs.Ajustes
import com.hotelwii.feature.imprimir.tabs.General
import com.hotelwii.feature.imprimir.tabs.Modelos

/**
 * ImprimirPantalla — Pantalla Principal del Módulo de Impresión Térmica en HotelWii.
 * Integrada con transiciones limpias FadeMain y diseño sincronizado con WiMain / Empresas.
 */
@Composable
fun ImprimirPantalla(
    tabActivaIndex: Int = 0,
    context: Context = LocalContext.current
) {
    val viewModel = remember { ImprimirViewModel(context) }
    val config by viewModel.config.collectAsState()
    val isProbando by viewModel.isProbando.collectAsState()
    val isImprimiendo by viewModel.isImprimiendo.collectAsState()
    val isEscaneando by viewModel.isEscaneando.collectAsState()
    val impresorasDetectadas by viewModel.impresorasDetectadas.collectAsState()
    val ultimoMensaje by viewModel.ultimoMensaje.collectAsState()
    val esError by viewModel.esError.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        FadeMain(targetState = tabActivaIndex) { page ->
            when (page) {
                0 -> General(
                    config = config,
                    isProbando = isProbando,
                    isImprimiendo = isImprimiendo,
                    isEscaneando = isEscaneando,
                    impresorasDetectadas = impresorasDetectadas,
                    ultimoMensaje = ultimoMensaje,
                    esError = esError,
                    onEscanearRed = { viewModel.escanearRedLocal() },
                    onSeleccionarDetectada = { detectada -> viewModel.seleccionarImpresoraDetectada(detectada) },
                    onGuardarConfiguracion = { nuevaConfig -> viewModel.guardarConfiguracion(nuevaConfig) },
                    onComprobarConexion = { viewModel.comprobarConexion() },
                    onImprimirBytes = { bytes, desc -> viewModel.imprimirBytes(bytes, desc) }
                )
                1 -> Modelos(
                    config = config,
                    isImprimiendo = isImprimiendo,
                    onImprimirBytes = { bytes, desc -> viewModel.imprimirBytes(bytes, desc) }
                )
                2 -> Ajustes(
                    config = config,
                    onGuardarConfiguracion = { nuevaConfig -> viewModel.guardarConfiguracion(nuevaConfig) }
                )
            }
        }
    }
}
