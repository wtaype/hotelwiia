package com.hotelwii.feature.cuenta

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kidev.FadeMain
import com.hotelwii.core.kidev.WiDialog
import com.hotelwii.feature.cuenta.tabs.Ajustes
import com.hotelwii.feature.cuenta.tabs.General
import com.hotelwii.feature.cuenta.tabs.Perfil
import com.hotelwii.feature.cuenta.tabs.Seguridad

import androidx.compose.runtime.LaunchedEffect
import com.hotelwii.core.kidev.WiMessengerHost
import com.hotelwii.core.kidev.WiMsgType
import com.hotelwii.core.kidev.rememberWiMessenger

/**
 * 🏨 CuentaPantalla.kt — Pantalla Maestra del Módulo Cuenta integrada con WiMessengerHost (0ms Latency).
 */
@Composable
fun CuentaPantalla(
    tabActivaIndex: Int = 0,
    onSeleccionarTab: (Int) -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onTemaCambiado: (String) -> Unit = {},
    viewModel: CuentaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messenger = rememberWiMessenger()

    LaunchedEffect(uiState.mensajeExito, uiState.error) {
        uiState.mensajeExito?.let { msg ->
            messenger.Notificacion("✨ $msg", type = WiMsgType.Success)
            viewModel.limpiarMensajes()
        }
        uiState.error?.let { err ->
            messenger.Notificacion("❌ $err", type = WiMsgType.Error)
            viewModel.limpiarMensajes()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FadeMain(targetState = tabActivaIndex) { page ->
            when (page) {
                0 -> General(
                    uiState = uiState,
                    onSeleccionarTema = { nombreTema ->
                        viewModel.seleccionarTema(nombreTema, onTemaCambiado)
                    },
                    onCerrarSesion = {
                        viewModel.cerrarSesion(onCerrarSesion)
                    }
                )
                1 -> Perfil(
                    uiState = uiState,
                    viewModel = viewModel
                )
                2 -> Seguridad(
                    uiState = uiState
                )
                3 -> Ajustes()
                else -> General(
                    uiState = uiState,
                    onSeleccionarTema = { nombreTema ->
                        viewModel.seleccionarTema(nombreTema, onTemaCambiado)
                    },
                    onCerrarSesion = {
                        viewModel.cerrarSesion(onCerrarSesion)
                    }
                )
            }
        }

        // Modal Informativo para Funcionalidades a Futuro
        WiDialog(
            show = uiState.mostrarModalFuturo,
            title = uiState.tituloModalFuturo.ifBlank { "Próximamente" },
            text = uiState.mensajeModalFuturo,
            confirmText = "Entendido",
            dismissText = "",
            onConfirm = { viewModel.cerrarModalFuturo() },
            onDismiss = { viewModel.cerrarModalFuturo() }
        )

        // 🌟 Sistema de Notificaciones Flotantes estilo Apple (kidev WiMessengerHost)
        WiMessengerHost(messenger = messenger)
    }
}
