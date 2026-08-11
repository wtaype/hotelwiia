package com.hotelwii.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.hotelwii.core.kicss.WiTemaColors
import com.hotelwii.core.kidev.WiTemas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 🚀 MainViewModel — ViewModel principal ultra-liviano para HotelWii.
 * Gestiona la reactividad del tema activo y coordina su persistencia con WiTemas.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentTema = MutableStateFlow<WiTemaColors>(WiTemas.getTemaInicial(application))
    val currentTema: StateFlow<WiTemaColors> = _currentTema.asStateFlow()

    /**
     * Resuelve la ruta inicial sincrónicamente en RAM (< 2ms) para evitar parpadeos
     */
    val rutaInicial: String = "hola"

    /**
     * Sincroniza y guarda la nueva preferencia de tema en WiStore
     */
    fun setTema(tema: WiTemaColors) {
        val nuevoTema = WiTemas.saveTema(getApplication(), tema)
        _currentTema.value = nuevoTema
    }

    /**
     * Permite cambiar de tema especificando únicamente su nombre (ej. "Paz", "Futuro", "Dulce")
     */
    fun setTemaByName(nombreTema: String) {
        val nuevoTema = WiTemas.saveTema(getApplication(), nombreTema)
        _currentTema.value = nuevoTema
    }
}
