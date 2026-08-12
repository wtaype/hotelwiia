package com.hotelwii.app

import androidx.compose.runtime.Composable
import com.hotelwii.app.components.Modulo
import com.hotelwii.core.kicss.WiTemaColors
import com.hotelwii.feature.hola.HolaScreen

/**
 * 🧭 Navegar.kt — Enrutador Composable Sincrónico Ultra-Rápido (< 0.1ms).
 * Conmuta entre módulos feature aceptando tabActivaIndex dinámico por página.
 */
@Composable
fun Navegar(
    rutasState: RutasState,
    tabActivaIndex: Int = rutasState.tabActivaIndex,
    temaActual: WiTemaColors,
    onTemaCambiado: (WiTemaColors) -> Unit = {}
) {
    when (rutasState.rutaActual) {
        "hola" -> {
            HolaScreen(
                tabActivaIndex = tabActivaIndex,
                temaActual = temaActual,
                onCambiarTema = onTemaCambiado
            )
        }
        "auth" -> {
            com.hotelwii.feature.auth.AuthPantalla(
                onAuthExitosa = {
                    rutasState.navegarA("hola")
                }
            )
        }
        else -> {

            Modulo(
                rutasState = rutasState,
                tabActivaIndex = tabActivaIndex
            )
        }
    }
}
