package com.hotelwii.feature.recepcion.tabs

import androidx.compose.runtime.Composable
import com.hotelwii.feature.recepcion.RecepcionUiState
import com.hotelwii.feature.recepcion.data.ModeloHabitacion

/**
 * Redirección de compatibilidad a Habitaciones.kt (Plural).
 */
@Composable
fun Habitacion(
    uiState: RecepcionUiState,
    onSeleccionarHabitacion: (ModeloHabitacion) -> Unit,
    onNuevaHabitacion: () -> Unit
) {
    Habitaciones(
        uiState = uiState,
        onSeleccionarHabitacion = onSeleccionarHabitacion,
        onNuevaHabitacion = onNuevaHabitacion
    )
}
