package com.hotelwii.core.kidev

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss

/**
 * 📦 WiMain.kt — Contenedor Marco Principal de Contenido con fondo WiCss.wb y borde Glass (WiCss.glassBrd).
 * Altura mínima 80vh (~560dp) y alineación superior (Arrangement.Top) para una presencia visual impecable.
 */
@Composable
fun WiMain(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 560.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.glassBrd, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            content = content
        )
    }
}
