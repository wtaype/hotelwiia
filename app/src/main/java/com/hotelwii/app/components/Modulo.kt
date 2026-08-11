package com.hotelwii.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.app.Rutas
import com.hotelwii.app.RutasState
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiMain

/**
 * 📦 Modulo.kt — Vista base corta y elegante en español para módulos en construcción o activos (Mesas, Caja, Inventario, Reportes).
 * Encapsulada dentro de WiMain con diseño glassmorphism de HotelWii.
 */
@Composable
fun Modulo(
    rutasState: RutasState,
    tabActivaIndex: Int = rutasState.tabActivaIndex,
    modifier: Modifier = Modifier
) {
    val meta = Rutas.getMeta(rutasState.rutaActual)
    val tabActual = meta.tabs.getOrNull(tabActivaIndex)
    val tituloTab = tabActual?.titulo ?: meta.titulo

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        WiMain {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header del Módulo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        meta.icono?.let { vector ->
                            Icon(
                                imageVector = vector,
                                contentDescription = null,
                                tint = WiCss.mco,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(
                            text = meta.titulo,
                            style = WiText.h3,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    GoldPill(text = "MÓDULO ACTIVO")
                }

                Text(
                    text = meta.subtitulo,
                    style = WiText.small,
                    color = WiCss.tx3
                )

                // Bloque Indicador de Pestaña Activa
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(WiCss.inp)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Sub-pestaña seleccionada: $tituloTab",
                                style = WiText.body,
                                color = WiCss.tx1,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Interfaz lista para conectar con endpoints de datos y transacciones.",
                                style = WiText.small,
                                color = WiCss.tx3
                            )
                        }
                    }
                }

                WiButton(
                    text = "Refrescar Estado de $tituloTab",
                    onClick = { },
                    icon = WiIcons.Refresh,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
