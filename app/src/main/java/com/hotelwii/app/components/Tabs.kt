package com.hotelwii.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.app.MetaTab
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText

/**
 * 🧩 Tabs.kt — Barra Enterprise de sub-pestañas 100% Ancho y Plana con respuesta de color instantánea (0ms delay).
 */
@Composable
fun Tabs(
    tabsList: List<MetaTab>,
    tabActivaIndex: Int,
    onSeleccionarTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tabsList.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(WiCss.wb)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                tabsList.forEachIndexed { index, tab ->
                    val isSelected = index == tabActivaIndex
                    val textColor = if (isSelected) WiCss.hv else WiCss.tx3
                    val iconColor = if (isSelected) WiCss.hv else WiCss.tx3
                    val indicatorColor = if (isSelected) WiCss.hv else WiCss.wb

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSeleccionarTab(index) }
                            .padding(top = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            tab.icono?.let { vectorIcon ->
                                Icon(
                                    imageVector = vectorIcon,
                                    contentDescription = tab.titulo,
                                    tint = iconColor,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                            }

                            Text(
                                text = tab.titulo,
                                style = WiText.small,
                                color = textColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }

                        // Línea activa pegada al borde inferior exacto (0 margin)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(indicatorColor)
                        )
                    }
                }
            }

            // Borde Glass divisor inferior unificado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                .background(WiCss.glassBrd.copy(alpha = 0.5f))
            )
        }
    }
}
