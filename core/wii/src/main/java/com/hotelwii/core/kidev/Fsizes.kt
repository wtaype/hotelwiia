package com.hotelwii.core.kidev

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.dpSmart
import com.hotelwii.core.kicss.fPoppins
import kotlin.math.abs

data class WiFontScaleOption(
    val name: String,
    val value: Float
)

val WiFontScaleOptions = listOf(
    WiFontScaleOption("Pequeño", 0.90f),
    WiFontScaleOption("Normal", 1.00f),
    WiFontScaleOption("Grande", 1.10f)
)

@Composable
fun WiFontSizeSelector(
    currentScale: Float,
    onScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(WiCss.inp)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WiFontScaleOptions.forEach { option ->
            val isSelected = abs(currentScale - option.value) < 0.01f
            val background = if (isSelected) WiCss.mco else Color.Transparent
            val textColor = if (isSelected) WiCss.white else WiCss.tx2

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(background)
                    .clickable { onScaleChange(option.value) }
                    .padding(vertical = dpSmart(8f, 0.9f, 12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.name,
                    color = textColor,
                    fontFamily = fPoppins,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
