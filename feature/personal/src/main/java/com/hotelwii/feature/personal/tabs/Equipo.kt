package com.hotelwii.feature.personal.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.feature.personal.components.TarjetaPersonal
import com.hotelwii.feature.personal.data.ModeloPersonal

/**
 * 👥 Tab 1: Equipo.kt — Catálogo interactivo de colaboradores con switch en tiempo real.
 */
@Composable
fun Equipo(
    personales: List<ModeloPersonal>,
    onIrANuevo: () -> Unit,
    onConmutarActivo: (String, Boolean) -> Unit,
    onEditarPersonal: (ModeloPersonal) -> Unit,
    onEliminarPersonal: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalTrabajadores = personales.size
    val totalActivos = personales.count { it.activo }
    val totalRecepcion = personales.count { it.rol.lowercase() == "recepcion" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. TARJETA ACCIÓN SUPERIOR: [ + Registrar Nuevo Colaborador ]
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(WiCss.wb)
                    .border(1.dp, WiCss.mco.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                    .clickable(onClick = onIrANuevo)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(WiCss.mco.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "Nuevo Colaborador",
                            tint = WiCss.mco,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Registrar Nuevo Colaborador",
                            style = WiText.body,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Asigna recepcionistas, limpieza y PIN de acceso",
                            style = WiText.small,
                            color = WiCss.tx3
                        )
                    }
                }
            }
        }

        // 2. MÉTRICAS DE EQUIPO (KPI CARDS)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiCardPersonal(
                    titulo = "EQUIPO",
                    valor = "$totalTrabajadores",
                    subtitulo = "Registrados",
                    color = WiCss.tx1,
                    modifier = Modifier.weight(1f)
                )
                KpiCardPersonal(
                    titulo = "ACTIVOS",
                    valor = "$totalActivos",
                    subtitulo = "En lista",
                    color = WiCss.success,
                    modifier = Modifier.weight(1f)
                )
                KpiCardPersonal(
                    titulo = "RECEPCIÓN",
                    valor = "$totalRecepcion",
                    subtitulo = "Atendiendo",
                    color = WiCss.mco,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. LISTADO DE COLABORADORES
        if (personales.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(WiCss.wb)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = WiCss.tx3,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Aún no hay colaboradores registrados.",
                            style = WiText.body,
                            color = WiCss.tx2,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Toca el botón superior para agregar el primer miembro del equipo.",
                            style = WiText.small,
                            color = WiCss.tx3
                        )
                    }
                }
            }
        } else {
            items(personales, key = { it.id ?: it.numDoc }) { p ->
                TarjetaPersonal(
                    personal = p,
                    onConmutarActivo = { nuevoActivo ->
                        val idOCodigo = if (!p.id.isNullOrBlank()) p.id else p.numDoc
                        onConmutarActivo(idOCodigo, nuevoActivo)
                    },
                    onEditar = { onEditarPersonal(p) },
                    onEliminar = {
                        val idOCodigo = if (!p.id.isNullOrBlank()) p.id else p.numDoc
                        onEliminarPersonal(idOCodigo)
                    }
                )
            }
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun KpiCardPersonal(
    titulo: String,
    valor: String,
    subtitulo: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WiCss.wb)
            .border(1.dp, WiCss.brd, RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = titulo,
                style = WiText.label,
                color = WiCss.tx3,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = valor,
                style = WiText.h3,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitulo,
                style = WiText.small,
                color = WiCss.tx3
            )
        }
    }
}
