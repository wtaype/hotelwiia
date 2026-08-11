package com.hotelwii.feature.hola.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.*
import com.hotelwii.core.kidev.WiDev
import com.hotelwii.core.kidev.WiMain
import com.hotelwii.feature.hola.components.DemoRoomCard
import com.hotelwii.feature.hola.components.ThemeSelectorCard

@Composable
fun GeneralTab(
    temaActual: WiTemaColors,
    onCambiarTema: (WiTemaColors) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WiMain {
            // Cabecera Principal Hola HotelWii
            WiDev.CardBase(
                backgroundColor = WiCss.wb,
                shapeRadius = 24.dp,
                padding = 20.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "¡Hola HotelWii! 🏨✨",
                            style = WiText.h1.copy(color = WiCss.tx)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Super App de Gestión Hotelera - Huacachina, Ica",
                            style = WiText.body.copy(color = WiCss.tx3)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.bg1)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = WiIcons.Building,
                            contentDescription = null,
                            tint = WiCss.hv
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Indicador de Estado de la Arquitectura
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WiDev.BadgeEstado(texto = ":core:wii OK", colorBg = WiCss.success)
                    WiDev.BadgeEstado(texto = ":core:data OK", colorBg = WiCss.success)
                    WiDev.BadgeEstado(texto = ":feature:hola OK", colorBg = WiCss.success)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector Interactivo de Temas (WiTema)
            ThemeSelectorCard(
                temaActual = temaActual,
                onTemaSeleccionado = onCambiarTema
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card de Demostración de Habitación en Huacachina
            Text(
                text = "🏨 Demostración de Componentes de Habitación",
                style = WiText.h3.copy(color = WiCss.tx),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
            )

            DemoRoomCard(
                numeroHabitacion = "201",
                tipoHabitacion = "Suite Matrimonial Vista Dunas",
                precioNoche = "S/ 240 / Noche",
                estado = "DISPONIBLE",
                estadoColor = WiCss.success
            )

            Spacer(modifier = Modifier.height(10.dp))

            DemoRoomCard(
                numeroHabitacion = "202",
                tipoHabitacion = "Doble Personas / Turistas",
                precioNoche = "S/ 190 / Noche",
                estado = "OCUPADA",
                estadoColor = WiCss.error
            )
        }
    }
}
