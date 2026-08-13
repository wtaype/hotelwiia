package com.hotelwii.feature.empresas.cards

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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.GoldPill
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * 🏨 EmpresaCard.kt — Tarjeta visual limpia de Hotel/Empresa (Estilo 2) con header integrado,
 * badge HOTEL ACTIVO, botones de acción en cabecera e información concisa.
 */
@Composable
fun EmpresaCard(
    empresa: ModeloEmpresa,
    isActiva: Boolean,
    onSeleccionar: (ModeloEmpresa) -> Unit,
    onEditar: (ModeloEmpresa) -> Unit,
    onEliminar: (ModeloEmpresa) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isActiva) WiCss.mco else WiCss.brd.copy(alpha = 0.5f)
    val estaHabilitada = empresa.esEmpresaActiva

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (estaHabilitada) WiCss.wb else WiCss.wb.copy(alpha = 0.65f))
            .border(
                width = if (isActiva) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onSeleccionar(empresa) }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Fila 1 (Header): Icono + Nombre/Estado + (Badge Actual & Acciones Editar/Eliminar)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Izquierda: Icono de Casa/Hotel y Nombres
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WiCss.mco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Home,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = empresa.nombreComercial.ifBlank { "Hotel Sin Nombre" },
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (estaHabilitada) "• HABILITADA" else "• INACTIVA",
                            style = WiText.tiny,
                            color = if (estaHabilitada) WiCss.success else WiCss.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.width(6.dp))

                // Derecha: Badge de Hotel Activo + Botones Editar y Eliminar ordenados en la cabecera
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (isActiva) {
                        GoldPill(text = "ACTUAL")
                        Spacer(Modifier.width(4.dp))
                    }

                    IconButton(
                        onClick = { onEditar(empresa) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Editar Hotel",
                            tint = WiCss.mco,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onEliminar(empresa) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Eliminar Hotel",
                            tint = WiCss.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Fila 2: Información Fiscal con Icono de Información (RUC & Razón Social)
            if (empresa.ruc.isNotBlank() || empresa.razonSocial.isNotBlank()) {
                val textoRucRS = listOfNotNull(
                    if (empresa.ruc.isNotBlank()) "RUC: ${empresa.ruc}" else null,
                    if (empresa.razonSocial.isNotBlank()) empresa.razonSocial else null
                ).joinToString(" • ")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = textoRucRS,
                        style = WiText.small,
                        color = WiCss.tx2,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Fila 3: Dirección & Ubicación con Icono
            val ubicacionCompleta = listOfNotNull(
                empresa.direccion.takeIf { !it.isNullOrBlank() },
                empresa.ubigeoFormateado.takeIf { it != "Sin Ubigeo" }
            ).joinToString(" — ")

            if (ubicacionCompleta.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = ubicacionCompleta,
                        style = WiText.small,
                        color = WiCss.tx3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Botón de selección cuando la tarjeta no está activa
            if (!isActiva) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(WiCss.mco.copy(alpha = 0.12f))
                            .clickable { onSeleccionar(empresa) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Seleccionar como Hotel Activo",
                            style = WiText.small,
                            color = WiCss.mco,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

