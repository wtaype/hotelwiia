package com.hotelwii.feature.hola.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.*

@Composable
fun ThemeSelectorCard(
    temaActual: WiTemaColors,
    onTemaSeleccionado: (WiTemaColors) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "🎨 Selector de Temas WiTema (5 Temas)",
                style = WiText.h3.copy(color = WiCss.tx)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Selecciona un tema para probar el contraste y cambiar la interfaz en vivo:",
                style = WiText.small.copy(color = WiCss.tx3)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HotelWiTemas.forEach { tema ->
                    val isSelected = tema.name == temaActual.name
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(tema.bg)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) tema.hv else tema.brd,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onTemaSeleccionado(tema) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tema.name,
                            style = WiText.small.copy(
                                color = tema.tx,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
