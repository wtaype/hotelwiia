package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object WiAnim {
    val trS: TweenSpec<Float> = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    val trM: TweenSpec<Float> = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    val trF: TweenSpec<Float> = tween(durationMillis = 150, easing = FastOutSlowInEasing)

    fun <T> springSmooth(): SpringSpec<T> = spring(
        dampingRatio = 0.78f, // Bouncy but stable
        stiffness = 380f      // Medium-low speed
    )

    fun <T> springBouncy(): SpringSpec<T> = spring(
        dampingRatio = 0.65f, // Bouncy, playful
        stiffness = 240f
    )
}

