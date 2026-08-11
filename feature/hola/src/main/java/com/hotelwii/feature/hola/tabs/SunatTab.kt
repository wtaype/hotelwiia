package com.hotelwii.feature.hola.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.hotelwii.core.data.api.RucResponse
import com.hotelwii.core.data.api.TipoCambioResponse
import com.hotelwii.core.kicss.*
import com.hotelwii.core.kidev.WiMain
import kotlinx.coroutines.launch

@Composable
fun SunatTab(
    apiKey: String = "sk_18254.WIzpCD6G97A8P4bidrMOuwAKj2Lqjjge",
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var inputRuc by remember { mutableStateOf("") }

    var estadoRuc by remember { mutableStateOf<ApiResultado<RucResponse>?>(null) }
    var estadoTipoCambio by remember { mutableStateOf<ApiResultado<TipoCambioResponse>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WiMain {
            // 🏢 SECCIÓN 1: CONSULTA RUC (SUNAT)
            Text(
                text = "🏢 Consulta Oficial de RUC (SUNAT)",
                style = WiText.h2.copy(color = WiCss.tx)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Consulta cualquier RUC de 11 dígitos registrado en la SUNAT:",
                style = WiText.small.copy(color = WiCss.tx3)
            )

            Spacer(modifier = Modifier.height(14.dp))

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
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WiCss.mco)
                }
                is ApiResultado.Exito -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(WiCss.bg4)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(text = "✅ Empresa Encontrada en SUNAT:", style = WiText.small.copy(color = WiCss.mco, fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = res.data.nombreEmpresa, style = WiText.h3.copy(color = WiCss.tx))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "Dirección: ${res.data.direccionCompleta}", style = WiText.body.copy(color = WiCss.tx2))
                            Text(text = "Estado: ${res.data.estado ?: "ACTIVO"} | Condición: ${res.data.condicion ?: "HABIDO"}", style = WiText.small.copy(color = WiCss.tx3))
                        }
                    }
                }
                is ApiResultado.Error -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "❌ ${res.mensaje}", style = WiText.small.copy(color = WiCss.error))
                }
                null -> {}
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 💵 SECCIÓN 2: TIPO DE CAMBIO DÓLAR / SOLES (SUNAT)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "💵 Tipo de Cambio Dólar (USD / PEN)", style = WiText.h3.copy(color = WiCss.tx))
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
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WiCss.bt)
                }
                is ApiResultado.Exito -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(WiCss.bg1)
                                .padding(14.dp),
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(WiCss.bg4)
                                .padding(14.dp),
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
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "❌ ${res.mensaje}", style = WiText.small.copy(color = WiCss.error))
                }
                null -> {}
            }
        }
    }
}
