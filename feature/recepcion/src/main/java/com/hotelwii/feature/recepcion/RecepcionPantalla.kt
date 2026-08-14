package com.hotelwii.feature.recepcion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kidev.WiMessengerHost
import com.hotelwii.core.kidev.WiMsgType
import com.hotelwii.core.kidev.rememberWiMessenger
import com.hotelwii.feature.recepcion.flujos.CheckIn
import com.hotelwii.feature.recepcion.flujos.PagoPos
import com.hotelwii.feature.recepcion.flujos.Precuenta
import com.hotelwii.feature.recepcion.flujos.Registro
import com.hotelwii.feature.recepcion.tabs.Habitaciones
import com.hotelwii.feature.recepcion.tabs.Precios
import com.hotelwii.feature.recepcion.tabs.Reservas

/**
 * 🏨 RecepcionPantalla.kt — Pantalla Maestra del Centro de Control de Recepción con 0 Duplicaciones de Modales.
 */
@Composable
fun RecepcionPantalla(
    tabActivaIndex: Int = 0,
    onSeleccionarTab: (Int) -> Unit = {},
    viewModel: RecepcionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messenger = rememberWiMessenger()

    LaunchedEffect(uiState.mensajeExito, uiState.error) {
        uiState.mensajeExito?.let { msg ->
            messenger.Notificacion(msg, type = WiMsgType.Success)
            viewModel.limpiarMensajes()
        }
        uiState.error?.let { err ->
            messenger.Notificacion(err, type = WiMsgType.Error)
            viewModel.limpiarMensajes()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
    ) {
        // Renderizado directo según la página activa del Pager
        when (tabActivaIndex) {
            0 -> Reservas(
                uiState = uiState,
                onRefrescarReservas = { viewModel.cargarDatosLocalFirst(isRefreshManual = true) }
            )
            1 -> Habitaciones(
                uiState = uiState,
                onSeleccionarHabitacion = { hab -> viewModel.seleccionarHabitacionAccion(hab) },
                onNuevaHabitacion = { viewModel.abrirCreacionHabitacion() }
            )
            2 -> Precios(
                uiState = uiState,
                onNuevaHabitacion = { viewModel.abrirCreacionHabitacion() },
                onEditarHabitacion = { hab -> viewModel.abrirEdicionHabitacion(hab) },
                onActualizarPrecioRapido = { id, precio -> viewModel.actualizarPrecioRapido(id, precio) },
                onEliminarHabitacion = { id -> viewModel.eliminarHabitacion(id) }
            )
            else -> Reservas(
                uiState = uiState,
                onRefrescarReservas = { viewModel.cargarDatosLocalFirst(isRefreshManual = true) }
            )
        }

        // Hojas Deslizable y Fijo: Se renderizan EXACTAMENTE UNA SOLA VEZ (en tabActivaIndex == 1) para evitar duplicación de ventanas en el HorizontalPager
        if (tabActivaIndex == 1) {
            if (uiState.mostrarDialogCheckIn && uiState.habitacionSeleccionada != null) {
                CheckIn(
                    habitacion = uiState.habitacionSeleccionada!!,
                    onCerrar = { viewModel.cerrarModales() },
                    onConfirmarCheckIn = { venta -> viewModel.registrarCheckIn(venta) }
                )
            }

            if (uiState.mostrarDialogPrecuenta && uiState.habitacionSeleccionada != null) {
                Precuenta(
                    habitacion = uiState.habitacionSeleccionada!!,
                    venta = uiState.ventaActivaSeleccionada,
                    onCerrar = { viewModel.cerrarModales() },
                    onIrAPagar = { viewModel.abrirPagoPos() }
                )
            }

            if (uiState.mostrarDialogPagoPos && uiState.habitacionSeleccionada != null) {
                PagoPos(
                    habitacion = uiState.habitacionSeleccionada!!,
                    venta = uiState.ventaActivaSeleccionada,
                    onCerrar = { viewModel.cerrarModales() },
                    onConfirmarPagoCheckOut = { metodo, tipoComp, montoFinal ->
                        viewModel.registrarPagoCheckOut(metodo, tipoComp, montoFinal)
                    }
                )
            }

            if (uiState.mostrarFormularioHabitacion) {
                Registro(
                    habitacion = uiState.habitacionSeleccionada,
                    onCerrar = { viewModel.cerrarModales() },
                    onGuardar = { hab -> viewModel.guardarHabitacion(hab) }
                )
            }
        }

        // Notificaciones Flotantes kidev
        WiMessengerHost(messenger = messenger)
    }
}
