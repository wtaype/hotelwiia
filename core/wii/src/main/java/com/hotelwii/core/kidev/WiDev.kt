package com.hotelwii.core.kidev

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText

/**
 * 🛠️ WiDev — Helper unificado para CardsBase y Badges de Estado en HotelWii.
 */
object WiDev {

    @Composable
    fun CardBase(
        modifier: Modifier = Modifier,
        backgroundColor: Color = WiCss.wb,
        shapeRadius: Dp = 16.dp,
        padding: Dp = 16.dp,
        content: @Composable ColumnScope.() -> Unit
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(shapeRadius))
                .background(backgroundColor)
                .border(1.dp, WiCss.brd, RoundedCornerShape(shapeRadius))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }

    @Composable
    fun BadgeEstado(
        texto: String,
        colorBg: Color = WiCss.success,
        colorTexto: Color = Color.White,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(colorBg)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                style = WiText.tiny.copy(
                    color = colorTexto,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
