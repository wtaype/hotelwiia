package com.hotelwii.feature.hola.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hotelwii.core.data.api.ApiResultado
import com.hotelwii.core.data.api.DecolectaService
import com.hotelwii.core.data.api.DniResponse
import com.hotelwii.core.data.api.RucResponse
import com.hotelwii.core.data.api.TipoCambioResponse
import com.hotelwii.core.kicss.*
import com.hotelwii.core.kidev.WiDev
import kotlinx.coroutines.launch

@Composable
fun DniRucTesterCard(
    apiKey: String = "sk_18254.WIzpCD6G97A8P4bidrMOuwAKj2Lqjjge",
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    var inputDni by remember { mutableStateOf("") }
    var inputRuc by remember { mutableStateOf("") }

    var estadoDni by remember { mutableStateOf<ApiResultado<DniResponse>?>(null) }
    var estadoRuc by remember { mutableStateOf<ApiResultado<RucResponse>?>(null) }
    var estadoTipoCambio by remember { mutableStateOf<ApiResultado<TipoCambioResponse>?>(null) }

    WiDev.CardBase(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = WiCss.wb,
        shapeRadius = 24.dp,
        padding = 20.dp
    ) {
        Text(
            text = "⚡ Búsqueda General Decolecta (RENIEC / SUNAT)",
            style = WiText.h3.copy(color = WiCss.tx)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Consulta oficial de cualquier DNI, RUC o Tipo de Cambio en Perú (RENIEC/SUNAT):",
            style = WiText.small.copy(color = WiCss.tx3)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🆔 SECCIÓN 1: CONSULTA DNI (RENIEC)
        Text(
            text = "🆔 Consulta de DNI (RENIEC)",
            style = WiText.label.copy(color = WiCss.tx)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputDni,
                onValueChange = { if (it.length <= 8) inputDni = it },
                label = { Text("Número DNI (8 dígitos)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WiCss.hv,
                    unfocusedBorderColor = WiCss.brd
                )
            )

            Button(
                onClick = {
                    if (inputDni.length == 8) {
                        estadoDni = ApiResultado.Cargando
                        coroutineScope.launch {
                            estadoDni = DecolectaService.consultarDni(inputDni, apiKey)
                        }
                    }
                },
                enabled = inputDni.length == 8 && estadoDni != ApiResultado.Cargando,
                colors = ButtonDefaults.buttonColors(containerColor = WiCss.hv),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Buscar DNI", color = Color.White)
            }
        }

        // Resultado DNI
        when (val res = estadoDni) {
            is ApiResultado.Cargando -> {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WiCss.hv)
            }
            is ApiResultado.Exito -> {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.bg1)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "✅ Huésped Encontrado en RENIEC:", style = WiText.small.copy(color = WiCss.success, fontWeight = FontWeight.Bold))
                        Text(text = res.data.nombreCompleto, style = WiText.h4.copy(color = WiCss.tx))
                        Text(text = "DNI: ${res.data.documento.ifBlank { inputDni }}", style = WiText.small.copy(color = WiCss.tx3))
                    }
                }
            }
            is ApiResultado.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "❌ ${res.mensaje}", style = WiText.small.copy(color = WiCss.error))
            }
            null -> {}
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 🏢 SECCIÓN 2: CONSULTA RUC (SUNAT)
        Text(
            text = "🏢 Consulta de RUC (SUNAT)",
            style = WiText.label.copy(color = WiCss.tx)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputRuc,
                onValueChange = { if (it.length <= 11) inputRuc = it },
                label = { Text("Número RUC (11 dígitos)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WiCss.hv,
                    unfocusedBorderColor = WiCss.brd
                )
            )

            Button(
                onClick = {
                    if (inputRuc.length == 11) {
                        estadoRuc = ApiResultado.Cargando
                        coroutineScope.launch {
                            estadoRuc = DecolectaService.consultarRuc(inputRuc, apiKey)
                        }
                    }
                },
                enabled = inputRuc.length == 11 && estadoRuc != ApiResultado.Cargando,
                colors = ButtonDefaults.buttonColors(containerColor = WiCss.mco),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Buscar RUC", color = Color.White)
            }
        }

        // Resultado RUC
        when (val res = estadoRuc) {
            is ApiResultado.Cargando -> {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WiCss.mco)
            }
            is ApiResultado.Exito -> {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(WiCss.bg4)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(text = "✅ Empresa Encontrada en SUNAT:", style = WiText.small.copy(color = WiCss.mco, fontWeight = FontWeight.Bold))
                        Text(text = res.data.nombreEmpresa, style = WiText.h4.copy(color = WiCss.tx))
                        Text(text = "Dirección: ${res.data.direccionCompleta}", style = WiText.small.copy(color = WiCss.tx2))
                        Text(text = "Estado: ${res.data.estado ?: "ACTIVO"} | Condición: ${res.data.condicion ?: "HABIDO"}", style = WiText.tiny.copy(color = WiCss.tx3))
                    }
                }
            }
            is ApiResultado.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "❌ ${res.mensaje}", style = WiText.small.copy(color = WiCss.error))
            }
            null -> {}
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 💵 SECCIÓN 3: TIPO DE CAMBIO DÓLAR / SOLES (SUNAT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "💵 Tipo de Cambio Dólar (USD / PEN)", style = WiText.label.copy(color = WiCss.tx))
                Text(text = "Cotización oficial SUNAT / SBS", style = WiText.tiny.copy(color = WiCss.tx3))
            }

            Button(
                onClick = {
                    estadoTipoCambio = ApiResultado.Cargando
                    coroutineScope.launch {
                        estadoTipoCambio = DecolectaService.consultarTipoCambio("", apiKey)
                    }
                },
                enabled = estadoTipoCambio != ApiResultado.Cargando,
                colors = ButtonDefaults.buttonColors(containerColor = WiCss.bt),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Obtener USD/PEN", color = Color.White)
            }
        }

        when (val res = estadoTipoCambio) {
            is ApiResultado.Cargando -> {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WiCss.bt)
            }
            is ApiResultado.Exito -> {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.bg1)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Compra USD", style = WiText.tiny.copy(color = WiCss.tx3))
                            Text(text = "S/ ${res.data.compra}", style = WiText.posAmount.copy(color = WiCss.hv))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WiCss.bg4)
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Venta USD", style = WiText.tiny.copy(color = WiCss.tx3))
                            Text(text = "S/ ${res.data.venta}", style = WiText.posAmount.copy(color = WiCss.mco))
                        }
                    }
                }
            }
            is ApiResultado.Error -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "❌ ${res.mensaje}", style = WiText.small.copy(color = WiCss.error))
            }
            null -> {}
        }
    }
}
