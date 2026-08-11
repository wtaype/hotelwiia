// widev/Swipes.kt — Librería utilitaria para gestos premium (deslizamiento/swipe)
package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.abs

/**
 * wiSwipe — Modificador reutilizable de Compose para detectar gestos de deslizamiento (swipe).
 *
 * Características premium:
 * - Detección inercial (Fling): Si el gesto dura menos de 250ms y recorre al menos 80px (~50dp),
 *   se activa el callback de inmediato.
 * - Detección normal (Lento): Requiere superar un umbral de 200px (~120dp).
 * - Multi-direccional: Soporta Left (Izquierda), Right (Derecha), Up (Arriba) y Down (Abajo).
 * - Callbacks enriquecidos: Cada acción recibe la duración en milisegundos y la velocidad física (px/ms).
 */
fun Modifier.wiSwipe(
    onLeft: ((durationMs: Long, velocity: Float) -> Unit)? = null,
    onRight: ((durationMs: Long, velocity: Float) -> Unit)? = null,
    onUp: ((durationMs: Long, velocity: Float) -> Unit)? = null,
    onDown: ((durationMs: Long, velocity: Float) -> Unit)? = null,
    consumirLeft: Boolean = onLeft != null,
    consumirRight: Boolean = onRight != null,
    consumirUp: Boolean = onUp != null,
    consumirDown: Boolean = onDown != null
): Modifier = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val eventDown = awaitPointerEvent()
            val changeDown = eventDown.changes.first()
            val pointerId = changeDown.id
            val timeStart = System.currentTimeMillis()
            val posStart = changeDown.position
            
            var totalDragX = 0f
            var totalDragY = 0f
            var lastChange = changeDown
            
            // Seguir el rastro del dedo
            while (true) {
                val eventMove = awaitPointerEvent()
                val changeMove = eventMove.changes.firstOrNull { it.id == pointerId }
                if (changeMove == null || !changeMove.pressed) break
                
                val dragAmountX = changeMove.position.x - changeMove.previousPosition.x
                val dragAmountY = changeMove.position.y - changeMove.previousPosition.y
                totalDragX += dragAmountX
                totalDragY += dragAmountY
                
                // Consumir selectivamente según dirección predominante
                if (abs(totalDragX) > abs(totalDragY)) {
                    if (totalDragX < -15f && consumirLeft) {
                        changeMove.consume()
                    } else if (totalDragX > 15f && consumirRight) {
                        changeMove.consume()
                    }
                } else {
                    if (totalDragY < -15f && consumirUp) {
                        changeMove.consume()
                    } else if (totalDragY > 15f && consumirDown) {
                        changeMove.consume()
                    }
                }
                lastChange = changeMove
            }
            
            val durationMs = System.currentTimeMillis() - timeStart
            val totalDistanceX = lastChange.position.x - posStart.x
            val totalDistanceY = lastChange.position.y - posStart.y
            
            // Fling rápido vs Arrastre lento
            val thresholdDistanceFling = 80f // 50dp aprox
            val thresholdDistanceNormal = 200f // 120dp aprox
            
            val isFling = durationMs < 250
            val requiredDistance = if (isFling) thresholdDistanceFling else thresholdDistanceNormal
            
            // Ejecutar el callback correspondiente
            if (abs(totalDistanceX) > abs(totalDistanceY)) {
                // Dirección horizontal predominante
                val velocity = if (durationMs > 0) abs(totalDistanceX) / durationMs else 0f
                if (totalDistanceX < -requiredDistance) {
                    onLeft?.invoke(durationMs, velocity)
                } else if (totalDistanceX > requiredDistance) {
                    onRight?.invoke(durationMs, velocity)
                }
            } else {
                // Dirección vertical predominante
                val velocity = if (durationMs > 0) abs(totalDistanceY) / durationMs else 0f
                if (totalDistanceY < -requiredDistance) {
                    onUp?.invoke(durationMs, velocity)
                } else if (totalDistanceY > requiredDistance) {
                    onDown?.invoke(durationMs, velocity)
                }
            }
        }
    }
}

