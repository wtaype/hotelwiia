package com.hotelwii.feature.recepcion.flujos

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.WiButton
import com.hotelwii.core.kidev.WiButtonVariant
import com.hotelwii.core.kidev.WiField
import com.hotelwii.core.kidev.WiSelect
import com.hotelwii.feature.recepcion.components.Fijo
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloVenta
import com.hotelwii.feature.recepcion.servicios.LectorPro
import kotlinx.coroutines.launch

/**
 * 📸 CheckIn.kt — Flujo Fijo Dedicado para Check-In con Gemini Vision OCR 100% Real (Sin Mocks).
 * Botón único full-width sin botón redundante de cancelar.
 */
@Composable
fun CheckIn(
    habitacion: ModeloHabitacion,
    onCerrar: () -> Unit,
    onConfirmarCheckIn: (ModeloVenta) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lectorPro = remember { LectorPro(context) }

    var tipoDoc by remember { mutableStateOf("dni") }
    var numDoc by remember { mutableStateOf("") }
    var clienteNombre by remember { mutableStateOf("") }
    var nacionalidad by remember { mutableStateOf("Perú") }
    var cantidadStr by remember { mutableStateOf("1") }
    var montoAdelantoStr by remember { mutableStateOf("0.00") }
    var notas by remember { mutableStateOf("") }
    var mensajeScanInfo by remember { mutableStateOf<String?>(null) }
    var estaEscaneando by remember { mutableStateOf(false) }

    // Launcher de Cámara real para escaneo de DNI con Gemini OCR
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            scope.launch {
                estaEscaneando = true
                mensajeScanInfo = "Analizando fotografía con Gemini IA Vision..."
                val res = lectorPro.procesarFoto(bitmap, tipoDoc)
                estaEscaneando = false
                if (res.esExitoso) {
                    numDoc = res.numDoc
                    clienteNombre = res.clienteNombre
                    nacionalidad = res.nacionalidad
                    tipoDoc = res.tipoDoc
                    mensajeScanInfo = res.mensajeInfo
                } else {
                    mensajeScanInfo = res.mensajeInfo
                }
            }
        }
    }

    // Launcher de Permiso de Cámara
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            mensajeScanInfo = "Permiso de cámara requerido para escanear DNI."
        }
    }

    Fijo(
        onCerrar = onCerrar,
        onVolver = onCerrar,
        titulo = "Check-In Habitación ${habitacion.numero}",
        subtitulo = "Tarifa: S/ ${String.format("%.2f", habitacion.precio)} (${habitacion.tipo})",
        icono = Icons.Rounded.Person
    ) {
        // Botón Escáner Cámara IA OCR LectorPro sin emojis ni mocks
        WiButton(
            text = if (estaEscaneando) "PROCESANDO FOTOGRAFÍA CON GEMINI IA..." else "ESCANEAR DNI / PASAPORTE CON CÁMARA (IA OCR)",
            onClick = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            },
            variant = WiButtonVariant.Secondary,
            icon = WiIcons.PhotoCamera,
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeScanInfo != null) {
            Text(
                text = mensajeScanInfo!!,
                style = WiText.small,
                color = if (mensajeScanInfo!!.startsWith("Error") || mensajeScanInfo!!.startsWith("No")) WiCss.error else WiCss.success
            )
        }

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

        WiField(
            value = montoAdelantoStr,
            onValueChange = { montoAdelantoStr = it },
            label = "Adelanto (S/)",
            modifier = Modifier.fillMaxWidth()
        )

        WiField(
            value = notas,
            onValueChange = { notas = it },
            label = "Notas internas (ej: Almohada extra)",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        // Botón Único Full-Width
        WiButton(
            text = "Confirmar Check-In",
            onClick = {
                val v = ModeloVenta(
                    habitacionId = habitacion.id ?: "",
                    tipoDoc = tipoDoc,
                    numDoc = numDoc.trim(),
                    clienteNombre = clienteNombre.trim().ifBlank { "Huésped Hab. ${habitacion.numero}" },
                    nacionalidad = nacionalidad.trim(),
                    montoAlquiler = habitacion.precio,
                    montoAdelanto = montoAdelantoStr.toDoubleOrNull() ?: 0.0,
                    montoTotal = habitacion.precio,
                    observaciones = notas.trim()
                )
                onConfirmarCheckIn(v)
            },
            variant = WiButtonVariant.Primary,
            icon = Icons.Rounded.Check,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
