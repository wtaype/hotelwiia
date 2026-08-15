package com.hotelwii.feature.actualizar.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import kotlin.math.roundToInt

/**
 * 📊 ProgresoStreamCard — Barra de progreso suave para streaming de APK en vivo
 */
@Composable
fun ProgresoStreamCard(
    progreso: Float,
    mbDescargados: String,
    modifier: Modifier = Modifier
) {
    val progresoAnimado by animateFloatAsState(
        targetValue = progreso,
        label = "ProgresoAnimado"
    )
    val porcentajeInt = (progresoAnimado * 100).roundToInt().coerceIn(0, 100)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.mco.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Descargando desde Cloudflare R2",
                    style = WiText.body,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$porcentajeInt%",
                    style = WiText.body,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { progresoAnimado },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = WiCss.mco,
                trackColor = WiCss.inp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transferencia segura OTA",
                    style = WiText.small,
                    color = WiCss.tx3
                )
                Text(
                    text = mbDescargados,
                    style = WiText.small,
                    color = WiCss.tx2,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
