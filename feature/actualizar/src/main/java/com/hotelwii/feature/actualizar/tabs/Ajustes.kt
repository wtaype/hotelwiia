package com.hotelwii.feature.actualizar.tabs

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kidev.wiStore
import com.hotelwii.feature.actualizar.ActualizarMotor

/**
 * Ajustes — Preferencias de actualización OTA persistidas con WiStore
 */
@Composable
fun Ajustes(context: Context = LocalContext.current) {
    val store = remember { wiStore(context) }

    var ceroFriccion by remember {
        mutableStateOf(store.getBool(ActualizarMotor.KEY_CERO_FRICCION, true))
    }

    var autoCheck by remember {
        mutableStateOf(store.getBool(ActualizarMotor.KEY_AUTO_CHECK, true))
    }

    var soloWifi by remember {
        mutableStateOf(store.getBool(ActualizarMotor.KEY_SOLO_WIFI, false))
    }

    var limpiarCache by remember {
        mutableStateOf(store.getBool(ActualizarMotor.KEY_LIMPIAR_CACHE, true))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TARJETA HERO DE AJUSTES
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = WiCss.wb),
            border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(WiCss.bt.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = WiIcons.Settings,
                            contentDescription = null,
                            tint = WiCss.bt,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Preferencias de Actualización",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiCss.tx
                        )
                        Text(
                            text = "Personaliza el comportamiento del motor OTA",
                            fontSize = 12.sp,
                            color = WiCss.tx3
                        )
                    }
                }
            }
        }

        // AJUSTE 1: MODO CERO FRICCIÓN
        ItemSwitchAjuste(
            titulo = "Modo Cero Fricción",
            descripcion = "Abre directamente el instalador oficial tras verificar el hash SHA-256 sin pasos intermedios.",
            checked = ceroFriccion,
            onCheckedChange = {
                ceroFriccion = it
                store.saveBool(ActualizarMotor.KEY_CERO_FRICCION, it)
            }
        )

        // AJUSTE 2: COMPROBACIÓN AUTOMÁTICA
        ItemSwitchAjuste(
            titulo = "Comprobar al Iniciar Turno",
            descripcion = "Verifica silenciosamente si hay una nueva versión disponible en Cloudflare al abrir HotelWii.",
            checked = autoCheck,
            onCheckedChange = {
                autoCheck = it
                store.saveBool(ActualizarMotor.KEY_AUTO_CHECK, it)
            }
        )

        // AJUSTE 3: DESCARGA SOLO EN WIFI
        ItemSwitchAjuste(
            titulo = "Descargar Solo en Red WiFi",
            descripcion = "Evita el consumo de datos móviles en planes telefónicos del personal del hotel.",
            checked = soloWifi,
            onCheckedChange = {
                soloWifi = it
                store.saveBool(ActualizarMotor.KEY_SOLO_WIFI, it)
            }
        )

        // AJUSTE 4: LIMPIEZA AUTOMÁTICA DE CACHÉ
        ItemSwitchAjuste(
            titulo = "Limpieza Automática de APKs",
            descripcion = "Elimina los paquetes temporales tras la instalación para mantener libre el almacenamiento.",
            checked = limpiarCache,
            onCheckedChange = {
                limpiarCache = it
                store.saveBool(ActualizarMotor.KEY_LIMPIAR_CACHE, it)
            }
        )

        // INFO DE DISTRIBUCIÓN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WiCss.wb.copy(alpha = 0.6f))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = WiIcons.Info,
                    contentDescription = null,
                    tint = WiCss.bt,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Servidor Oficial: hotelwii.amorwii.com",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = WiCss.tx
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Infraestructura Cloudflare R2 con CDN de latencia ultra baja en Perú.",
                        fontSize = 11.sp,
                        color = WiCss.tx3
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemSwitchAjuste(
    titulo: String,
    descripcion: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WiCss.wb),
        border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WiCss.tx
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = WiCss.tx3,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = WiCss.success
                )
            )
        }
    }
}
