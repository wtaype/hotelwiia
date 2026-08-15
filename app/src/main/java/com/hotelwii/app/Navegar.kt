package com.hotelwii.app

import androidx.compose.runtime.Composable
import com.hotelwii.app.components.Modulo
import com.hotelwii.core.kicss.WiTemaColors
import com.hotelwii.core.kicss.obtenerTemaColors
import com.hotelwii.feature.actualizar.ActualizarPantalla
import com.hotelwii.feature.auth.AuthPantalla
import com.hotelwii.feature.cuenta.CuentaPantalla
import com.hotelwii.feature.empresas.EmpresaPantalla
import com.hotelwii.feature.hola.HolaScreen
import com.hotelwii.feature.recepcion.RecepcionPantalla

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
            AuthPantalla(
                onAuthExitosa = {
                    rutasState.navegarA(Seo.PANTALLA_INICIAL)
                }
            )
        }
        "habitaciones" -> {
            RecepcionPantalla(
                tabActivaIndex = tabActivaIndex,
                tabVisibleActual = rutasState.tabActivaIndex,
                onSeleccionarTab = { index ->
                    rutasState.seleccionarTab(index)
                }
            )
        }
        "cuenta" -> {
            CuentaPantalla(
                tabActivaIndex = tabActivaIndex,
                onSeleccionarTab = { index ->
                    rutasState.seleccionarTab(index)
                },
                onCerrarSesion = {
                    rutasState.navegarA("auth")
                },
                onTemaCambiado = { nombreTema ->
                    onTemaCambiado(obtenerTemaColors(nombreTema))
                },
                onNavegarRuta = { ruta ->
                    rutasState.navegarA(ruta)
                }
            )
        }
        "empresas" -> {
            EmpresaPantalla(
                tabActivaIndex = tabActivaIndex,
                onSeleccionarTab = { index ->
                    rutasState.seleccionarTab(index)
                }
            )
        }
        "actualizar" -> {
            ActualizarPantalla(
                tabActivaIndex = tabActivaIndex
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
