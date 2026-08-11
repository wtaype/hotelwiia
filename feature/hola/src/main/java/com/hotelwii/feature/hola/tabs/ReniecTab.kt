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
import com.hotelwii.core.data.api.DniResponse
import com.hotelwii.core.kicss.*
import com.hotelwii.core.kidev.WiDev
import com.hotelwii.core.kidev.WiMain
import kotlinx.coroutines.launch

@Composable
fun ReniecTab(
    apiKey: String = "sk_18254.WIzpCD6G97A8P4bidrMOuwAKj2Lqjjge",
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var inputDni by remember { mutableStateOf("") }
    var estadoDni by remember { mutableStateOf<ApiResultado<DniResponse>?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        WiMain {
            Text(
                text = "🆔 Búsqueda Oficial de DNI (RENIEC)",
                style = WiText.h2.copy(color = WiCss.tx)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Consulta en vivo cualquier DNI de 8 dígitos registrado en Perú:",
                style = WiText.small.copy(color = WiCss.tx3)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = WiCss.hv)
                }
                is ApiResultado.Exito -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(WiCss.bg1)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(text = "✅ Huésped Encontrado en RENIEC:", style = WiText.small.copy(color = WiCss.success, fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = res.data.nombreCompleto, style = WiText.h3.copy(color = WiCss.tx))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = "DNI: ${res.data.documento.ifBlank { inputDni }}", style = WiText.body.copy(color = WiCss.tx3))
                        }
                    }
                }
                is ApiResultado.Error -> {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "❌ ${res.mensaje}", style = WiText.small.copy(color = WiCss.error))
                }
                null -> {}
            }
        }
    }
}
