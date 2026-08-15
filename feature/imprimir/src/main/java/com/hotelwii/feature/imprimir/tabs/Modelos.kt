package com.hotelwii.feature.imprimir.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.imprimir.components.TarjetaModelo
import com.hotelwii.feature.imprimir.modelos.ArqueoCajaTicket
import com.hotelwii.feature.imprimir.modelos.BoletaVenta
import com.hotelwii.feature.imprimir.modelos.DatosBoleta
import com.hotelwii.feature.imprimir.modelos.DatosCheckInTicket
import com.hotelwii.feature.imprimir.modelos.DatosFactura
import com.hotelwii.feature.imprimir.modelos.FacturaVenta
import com.hotelwii.feature.imprimir.modelos.PrecuentaTicket
import com.hotelwii.feature.imprimir.modelos.TicketCheckIn
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora

/**
 * Modelos — Tab 2 del Módulo Imprimir: Catálogo de Comprobantes y Pruebas Directas.
 */
@Composable
fun Modelos(
    config: ModeloConfigImpresora,
    isImprimiendo: Boolean,
    onImprimirBytes: (bytes: ByteArray, descripcion: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .imePadding()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Modelos de Comprobantes",
                style = WiText.h3,
                color = WiCss.tx1,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Prueba la impresión física de cada plantilla para verificar formato, fuentes y corte de papel.",
                style = WiText.small,
                color = WiCss.tx2
            )

            // 1. Boleta Electrónica
            TarjetaModelo(
                titulo = "Boleta de Venta Electrónica",
                subtitulo = "Comprobante oficial SUNAT con DNI, detalle de noches/servicios, IGV y QR.",
                etiquetaBadge = "SUNAT B001",
                icono = WiIcons.Receipt,
                isImprimiendo = isImprimiendo,
                onImprimir = {
                    val bytes = BoletaVenta.generar(
                        datos = DatosBoleta(),
                        anchoPapel = config.anchoPapel
                    )
                    onImprimirBytes(bytes, "Boleta Electrónica B001")
                }
            )

            // 2. Factura Electrónica
            TarjetaModelo(
                titulo = "Factura Electrónica",
                subtitulo = "Comprobante corporativo con RUC de empresa, Razón Social, Dirección e IGV.",
                etiquetaBadge = "SUNAT F001",
                icono = WiIcons.Building,
                isImprimiendo = isImprimiendo,
                onImprimir = {
                    val bytes = FacturaVenta.generar(
                        datos = DatosFactura(),
                        anchoPapel = config.anchoPapel
                    )
                    onImprimirBytes(bytes, "Factura Electrónica F001")
                }
            )

            // 3. Voucher de Check-In
            TarjetaModelo(
                titulo = "Voucher de Check-In del Huésped",
                subtitulo = "Ticket de bienvenida con N° de cuarto, clave Wi-Fi, hora de salida y normas.",
                etiquetaBadge = "Recepción",
                icono = WiIcons.Building,
                isImprimiendo = isImprimiendo,
                onImprimir = {
                    val bytes = TicketCheckIn.generar(
                        datos = DatosCheckInTicket(),
                        anchoPapel = config.anchoPapel
                    )
                    onImprimirBytes(bytes, "Voucher de Check-In")
                }
            )

            // 4. Pre-cuenta
            TarjetaModelo(
                titulo = "Estado de Pre-cuenta",
                subtitulo = "Comprobante informativo para que el huésped revise consumos antes de pagar.",
                etiquetaBadge = "Informativo",
                icono = Icons.Rounded.ShoppingCart,
                isImprimiendo = isImprimiendo,
                onImprimir = {
                    val bytes = PrecuentaTicket.generar(
                        anchoPapel = config.anchoPapel
                    )
                    onImprimirBytes(bytes, "Pre-cuenta de Habitación")
                }
            )

            // 5. Arqueo de Caja
            TarjetaModelo(
                titulo = "Arqueo y Cierre de Caja",
                subtitulo = "Reporte de liquidación de turno por recepcionista (Efectivo, Yape, Tarjeta).",
                etiquetaBadge = "Finanzas",
                icono = Icons.Rounded.DateRange,
                isImprimiendo = isImprimiendo,
                onImprimir = {
                    val bytes = ArqueoCajaTicket.generar(
                        anchoPapel = config.anchoPapel
                    )
                    onImprimirBytes(bytes, "Arqueo de Caja del Turno")
                }
            )
        }
    }
}
