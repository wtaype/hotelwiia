package com.hotelwii.feature.empresas

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
import com.hotelwii.feature.empresas.data.ModeloEmpresa
import com.hotelwii.feature.empresas.tabs.Ajustes
import com.hotelwii.feature.empresas.tabs.MisEmpresas
import com.hotelwii.feature.empresas.tabs.NuevoEmpresa

/**
 * 🏨 EmpresaPantalla.kt — Pantalla Maestra del Módulo Empresas / Hoteles.
 * Integra las 3 pestañas (Mis Hoteles, Nuevo Hotel, Ajustes Fiscales SUNAT) con WiMessengerHost y WiDialog modal.
 */
@Composable
fun EmpresaPantalla(
    tabActivaIndex: Int = 0,
    onSeleccionarTab: (Int) -> Unit = {},
    viewModel: EmpresaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val messenger = rememberWiMessenger()

    var hotelAEliminar by remember { mutableStateOf<ModeloEmpresa?>(null) }

    LaunchedEffect(uiState.mensajeExito, uiState.error) {
        uiState.mensajeExito?.let { msg ->
            messenger.Notificacion(msg, type = WiMsgType.Success)
            viewModel.limpiarMensajes()
        }
        uiState.error?.let { err ->
            messenger.Notificacion("❌ $err", type = WiMsgType.Error)
            viewModel.limpiarMensajes()
        }
    }

    if (hotelAEliminar != null) {
        WiDialog(
            show = true,
            title = "Eliminar Hotel",
            text = "¿Estás seguro de que deseas eliminar el hotel '${hotelAEliminar?.nombreComercial}'? Esta acción no se puede deshacer.",
            confirmText = "Eliminar Hotel",
            dismissText = "Cancelar",
            onConfirm = {
                hotelAEliminar?.let { viewModel.eliminarHotel(it) }
                hotelAEliminar = null
            },
            onDismiss = { hotelAEliminar = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FadeMain(targetState = tabActivaIndex) { page ->
            when (page) {
                0 -> MisEmpresas(
                    empresas = uiState.empresas,
                    hotelActivoId = uiState.hotelActivo?.id,
                    onSeleccionar = { emp ->
                        viewModel.seleccionarHotelActivo(emp)
                    },
                    onEditar = { emp ->
                        viewModel.prepararEdicion(emp)
                        onSeleccionarTab(1)
                    },
                    onEliminar = { emp ->
                        hotelAEliminar = emp
                    },
                    onRefrescar = {
                        viewModel.cargarEmpresasLocalFirst(isRefreshManual = true)
                    },
                    isRefreshing = uiState.isRefreshing,
                    onIrANuevo = {
                        viewModel.cancelarEdicion()
                        onSeleccionarTab(1)
                    }
                )
                1 -> NuevoEmpresa(
                    empresaExistente = uiState.empresaEdicion,
                    isBuscandoRuc = uiState.isBuscandoRuc,
                    isGuardando = uiState.isLoading,
                    onConsultarRuc = { ruc, callback ->
                        viewModel.consultarRuc(ruc, callback)
                    },
                    onGuardar = { dto ->
                        viewModel.guardarHotel(dto) {
                            onSeleccionarTab(0)
                        }
                    },
                    onCancelar = {
                        viewModel.cancelarEdicion()
                        onSeleccionarTab(0)
                    }
                )
                2 -> Ajustes(
                    empresas = uiState.empresas,
                    hotelSeleccionado = uiState.hotelAjustesSeleccionado ?: uiState.hotelActivo,
                    onSeleccionarHotel = { emp ->
                        viewModel.seleccionarHotelParaAjustes(emp)
                    },
                    isGuardando = uiState.isLoading,
                    onGuardarAjustes = { empId, notaVenta, boleta, factura, sBoleta, sFactura, sNota, impuesto, moneda ->
                        viewModel.guardarAjustesFacturacion(
                            empId, notaVenta, boleta, factura,
                            sBoleta, sFactura, sNota, impuesto, moneda
                        )
                    },
                    onToggleCampo = { emp, campo, nuevoValor ->
                        viewModel.toggleCampoEmpresa(emp, campo, nuevoValor)
                    }
                )
                else -> MisEmpresas(
                    empresas = uiState.empresas,
                    hotelActivoId = uiState.hotelActivo?.id,
                    onSeleccionar = { emp ->
                        viewModel.seleccionarHotelActivo(emp)
                    },
                    onEditar = { emp ->
                        viewModel.prepararEdicion(emp)
                        onSeleccionarTab(1)
                    },
                    onEliminar = { emp ->
                        hotelAEliminar = emp
                    },
                    onRefrescar = {
                        viewModel.cargarEmpresasLocalFirst(isRefreshManual = true)
                    },
                    isRefreshing = uiState.isRefreshing,
                    onIrANuevo = {
                        viewModel.cancelarEdicion()
                        onSeleccionarTab(1)
                    }
                )
            }
        }

        // 🌟 Sistema de Notificaciones Flotantes estilo Apple (kidev WiMessengerHost)
        WiMessengerHost(messenger = messenger)
    }
}
