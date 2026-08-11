package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WiSpin(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    size: Dp = 24.dp,
    strokeWidth: Dp = 2.dp
) {
    if (visible) {
        CircularProgressIndicator(
            color = WiCss.mco,
            strokeWidth = strokeWidth,
            modifier = modifier.size(size)
        )
    }
}

