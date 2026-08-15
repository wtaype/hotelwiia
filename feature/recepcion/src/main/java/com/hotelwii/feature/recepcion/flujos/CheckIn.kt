package com.hotelwii.feature.recepcion.flujos

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiComprimir
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.feature.recepcion.components.Fijo
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloVenta
import com.hotelwii.feature.recepcion.servicios.GeminiService
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 📸 CheckIn.kt — Pantalla Maestra de Check-In con Campo Celular / WhatsApp y Liquidación Automática por Noches.
 */
@Composable
fun CheckIn(
    habitacion: ModeloHabitacion,
    onCerrar: () -> Unit,
    onConfirmarCheckIn: (ModeloVenta) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService(context) }

    // Estados de Formulario Huésped
    var tipoDoc by remember { mutableStateOf("dni") }
    var numDoc by remember { mutableStateOf("") }
    var clienteNombre by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("Perú") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var cantidadStr by remember { mutableStateOf("1") }
    var noches by remember { mutableIntStateOf(1) }
    var montoAdelantoStr by remember { mutableStateOf("0.00") }
    var notas by remember { mutableStateOf("") }
    var tipoComprobantePref by remember { mutableStateOf("nota_venta") }

    // Fechas de Estadía Automáticas Seguras
    val ahora = remember { LocalDateTime.now() }
    val formatterEntrada = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a", Locale.forLanguageTag("es-PE")) }
    val formatterSalida = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag("es-PE")) }

    val fechaIngresoTexto = remember(ahora) { ahora.format(formatterEntrada) }
    val fechaSalidaEstimada = "${ahora.plusDays(noches.toLong()).format(formatterSalida)} 12:00 PM"

    // Estados de Fotos y Compresión
    var fotoFrenteBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var fotoFrenteBytes by remember { mutableStateOf<ByteArray?>(null) }
    var infoCompresionFrente by remember { mutableStateOf<String?>(null) }

    var fotoReversoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var fotoReversoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var infoCompresionReverso by remember { mutableStateOf<String?>(null) }

    var modoCamaraCaptura by remember { mutableStateOf("frente") } // "frente" o "reverso"
    var mensajeScanInfo by remember { mutableStateOf<String?>(null) }
    var estaEscaneando by remember { mutableStateOf(false) }

    // Toggle de Sección Avanzada
    var mostrarSeccionAvanzada by remember { mutableStateOf(false) }

    // Cálculos de Facturación en Vivo (Tarifa x Noches)
    val tarifaNoche = habitacion.precio
    val totalHospedaje = tarifaNoche * noches
    val adelanto = montoAdelantoStr.toDoubleOrNull() ?: 0.0
    val saldoPendiente = (totalHospedaje - adelanto).coerceAtLeast(0.0)

    // Launcher de Cámara real
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            // ⚡ 1. Compresión instantánea con WiComprimir
            val resultadoComp = WiComprimir.comprimir(bitmap, maxDimension = 1280, calidad = 85)

            if (modoCamaraCaptura == "frente") {
                fotoFrenteBitmap = resultadoComp.bitmap
                fotoFrenteBytes = resultadoComp.bytes
                val ahorroPct = if (resultadoComp.originalSizeKb > 0) {
                    ((resultadoComp.originalSizeKb - resultadoComp.compressedSizeKb) * 100 / resultadoComp.originalSizeKb).coerceAtLeast(0)
                } else 90
                infoCompresionFrente = "${resultadoComp.originalSizeKb} KB ➡️ ${resultadoComp.compressedSizeKb} KB (-$ahorroPct%)"

                // 🤖 2. Extracción IA automática directa del frontal
                scope.launch {
                    estaEscaneando = true
                    mensajeScanInfo = "Analizando documento con Gemini IA..."
                    val res = geminiService.procesarFotoDocumentoConGemini(resultadoComp.bitmap, tipoDoc)
                    estaEscaneando = false
                    if (res.esExitoso) {
                        numDoc = res.numDoc
                        clienteNombre = res.clienteNombre
                        nacionalidad = res.nacionalidad
                        tipoDoc = res.tipoDoc
                        if (res.fechaNacimiento.isNotBlank()) fechaNacimiento = res.fechaNacimiento
                        mensajeScanInfo = res.mensajeInfo
                    } else {
                        mensajeScanInfo = res.mensajeInfo
                    }
                }
            } else {
                fotoReversoBitmap = resultadoComp.bitmap
                fotoReversoBytes = resultadoComp.bytes
                val ahorroPct = if (resultadoComp.originalSizeKb > 0) {
                    ((resultadoComp.originalSizeKb - resultadoComp.compressedSizeKb) * 100 / resultadoComp.originalSizeKb).coerceAtLeast(0)
                } else 90
                infoCompresionReverso = "${resultadoComp.originalSizeKb} KB ➡️ ${resultadoComp.compressedSizeKb} KB (-$ahorroPct%)"
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            mensajeScanInfo = "Permiso de cámara requerido para capturar el documento."
        }
    }

    Fijo(
        onCerrar = onCerrar,
        onVolver = onCerrar,
        titulo = "Check-In Habitación ${habitacion.numero}",
        subtitulo = "${habitacion.piso} • ${habitacion.tipo} • S/ ${String.format("%.2f", habitacion.precio)} / noche",
        icono = Icons.Rounded.Person
    ) {
        // =========================================================================
        // 📸 SECCIÓN 1: FOTOS DEL DOCUMENTO (FRONTAL CON IA & REVERSO)
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "1. Fotos del Documento de Identidad",
                    style = WiText.body,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                // Mensaje de estado IA / Escaneo
                if (estaEscaneando) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = WiCss.mco,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Extrayendo datos del DNI con Gemini IA...",
                            style = WiText.small,
                            color = WiCss.mco,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (mensajeScanInfo != null) {
                    Text(
                        text = mensajeScanInfo!!,
                        style = WiText.small,
                        color = if (mensajeScanInfo!!.startsWith("Error") || mensajeScanInfo!!.startsWith("No")) WiCss.error else WiCss.success
                    )
                }

                // Ranuras táctiles para Frontal (con IA automática) y Reverso
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Ranura Frontal (Captura + Escaneo IA Automático)
                    SlotDocumento(
                        titulo = "Tomar Frontal",
                        subtitulo = "Completa datos con IA",
                        bitmap = fotoFrenteBitmap,
                        infoCompresion = infoCompresionFrente,
                        onClickCapturar = {
                            modoCamaraCaptura = "frente"
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Ranura Reverso
                    SlotDocumento(
                        titulo = "Tomar Reverso",
                        subtitulo = "Foto posterior (Opcional)",
                        bitmap = fotoReversoBitmap,
                        infoCompresion = infoCompresionReverso,
                        onClickCapturar = {
                            modoCamaraCaptura = "reverso"
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // =========================================================================
        // 👤 SECCIÓN 2: DATOS DEL HUÉSPED TITULAR (AUTOCOMPLETADOS POR IA)
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.brd, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "2. Datos del Huésped Titular",
                    style = WiText.body,
                    color = WiCss.tx1,
                    fontWeight = FontWeight.Bold
                )

                WiSelect(
                    options = listOf("DNI (Perú)", "RUC", "Pasaporte (Extranjero)", "Carnet de Extranjería (C.E.)"),
                    selectedOption = when (tipoDoc) {
                        "dni" -> "DNI (Perú)"
                        "ruc" -> "RUC"
                        "pasaporte" -> "Pasaporte (Extranjero)"
                        "ce" -> "Carnet de Extranjería (C.E.)"
                        else -> "DNI (Perú)"
                    },
                    onOptionSelected = { op ->
                        tipoDoc = when (op) {
                            "DNI (Perú)" -> "dni"
                            "RUC" -> "ruc"
                            "Pasaporte (Extranjero)" -> "pasaporte"
                            "Carnet de Extranjería (C.E.)" -> "ce"
                            else -> "dni"
                        }
                    },
                    label = "Tipo de Documento"
                )

                WiField(
                    value = numDoc,
                    onValueChange = { numDoc = it },
                    label = "Número de Documento / DNI",
                    leadingIcon = Icons.Rounded.Lock,
                    modifier = Modifier.fillMaxWidth()
                )

                WiField(
                    value = clienteNombre,
                    onValueChange = { clienteNombre = it },
                    label = "Nombres y Apellidos del Huésped",
                    leadingIcon = Icons.Rounded.Person,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiField(
                        value = nacionalidad,
                        onValueChange = { nacionalidad = it },
                        label = "Nacionalidad / País",
                        modifier = Modifier.weight(1f)
                    )

                    WiField(
                        value = cantidadStr,
                        onValueChange = { cantidadStr = it },
                        label = "Cantidad Huéspedes",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WiField(
                        value = fechaNacimiento,
                        onValueChange = { fechaNacimiento = it },
                        label = "Fecha Nacimiento",
                        leadingIcon = Icons.Rounded.DateRange,
                        modifier = Modifier.weight(1f)
                    )

                    WiField(
                        value = celular,
                        onValueChange = { celular = it },
                        label = "Celular / WhatsApp",
                        leadingIcon = WiIcons.Phone,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // =========================================================================
        // 🗓️ SECCIÓN 3: ESTADÍA & FACTURACIÓN EN VIVO (CHECK-IN, NOCHES Y CHECK-OUT)
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(WiCss.mco.copy(alpha = 0.08f))
                .border(1.dp, WiCss.mco.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "3. Estadía & Facturación",
                    style = WiText.body,
                    color = WiCss.mco,
                    fontWeight = FontWeight.Bold
                )

                // Fechas Automáticas de Check-In y Check-Out
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Check-In
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.wb)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Fecha Check-In", style = WiText.small, color = WiCss.tx3)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = fechaIngresoTexto,
                                style = WiText.body,
                                color = WiCss.tx1,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Check-Out Calculado
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.wb)
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Fecha Check-Out", style = WiText.small, color = WiCss.tx3)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = fechaSalidaEstimada,
                                style = WiText.body,
                                color = WiCss.mco,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Selector de Noches de Estadía con Botones + / -
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Noches de Estadía",
                            style = WiText.body,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "S/ ${String.format("%.2f", tarifaNoche)} por noche",
                            style = WiText.small,
                            color = WiCss.tx3
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Botón Restar Noche
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WiCss.inp)
                                .clickable { if (noches > 1) noches-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "—",
                                style = WiText.h4,
                                color = if (noches > 1) WiCss.tx1 else WiCss.tx3,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "$noches ${if (noches == 1) "noche" else "noches"}",
                            style = WiText.h4,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.Bold
                        )

                        // Botón Sumar Noche
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(WiCss.mco.copy(alpha = 0.15f))
                                .clickable { noches++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = "Sumar Noche",
                                tint = WiCss.mco,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Total Hospedaje Calculado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total Hospedaje ($noches ${if (noches == 1) "noche" else "noches"}):", style = WiText.small, color = WiCss.tx2)
                    Text(
                        text = "S/ ${String.format("%.2f", totalHospedaje)}",
                        style = WiText.h4,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                }

                WiField(
                    value = montoAdelantoStr,
                    onValueChange = { montoAdelantoStr = it },
                    label = "Monto Adelanto Recibido (S/)",
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saldo al Check-Out:",
                        style = WiText.body,
                        color = WiCss.tx1,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ ${String.format("%.2f", saldoPendiente)}",
                        style = WiText.h3,
                        color = if (saldoPendiente > 0) WiCss.tx1 else WiCss.success,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // =========================================================================
        // ⚙️ SECCIÓN 4: TOGGLE DESPLEGABLE DE OPCIONES AVANZADAS & NOTAS
        // =========================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(WiCss.wb)
                .border(1.dp, WiCss.brd, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarSeccionAvanzada = !mostrarSeccionAvanzada }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Opciones Avanzadas & Notas Internas",
                        style = WiText.small,
                        color = WiCss.tx2,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = if (mostrarSeccionAvanzada) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = WiCss.tx3,
                        modifier = Modifier.size(20.dp)
                    )
                }

                AnimatedVisibility(visible = mostrarSeccionAvanzada) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Spacer(Modifier.height(2.dp))

                        WiSelect(
                            options = listOf("Nota de Venta (Interno)", "Boleta Electrónica (B001)", "Factura Electrónica (F001)"),
                            selectedOption = when (tipoComprobantePref) {
                                "nota_venta" -> "Nota de Venta (Interno)"
                                "boleta" -> "Boleta Electrónica (B001)"
                                "factura" -> "Factura Electrónica (F001)"
                                else -> "Nota de Venta (Interno)"
                            },
                            onOptionSelected = { op ->
                                tipoComprobantePref = when (op) {
                                    "Nota de Venta (Interno)" -> "nota_venta"
                                    "Boleta Electrónica (B001)" -> "boleta"
                                    "Factura Electrónica (F001)" -> "factura"
                                    else -> "nota_venta"
                                }
                            },
                            label = "Comprobante Preferido para Cierre"
                        )

                        WiField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = "Notas internas (ej: Huésped VIP, cama extra)",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Espacio para scroll cómodo sobre el teclado virtual
        Spacer(Modifier.height(8.dp))

        // =========================================================================
        // 🚀 BOTÓN PRINCIPAL DE CONFIRMACIÓN A ANCHO COMPLETO (100%)
        // =========================================================================
        WiButton(
            text = "Confirmar Check-In",
            onClick = {
                val v = ModeloVenta(
                    habitacionId = habitacion.id ?: "",
                    tipoDoc = tipoDoc,
                    numDoc = numDoc.trim(),
                    clienteNombre = clienteNombre.trim().ifBlank { "Huésped Hab. ${habitacion.numero}" },
                    celular = celular.trim(),
                    nacionalidad = nacionalidad.trim(),
                    fechaNacimiento = fechaNacimiento.trim(),
                    cantidad = cantidadStr.toIntOrNull() ?: 1,
                    noches = noches,
                    fechaIngreso = fechaIngresoTexto,
                    fechaSalida = fechaSalidaEstimada,
                    tarifa = tarifaNoche,
                    montoAlquiler = totalHospedaje,
                    montoAdelanto = adelanto,
                    montoTotal = totalHospedaje,
                    tipoComprobante = tipoComprobantePref,
                    observaciones = notas.trim()
                )
                onConfirmarCheckIn(v)
            },
            variant = WiButtonVariant.Primary,
            icon = Icons.Rounded.Check,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
    }
}

/**
 * 🖼️ SlotDocumento: Ranura interactiva amigable ("Tomar Frontal" / "Tomar Reverso").
 */
@Composable
private fun SlotDocumento(
    titulo: String,
    subtitulo: String,
    bitmap: Bitmap?,
    infoCompresion: String?,
    onClickCapturar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(WiCss.bg)
            .border(1.dp, if (bitmap != null) WiCss.mco.copy(alpha = 0.4f) else WiCss.brd, RoundedCornerShape(14.dp))
            .clickable(onClick = onClickCapturar)
            .padding(10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(
                    text = infoCompresion ?: "Comprimido",
                    style = WiText.label,
                    color = WiCss.success,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Toca para cambiar foto",
                    style = WiText.label,
                    color = WiCss.tx3
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WiCss.inp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = WiIcons.PhotoCamera,
                            contentDescription = null,
                            tint = WiCss.mco,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = titulo,
                            style = WiText.small,
                            color = WiCss.tx1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = subtitulo,
                    style = WiText.label,
                    color = WiCss.tx3
                )
            }
        }
    }
}
