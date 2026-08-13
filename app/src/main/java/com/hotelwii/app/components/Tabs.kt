package com.hotelwii.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.app.MetaTab
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText

/**
 * 🧩 Tabs.kt — Barra Enterprise de sub-pestañas Responsivas (0ms delay).
 * Soporta pestañas fijas equilibradas para <= 3 ítems y Desplazamiento por Gestos (Scrollable Pro) para > 3 ítems.
 */
@Composable
fun Tabs(
    tabsList: List<MetaTab>,
    tabActivaIndex: Int,
    onSeleccionarTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tabsList.isEmpty()) return

    val isScrollable = tabsList.size > 3
    val scrollState = rememberScrollState()

    // Scroll automático suave al seleccionar tab cuando es scrollable
    LaunchedEffect(tabActivaIndex) {
        if (isScrollable) {
            val targetScroll = (tabActivaIndex * 120).coerceAtMost(scrollState.maxValue)
            scrollState.animateScrollTo(targetScroll)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(WiCss.wb)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (isScrollable) Modifier.horizontalScroll(scrollState) else Modifier),
                horizontalArrangement = if (isScrollable) Arrangement.Start else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                tabsList.forEachIndexed { index, tab ->
                    val isSelected = index == tabActivaIndex
                    val textColor = if (isSelected) WiCss.hv else WiCss.tx3
                    val iconColor = if (isSelected) WiCss.hv else WiCss.tx3
                    val indicatorColor = if (isSelected) WiCss.hv else WiCss.wb

                    Column(
                        modifier = Modifier
                            .then(if (!isScrollable) Modifier.weight(1f) else Modifier.padding(horizontal = 14.dp))
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
                                Spacer(Modifier.width(5.dp))
                            }

                            Text(
                                text = tab.titulo,
                                style = WiText.small,
                                color = textColor,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }

                        // Línea activa pegada al borde inferior exacto
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
