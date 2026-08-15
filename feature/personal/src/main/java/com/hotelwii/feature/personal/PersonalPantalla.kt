package com.hotelwii.feature.personal

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelwii.core.kidev.FadeMain
import com.hotelwii.core.kidev.WiDialog
import com.hotelwii.core.kidev.WiMessengerHost
import com.hotelwii.core.kidev.WiMsgType
import com.hotelwii.core.kidev.rememberWiMessenger
import com.hotelwii.feature.personal.data.ModeloPersonal
import com.hotelwii.feature.personal.tabs.Ajustes
import com.hotelwii.feature.personal.tabs.Equipo
import com.hotelwii.feature.personal.tabs.Nuevo

/**
 * 👥 PersonalPantalla.kt — Pantalla Maestra del Módulo Personal & Equipo.
 * Integra las 3 pestañas (Equipo, Nuevo, Ajustes) con reactividad Local-First (0 ms).
 */
@Composable
fun PersonalPantalla(
    tabActivaIndex: Int = 0,
    onSeleccionarTab: (Int) -> Unit = {},
    viewModel: PersonalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messenger = rememberWiMessenger()

    var personalAEliminar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.mensajeExito, uiState.mensajeError) {
        uiState.mensajeExito?.let { msg ->
            messenger.Notificacion(msg, type = WiMsgType.Success)
            viewModel.limpiarMensajes()
        }
        uiState.mensajeError?.let { err ->
            messenger.Notificacion(err, type = WiMsgType.Error)
            viewModel.limpiarMensajes()
        }
    }

    if (personalAEliminar != null) {
        WiDialog(
            show = true,
            title = "Eliminar Colaborador",
            text = "¿Estás seguro de que deseas eliminar este miembro del equipo? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            dismissText = "Cancelar",
            onConfirm = {
                personalAEliminar?.let { viewModel.eliminarPersonal(it) }
                personalAEliminar = null
            },
            onDismiss = { personalAEliminar = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FadeMain(targetState = tabActivaIndex) { page ->
            when (page) {
                0 -> Equipo(
                    personales = uiState.personales,
                    onIrANuevo = {
                        viewModel.cancelarEdicion()
                        onSeleccionarTab(1)
                    },
                    onConmutarActivo = { idOCodigo, activo ->
                        viewModel.conmutarActivo(idOCodigo, activo)
                    },
                    onEditarPersonal = { p ->
                        viewModel.seleccionarParaEditar(p)
                        onSeleccionarTab(1)
                    },
                    onEliminarPersonal = { idOCodigo ->
                        personalAEliminar = idOCodigo
                    }
                )

                1 -> Nuevo(
                    personalAEditar = uiState.personalEnEdicion,
                    onGuardar = { p ->
                        viewModel.guardarPersonal(p)
                        onSeleccionarTab(0)
                    },
                    onCancelarEdicion = {
                        viewModel.cancelarEdicion()
                        onSeleccionarTab(0)
                    }
                )

                2 -> Ajustes(
                    onGuardarAjustes = {
                        messenger.Notificacion("Preferencias de personal guardadas", type = WiMsgType.Success)
                    }
                )
            }
        }

        WiMessengerHost(messenger = messenger)
    }
}
