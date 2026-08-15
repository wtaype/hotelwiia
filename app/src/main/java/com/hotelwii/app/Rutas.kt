package com.hotelwii.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 🧭 RutasState — Controlador de estado de navegación reactivo y ultra-rápido (< 0.1ms).
 */
class RutasState(rutaInicial: String = Seo.PANTALLA_INICIAL) {
    var rutaActual by mutableStateOf(rutaInicial)
        private set

    var tabActivaIndex by mutableStateOf(0)
        private set

    var paddingHorizontal by mutableStateOf(10.dp)
        private set

    var paddingVertical by mutableStateOf(6.dp)
        private set

    fun navegarA(nuevaRuta: String) {
        if (rutaActual != nuevaRuta) {
            rutaActual = nuevaRuta
            tabActivaIndex = 0
        }
    }

    fun seleccionarTab(index: Int) {
        tabActivaIndex = index
    }

    fun setContentPadding(horizontal: Dp = 10.dp, vertical: Dp = 6.dp) {
        paddingHorizontal = horizontal
        paddingVertical = vertical
    }
}

@Composable
fun rememberRutas(rutaInicial: String = Seo.PANTALLA_INICIAL): RutasState {
    return remember(rutaInicial) { RutasState(rutaInicial) }
}

/**
 * 🗺️ Rutas — Router dinámico derivado de Seo.kt ordenado numéricamente mediante `orden`.
 */
object Rutas {
    /** Lista de rutas navegables principales ordenadas por `orden` */
    val RUTAS_NAV: List<MetaRuta> = Seo.METADATOS.values
        .filter { it.orden != null }
        .sortedBy { it.orden }

    /** Lista de rutas que se muestran en el Drawer según Seo.kt */
    val RUTAS_DRAWER: List<MetaRuta> = Seo.METADATOS.values
        .filter { it.mostrarMenuLateral }
        .sortedBy { it.orden ?: 99 }

    /** Obtiene los metadatos de una ruta o retorna el valor por defecto */
    fun getMeta(key: String): MetaRuta = Seo.METADATOS[key] ?: Seo.DEFAULT
}
