package com.hotelwii.feature.hola

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hotelwii.core.kicss.WiTemaColors
import com.hotelwii.feature.hola.tabs.GeneralTab
import com.hotelwii.feature.hola.tabs.ReniecTab
import com.hotelwii.feature.hola.tabs.SunatTab

@Composable
fun HolaScreen(
    tabActivaIndex: Int = 0,
    temaActual: WiTemaColors,
    onCambiarTema: (WiTemaColors) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (tabActivaIndex) {
            0 -> GeneralTab(
                temaActual = temaActual,
                onCambiarTema = onCambiarTema
            )
            1 -> ReniecTab()
            2 -> SunatTab()
            else -> GeneralTab(
                temaActual = temaActual,
                onCambiarTema = onCambiarTema
            )
        }
    }
}
