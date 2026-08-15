package com.hotelwii.feature.personal.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.personal.data.ModeloPersonal

/**
 * 🪪 TarjetaPersonal.kt — Tarjeta interactiva de colaborador con switch de activación instantánea.
 */
@Composable
fun TarjetaPersonal(
    personal: ModeloPersonal,
    onConmutarActivo: (Boolean) -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iniciales = personal.nombre.trim().split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .uppercase()
        .ifBlank { "P" }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Fila Superior: Avatar + Datos Principales + Switch Activo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar + Nombre y DNI
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar Circular con Iniciales
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (personal.activo) WiCss.mco.copy(alpha = 0.15f) else WiCss.inp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            style = WiText.body,
                            color = if (personal.activo) WiCss.mco else WiCss.tx3,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column {
                        Text(
                            text = personal.nombre.ifBlank { "Sin nombre" },
                            style = WiText.body,
                            color = if (personal.activo) WiCss.tx1 else WiCss.tx3,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (personal.numDoc.isNotBlank()) {
                                Text(
                                    text = "DNI: ${personal.numDoc}",
                                    style = WiText.small,
                                    color = WiCss.tx3
                                )
                            }
                            if (personal.celular.isNotBlank()) {
                                Text(
                                    text = "•  ${personal.celular}",
                                    style = WiText.small,
                                    color = WiCss.tx3
                                )
                            }
                        }
                    }
                }

                // Switch Operativo (Activo / Descanso)
                Column(horizontalAlignment = Alignment.End) {
                    Switch(
                        checked = personal.activo,
                        onCheckedChange = onConmutarActivo,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = WiCss.wb,
                            checkedTrackColor = WiCss.success,
                            uncheckedThumbColor = WiCss.wb,
                            uncheckedTrackColor = WiCss.inp
                        )
                    )
                    Text(
                        text = if (personal.activo) "Activo" else "Inactivo",
                        fontSize = 10.sp,
                        color = if (personal.activo) WiCss.success else WiCss.tx3,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Fila Inferior: Rol + Botones de Acción (Editar / Eliminar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge de Rol
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BadgeRol(rol = personal.rol)
                    if (personal.pin.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WiCss.inp)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "PIN: ${personal.pin}",
                                fontSize = 10.sp,
                                color = WiCss.tx3,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Botones Compactos
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Editar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiCss.mco.copy(alpha = 0.12f))
                            .clickable(onClick = onEditar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar",
                            tint = WiCss.mco,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Botón Eliminar
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WiCss.error.copy(alpha = 0.12f))
                            .clickable(onClick = onEliminar),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Eliminar",
                            tint = WiCss.error,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
