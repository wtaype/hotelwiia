package com.hotelwii.core.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HotelSessionState(
    val hotelNombre: String = "Hotel Huacachina Oasis",
    val usuarioNombre: String = "Recepcionista Admin",
    val hotelId: String = "hotel-001",
    val isConectado: Boolean = true
)

object SessionManager {
    private val _sessionState = MutableStateFlow(HotelSessionState())
    val sessionState: StateFlow<HotelSessionState> = _sessionState.asStateFlow()

    fun actualizarSession(hotelNombre: String, usuarioNombre: String) {
        _sessionState.value = _sessionState.value.copy(
            hotelNombre = hotelNombre,
            usuarioNombre = usuarioNombre
        )
    }
}
