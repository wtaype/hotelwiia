package com.hotelwii.feature.actualizar.tabs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kidev.wiStore
import com.hotelwii.feature.actualizar.ActualizarMotor

/**
 * Permisos — Gestión visual y autónoma de permisos OTA de Android con detección instantánea ON_RESUME
 */
@Composable
fun Permisos(context: Context = LocalContext.current) {
    val store = remember { wiStore(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estado en tiempo real del permiso del sistema operativo
    var tienePermisoInstalar by remember {
        mutableStateOf(ActualizarMotor.tienePermisoInstalar(context))
    }

    // ⚡ DETECTOR EN TIEMPO REAL ON_RESUME: Apenas regresas de Ajustes, evalúa en 0.1ms
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                tienePermisoInstalar = ActualizarMotor.tienePermisoInstalar(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val launcherAjustes = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        tienePermisoInstalar = ActualizarMotor.tienePermisoInstalar(context)
    }

    var notificacionesOta by remember {
        mutableStateOf(store.getBool(ActualizarMotor.KEY_PERMISO_NOTIF, true))
    }

    var segundoPlanoOta by remember {
        mutableStateOf(store.getBool(ActualizarMotor.KEY_PERMISO_BG, true))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TARJETA HERO DE PERMISOS
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
                            imageVector = WiIcons.Security,
                            contentDescription = null,
                            tint = WiCss.bt,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Permisos del Sistema OTA",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiCss.tx
                        )
                        Text(
                            text = "Configuración autónoma sin alertas repetitivas",
                            fontSize = 12.sp,
                            color = WiCss.tx3
                        )
                    }
                }
            }
        }

        // PERMISO 1: INSTALACIÓN DE PAQUETES (CRÍTICO)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = WiCss.wb),
            border = BorderStroke(1.dp, WiCss.brd.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Instalar Actualizaciones Directas",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = WiCss.tx
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Permite a HotelWii reemplazar versiones automáticamente en 1 solo clic.",
                            fontSize = 12.sp,
                            color = WiCss.tx3,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (tienePermisoInstalar) {
                        Box(
                            modifier = Modifier
                                .background(WiCss.success.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                .border(1.dp, WiCss.success.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = WiIcons.Check,
                                    contentDescription = null,
                                    tint = WiCss.success,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Activo",
                                    color = WiCss.success,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    launcherAjustes.launch(intent)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WiCss.bt)
                        ) {
                            Text(text = "Activar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PERMISO 2: NOTIFICACIONES DE NUEVAS VERSIONES
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
                        text = "Alertas de Nuevas Versiones",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiCss.tx
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Avisa discretamente al recepcionista cuando hay un nuevo release listo.",
                        fontSize = 12.sp,
                        color = WiCss.tx3,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = notificacionesOta,
                    onCheckedChange = {
                        notificacionesOta = it
                        store.saveBool(ActualizarMotor.KEY_PERMISO_NOTIF, it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WiCss.success
                    )
                )
            }
        }

        // PERMISO 3: DESCARGAS EN SEGUNDO PLANO
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
                        text = "Descargas en Segundo Plano",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = WiCss.tx
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Evita que la descarga se detenga si la pantalla del teléfono se apaga.",
                        fontSize = 12.sp,
                        color = WiCss.tx3,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Switch(
                    checked = segundoPlanoOta,
                    onCheckedChange = {
                        segundoPlanoOta = it
                        store.saveBool(ActualizarMotor.KEY_PERMISO_BG, it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = WiCss.success
                    )
                )
            }
        }

        // EXPLICACIÓN DE PRIVACIDAD
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
                Text(
                    text = "Una vez activado el permiso de instalación, todas las actualizaciones futuras se realizarán de forma directa sin volver a solicitar confirmaciones.",
                    fontSize = 12.sp,
                    color = WiCss.tx1,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
