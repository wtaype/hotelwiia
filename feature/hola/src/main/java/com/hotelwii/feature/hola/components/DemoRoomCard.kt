package com.hotelwii.feature.hola.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.*
import com.hotelwii.core.kidev.WiDev

@Composable
fun DemoRoomCard(
    numeroHabitacion: String = "102",
    tipoHabitacion: String = "Matrimonial Oasis - Vista a la Laguna",
    precioNoche: String = "S/ 180 / Noche",
    estado: String = "DISPONIBLE",
    estadoColor: Color = WiCss.success,
    modifier: Modifier = Modifier
) {
    WiDev.CardBase(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = WiCss.wb,
        shapeRadius = 20.dp,
        padding = 18.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.bg1),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = WiIcons.Building,
                        contentDescription = null,
                        tint = WiCss.hv
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Habitación $numeroHabitacion",
                        style = WiText.h3.copy(color = WiCss.tx)
                    )
                    Text(
                        text = tipoHabitacion,
                        style = WiText.small.copy(color = WiCss.tx3)
                    )
                }
            }

            WiDev.BadgeEstado(
                texto = estado,
                colorBg = estadoColor
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = precioNoche,
                style = WiText.posAmount.copy(color = WiCss.hv)
            )

            WiDev.BadgeEstado(
                texto = "Huacachina Ica 🌴",
                colorBg = WiCss.inp,
                colorTexto = WiCss.tx2
            )
        }
    }
}
