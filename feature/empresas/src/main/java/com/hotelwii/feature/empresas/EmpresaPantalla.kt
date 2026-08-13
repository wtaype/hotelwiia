package com.hotelwii.feature.empresas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hotelwii.core.kidev.FadeMain
import com.hotelwii.core.kidev.WiMessengerHost
import com.hotelwii.core.kidev.WiMsgType
import com.hotelwii.core.kidev.rememberWiMessenger
import com.hotelwii.feature.empresas.tabs.Ajustes
import com.hotelwii.feature.empresas.tabs.MisEmpresas
import com.hotelwii.feature.empresas.tabs.NuevoEmpresa

/**
 * 🏨 EmpresaPantalla.kt — Pantalla Maestra del Módulo Empresas / Hoteles.
 * Integra las 3 pestañas (Mis Hoteles, Nuevo Hotel, Ajustes de Facturación) con WiMessengerHost.
 */
@Composable
fun EmpresaPantalla(
    tabActivaIndex: Int = 0,
    onSeleccionarTab: (Int) -> Unit = {},
    viewModel: EmpresaViewModel = viewModel()
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
                        viewModel.eliminarHotel(emp)
                    },
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
                    onGuardarAjustes = { empId, notaVenta, boleta, factura, impuesto, moneda ->
                        viewModel.guardarAjustesFacturacion(empId, notaVenta, boleta, factura, impuesto, moneda)
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
                        viewModel.eliminarHotel(emp)
                    },
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
