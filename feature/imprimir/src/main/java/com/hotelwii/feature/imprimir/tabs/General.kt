package com.hotelwii.feature.imprimir.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.feature.imprimir.components.Configurando
import com.hotelwii.feature.imprimir.components.Estados
import com.hotelwii.feature.imprimir.modelos.ArqueoCajaTicket
import com.hotelwii.feature.imprimir.modelos.BoletaVenta
import com.hotelwii.feature.imprimir.modelos.DatosBoleta
import com.hotelwii.feature.imprimir.modelos.DatosCheckInTicket
import com.hotelwii.feature.imprimir.modelos.DatosFactura
import com.hotelwii.feature.imprimir.modelos.FacturaVenta
import com.hotelwii.feature.imprimir.modelos.Prueba
import com.hotelwii.feature.imprimir.modelos.TicketCheckIn
import com.hotelwii.feature.imprimir.servicios.ImpresoraDetectada
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora

/**
 * General — Tab 1 del Módulo Imprimir: Estado Inteligente, Búsqueda en Red y Emisión Rápida.
 */
@Composable
fun General(
    config: ModeloConfigImpresora,
    isProbando: Boolean,
    isImprimiendo: Boolean,
    isEscaneando: Boolean,
    impresorasDetectadas: List<ImpresoraDetectada>,
    ultimoMensaje: String?,
    esError: Boolean,
    onEscanearRed: () -> Unit,
    onSeleccionarDetectada: (ImpresoraDetectada) -> Unit,
    onGuardarConfiguracion: (ModeloConfigImpresora) -> Unit,
    onComprobarConexion: () -> Unit,
    onImprimirBytes: (bytes: ByteArray, descripcion: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var modoEdicion by remember(config.estaConfigurada) { mutableStateOf(!config.estaConfigurada) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .padding(16.dp)
    ) {
        if (modoEdicion) {
            Configurando(
                configActual = config,
                isEscaneando = isEscaneando,
                impresorasDetectadas = impresorasDetectadas,
                onEscanearRed = onEscanearRed,
                onSeleccionarDetectada = { detectada ->
                    onSeleccionarDetectada(detectada)
                    modoEdicion = false
                },
                onGuardar = { nuevaConfig ->
                    onGuardarConfiguracion(nuevaConfig)
                    modoEdicion = false
                },
                onCancelar = if (config.estaConfigurada) { { modoEdicion = false } } else null
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta de Estado en Vivo y Pruebas
                Estados(
                    config = config,
                    isProbando = isProbando,
                    isImprimiendo = isImprimiendo,
                    ultimoMensaje = ultimoMensaje,
                    esError = esError,
                    onComprobarConexion = onComprobarConexion,
                    onImprimirPrueba = {
                        val bytes = Prueba.generar(
                            nombreHotel = "HOTEL WII & SUITES",
                            direccion = "Huacachina, Ica - Perú",
                            ruc = "20601234567",
                            anchoPapel = config.anchoPapel,
                            ipOpcional = config.ip
                        )
                        onImprimirBytes(bytes, "Ticket de Prueba")
                    },
                    onEditarConfiguracion = { modoEdicion = true }
                )
            }
        }
    }
}
