package com.hotelwii.feature.empresas.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSelectAvance
import com.hotelwii.core.kidev.WiSelectOption
import com.hotelwii.core.kidev.WiSwitch
import com.hotelwii.feature.empresas.data.ModeloEmpresa

/**
 * 📝 FormularioEmpresa.kt — Formulario 100% Sincronizado con public.empresas integrado con WiSelect 10/10 (Sin Emojis).
 */
@Composable
fun FormularioEmpresa(
    empresaExistente: ModeloEmpresa? = null,
    isBuscandoRuc: Boolean = false,
    isGuardando: Boolean = false,
    onConsultarRuc: (String, (String, String, String, String, String, String) -> Unit) -> Unit,
    onGuardar: (ModeloEmpresa) -> Unit,
    onCancelar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var ruc by remember(empresaExistente) { mutableStateOf(empresaExistente?.ruc ?: "") }
    var nombreComercial by remember(empresaExistente) { mutableStateOf(empresaExistente?.nombreComercial ?: "") }
    var razonSocial by remember(empresaExistente) { mutableStateOf(empresaExistente?.razonSocial ?: "") }
    var direccion by remember(empresaExistente) { mutableStateOf(empresaExistente?.direccion ?: "") }
    var departamento by remember(empresaExistente) { mutableStateOf(empresaExistente?.departamento ?: "") }
    var provincia by remember(empresaExistente) { mutableStateOf(empresaExistente?.provincia ?: "") }
    var distrito by remember(empresaExistente) { mutableStateOf(empresaExistente?.distrito ?: "") }
    var ubigeo by remember(empresaExistente) { mutableStateOf(empresaExistente?.ubigeo ?: "") }
    var telefono by remember(empresaExistente) { mutableStateOf(empresaExistente?.telefono ?: "") }
    var celular by remember(empresaExistente) { mutableStateOf(empresaExistente?.celular ?: "") }
    var email by remember(empresaExistente) { mutableStateOf(empresaExistente?.email ?: "") }
    var logoUrl by remember(empresaExistente) { mutableStateOf(empresaExistente?.logo ?: "") }
    var moneda by remember(empresaExistente) { mutableStateOf(empresaExistente?.moneda ?: "PEN") }
    var activo by remember(empresaExistente) { mutableStateOf(empresaExistente?.esEmpresaActiva ?: true) }
    var estado by remember(empresaExistente) { mutableStateOf(empresaExistente?.estado ?: "activo") }

    var mostrarMasOpciones by remember(empresaExistente) {
        mutableStateOf(!telefono.isNullOrBlank() || !email.isNullOrBlank() || !ubigeo.isNullOrBlank() || !logoUrl.isNullOrBlank() || !departamento.isNullOrBlank())
    }

    val opcionesMoneda = remember {
        listOf(
            WiSelectOption("PEN", "PEN (S/) - Soles Peruanos", "Moneda nacional del Perú"),
            WiSelectOption("USD", "USD ($) - Dólares Americanos", "Moneda internacional")
        )
    }

    val opcionesEstado = remember {
        listOf(
            WiSelectOption("activo", "Habilitado / Operativo", "Hotel activo para recepción y reservas"),
            WiSelectOption("inactivo", "Inactivo Temporal", "Pausado temporalmente"),
            WiSelectOption("suspendido", "Suspendido", "Suspendido por administración")
        )
    }

    val isRucValido = ruc.trim().length == 11 && ruc.all { it.isDigit() }
    val isNombreValido = nombreComercial.trim().length >= 2

    fun dispararBusquedaSunat() {
        if (isRucValido) {
            onConsultarRuc(ruc) { rSoc, dir, dep, prov, dist, ubig ->
                if (rSoc.isNotBlank()) razonSocial = rSoc
                if (nombreComercial.isBlank()) nombreComercial = rSoc
                if (dir.isNotBlank()) direccion = dir
                if (dep.isNotBlank()) departamento = dep
                if (prov.isNotBlank()) provincia = prov
                if (dist.isNotBlank()) distrito = dist
                if (ubig.isNotBlank()) ubigeo = ubig
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (empresaExistente == null) "Registrar Nuevo Hotel" else "Editar Hotel / Empresa",
            style = WiText.h3,
            color = WiCss.tx1,
            fontWeight = FontWeight.Bold
        )

        // 1. RUC del Hotel (Auto-trigger al 11° dígito + Ícono Lupa Clickable)
        WiField(
            value = ruc,
            onValueChange = { inputStr ->
                if (inputStr.length <= 11 && inputStr.all { c -> c.isDigit() }) {
                    ruc = inputStr
                    if (inputStr.length == 11) {
                        dispararBusquedaSunat()
                    }
                }
            },
            label = "RUC del Hotel (11 dígitos)",
            leadingIcon = WiIcons.Building,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isSuccess = isRucValido,
            isError = ruc.isNotBlank() && !isRucValido,
            errorMessage = if (ruc.isNotBlank() && !isRucValido) "El RUC debe tener 11 dígitos" else null,
            trailingIcon = {
                if (isBuscandoRuc) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = WiCss.mco,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = { dispararBusquedaSunat() }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = "Buscar RUC SUNAT",
                            tint = if (isRucValido) WiCss.mco else WiCss.tx3
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 2. Nombre Comercial del Hotel (Obligatorio)
        WiField(
            value = nombreComercial,
            onValueChange = { nombreComercial = it },
            label = "Nombre Comercial del Hotel *",
            leadingIcon = WiIcons.Building,
            isSuccess = isNombreValido,
            modifier = Modifier.fillMaxWidth()
        )

        // 3. Razón Social SUNAT
        WiField(
            value = razonSocial,
            onValueChange = { razonSocial = it },
            label = "Razón Social SUNAT",
            leadingIcon = Icons.Rounded.Info,
            modifier = Modifier.fillMaxWidth()
        )

        // 4. Dirección Fiscal del Hotel
        WiField(
            value = direccion,
            onValueChange = { direccion = it },
            label = "Dirección Fiscal del Hotel",
            leadingIcon = Icons.Rounded.LocationOn,
            modifier = Modifier.fillMaxWidth()
        )

        // 5. Celular / WhatsApp Recepción
        WiField(
            value = celular,
            onValueChange = { celular = it },
            label = "Celular / WhatsApp Recepción",
            leadingIcon = Icons.Rounded.Call,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )

        // 6. Selector WiSelect de Moneda Principal
        WiSelectAvance(
            selectedValue = moneda,
            options = opcionesMoneda,
            onOptionSelected = { moneda = it.value },
            label = "Moneda Principal del Hotel",
            modifier = Modifier.fillMaxWidth()
        )

        // 7. Selector WiSelect de Estado Administrativo
        WiSelectAvance(
            selectedValue = estado,
            options = opcionesEstado,
            onOptionSelected = { option ->
                estado = option.value
                activo = option.value == "activo"
            },
            label = "Estado Administrativo",
            modifier = Modifier.fillMaxWidth()
        )

        // Toggle Expandible para Opciones Adicionales
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(WiCss.inp.copy(alpha = 0.5f))
                .clickable { mostrarMasOpciones = !mostrarMasOpciones }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (mostrarMasOpciones) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.Add,
                        contentDescription = null,
                        tint = WiCss.mco,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (mostrarMasOpciones) "Ocultar Opciones Adicionales" else "Más Opciones (Ubicación, Logo, Fijo, Email, Ubigeo)",
                        style = WiText.body,
                        color = WiCss.mco,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Icon(
                    imageVector = if (mostrarMasOpciones) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = WiCss.mco,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Bloque Expandible con Ubicación Desglosada
        AnimatedVisibility(
            visible = mostrarMasOpciones,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiField(
                        value = departamento,
                        onValueChange = { departamento = it },
                        label = "Departamento",
                        leadingIcon = Icons.Rounded.Place,
                        modifier = Modifier.weight(1f)
                    )

                    WiField(
                        value = provincia,
                        onValueChange = { provincia = it },
                        label = "Provincia",
                        leadingIcon = Icons.Rounded.Place,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiField(
                        value = distrito,
                        onValueChange = { distrito = it },
                        label = "Distrito",
                        leadingIcon = Icons.Rounded.Place,
                        modifier = Modifier.weight(1f)
                    )

                    WiField(
                        value = ubigeo,
                        onValueChange = { ubigeo = it },
                        label = "Ubigeo SUNAT (6 dgt)",
                        leadingIcon = Icons.Rounded.Place,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                WiField(
                    value = logoUrl,
                    onValueChange = { logoUrl = it },
                    label = "URL del Logo del Hotel",
                    leadingIcon = Icons.Rounded.Share,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth()
                )

                WiField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = "Teléfono Fijo Recepción",
                    leadingIcon = Icons.Rounded.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                WiField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo Electrónico Institucional",
                    leadingIcon = Icons.Rounded.Email,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Switch Operativo del Hotel
        WiSwitch(
            checked = activo,
            onCheckedChange = { 
                activo = it 
                estado = if (it) "activo" else "inactivo"
            },
            label = "Habilitado para reservas y check-in",
            sublabel = if (activo) "El hotel está visible y listo para operar" else "Hotel desactivado temporalmente",
            activeTrackColor = WiCss.success
        )

        Spacer(Modifier.height(6.dp))

        // Botones de Acción (Guardar / Cancelar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (empresaExistente != null) {
                WiButton(
                    text = "Cancelar",
                    onClick = onCancelar,
                    containerColor = WiCss.inp,
                    modifier = Modifier.weight(1f)
                )
            }

            WiButton(
                text = if (empresaExistente == null) "Guardar Hotel" else "Actualizar Hotel",
                onClick = {
                    if (isNombreValido) {
                        val dto = (empresaExistente ?: ModeloEmpresa()).copy(
                            nombreComercial = nombreComercial.trim(),
                            razonSocial = razonSocial.trim(),
                            ruc = ruc.trim(),
                            direccion = direccion.trim(),
                            departamento = departamento.trim(),
                            provincia = provincia.trim(),
                            distrito = distrito.trim(),
                            ubigeo = ubigeo.trim(),
                            telefono = telefono.trim(),
                            celular = celular.trim(),
                            email = email.trim(),
                            logo = logoUrl.trim(),
                            moneda = moneda,
                            activo = activo,
                            estado = estado
                        )
                        onGuardar(dto)
                    }
                },
                loading = isGuardando,
                icon = Icons.Rounded.Check,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
