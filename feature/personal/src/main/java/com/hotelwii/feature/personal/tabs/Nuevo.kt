package com.hotelwii.feature.personal.tabs

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.personal.data.ModeloPersonal

/**
 * ➕ Tab 2: Nuevo.kt — Formulario de Registro y Edición de Colaboradores (5 Campos Esenciales + PIN Random Editable).
 */
@Composable
fun Nuevo(
    personalAEditar: ModeloPersonal? = null,
    onGuardar: (ModeloPersonal) -> Unit,
    onCancelarEdicion: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Generación inicial de PIN aleatorio (editable y opcional)
    val pinInicial = remember(personalAEditar) {
        personalAEditar?.pin ?: (1000..9999).random().toString()
    }

    var numDoc by remember(personalAEditar) { mutableStateOf(personalAEditar?.numDoc ?: "") }
    var nombre by remember(personalAEditar) { mutableStateOf(personalAEditar?.nombre ?: "") }
    var rol by remember(personalAEditar) { mutableStateOf(personalAEditar?.rol ?: "recepcion") }
    var pin by remember(personalAEditar) { mutableStateOf(pinInicial) }
    var celular by remember(personalAEditar) { mutableStateOf(personalAEditar?.celular ?: "") }
    var correo by remember(personalAEditar) { mutableStateOf(personalAEditar?.correo ?: "") }
    var activo by remember(personalAEditar) { mutableStateOf(personalAEditar?.activo ?: true) }

    var dropdownRolExpandido by remember { mutableStateOf(false) }
    val opcionesRol = listOf(
        "recepcion" to "Recepción (Atención y Cobros)",
        "limpieza" to "Limpieza (Housekeeping)",
        "admin" to "Administrador (Control Total)",
        "caja" to "Caja (Facturación y Pagos)",
        "seguridad" to "Seguridad (Turnos Nocturnos)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ENCABEZADO
        item {
            Column {
                Text(
                    text = if (personalAEditar != null) "Editar Colaborador" else "Registrar Nuevo Colaborador",
                    style = WiText.h3,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Completa los datos del trabajador para recepción y turnos.",
                    style = WiText.small,
                    color = WiCss.tx3
                )
            }
        }

        // 1. DNI Y CELULAR (FILA SIMÉTRICA)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WiField(
                    value = numDoc,
                    onValueChange = { if (it.length <= 12) numDoc = it },
                    label = "DNI / Documento",
                    modifier = Modifier.weight(1f)
                )

                WiField(
                    value = celular,
                    onValueChange = { if (it.length <= 15) celular = it },
                    label = "Celular / WhatsApp",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2. NOMBRES Y APELLIDOS (CAMPO COMPLETO)
        item {
            WiField(
                value = nombre,
                onValueChange = { nombre = it },
                label = "Nombres y Apellidos Completos",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. ROL Y PIN DE 4 DÍGITOS (CON BOTÓN REFRESH RANDOM)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Selector de Rol
                Box(modifier = Modifier.weight(1.2f)) {
                    Column {
                        Text(text = "Cargo / Rol", style = WiText.small, color = WiCss.tx3)
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(WiCss.inp)
                                .clickable { dropdownRolExpandido = true }
                                .padding(horizontal = 12.dp, vertical = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = opcionesRol.firstOrNull { it.first == rol }?.second ?: rol.replaceFirstChar { it.uppercase() },
                                    style = WiText.body,
                                    color = WiCss.tx1,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                                Icon(
                                    imageVector = Icons.Rounded.ArrowDropDown,
                                    contentDescription = null,
                                    tint = WiCss.tx3
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = dropdownRolExpandido,
                        onDismissRequest = { dropdownRolExpandido = false },
                        modifier = Modifier.background(WiCss.wb)
                    ) {
                        opcionesRol.forEach { (clave, etiqueta) ->
                            DropdownMenuItem(
                                text = { Text(etiqueta, style = WiText.body, color = WiCss.tx1) },
                                onClick = {
                                    rol = clave
                                    dropdownRolExpandido = false
                                }
                            )
                        }
                    }
                }

                // PIN de 4 dígitos (Editable / Opcional)
                Row(
                    modifier = Modifier.weight(0.8f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WiField(
                        value = pin,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pin = it },
                        label = "PIN (Opcional)",
                        modifier = Modifier.weight(1f)
                    )

                    // Botón para generar otro PIN aleatorio
                    Box(
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.inp)
                            .clickable { pin = (1000..9999).random().toString() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Generar PIN aleatorio",
                            tint = WiCss.mco,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 4. CORREO OPCIONAL
        item {
            WiField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo Electrónico (Opcional)",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 5. SWITCH: TRABAJADOR ACTIVO
        item {
            WiSwitch(
                checked = activo,
                onCheckedChange = { activo = it },
                label = "Colaborador Activo",
                sublabel = "Habilita a este trabajador para recepción y turnos."
            )
        }

        item { Spacer(Modifier.height(10.dp)) }

        // BOTÓN PRINCIPAL
        item {
            WiButton(
                text = if (personalAEditar != null) "Guardar Cambios" else "Registrar Colaborador",
                onClick = {
                    val p = ModeloPersonal(
                        id = personalAEditar?.id,
                        nombre = nombre.trim(),
                        numDoc = numDoc.trim(),
                        celular = celular.trim(),
                        rol = rol.trim(),
                        pin = pin.trim(),
                        correo = correo.trim(),
                        activo = activo
                    )
                    onGuardar(p)
                },
                variant = WiButtonVariant.Primary,
                icon = WiIcons.Check,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (personalAEditar != null) {
            item {
                WiButton(
                    text = "Cancelar Edición",
                    onClick = onCancelarEdicion,
                    variant = WiButtonVariant.Secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}
