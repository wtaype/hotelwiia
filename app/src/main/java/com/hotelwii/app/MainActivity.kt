package com.hotelwii.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hotelwii.app.layouts.Principal
import com.hotelwii.core.kicss.WiTemaApp
import com.hotelwii.core.kidev.WiMessengerHost
import com.hotelwii.core.kidev.WiMessengerProvider
import com.hotelwii.core.kidev.rememberWiMessenger

/**
 * ⚡ MainActivity — Actividad principal ultra-delgada conectada a Navegar, RutasState y WiMessenger.
 */
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.hotelwii.core.kicss.FzSmart.init(this)

        setContent {
            val currentTema by mainViewModel.currentTema.collectAsState()
            val rutasState = rememberRutas(rutaInicial = mainViewModel.rutaInicial)
            val messenger = rememberWiMessenger()

            WiTemaApp(themeColors = currentTema) {
                WiMessengerProvider(messenger = messenger) {
                    WiMessengerHost(messenger = messenger)
                    Principal(rutasState = rutasState) { pageIndex ->
                        Navegar(
                            rutasState = rutasState,
                            tabActivaIndex = pageIndex,
                            temaActual = currentTema,
                            onTemaCambiado = { nuevoTema ->
                                mainViewModel.setTema(nuevoTema)
                            }
                        )
                    }
                }
            }
        }
    }
}
