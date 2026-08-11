package com.hotelwii.core.kidev

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 💫 FadeMain.kt — Wrapper atómico de transición Fade-In sútil (200ms) para el contenido central de sub-pestañas.
 * Permite que el contenido aparezca con suavidad sin demorar la respuesta instantánea (0ms) de la barra superior Tabs.kt.
 */
@Composable
fun FadeMain(
    targetState: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearOutSlowInEasing
                )
            ) togetherWith fadeOut(
                animationSpec = tween(
                    durationMillis = 100
                )
            )
        },
        modifier = modifier,
        label = "FadeMainTransition"
    ) { index ->
        content(index)
    }
}
