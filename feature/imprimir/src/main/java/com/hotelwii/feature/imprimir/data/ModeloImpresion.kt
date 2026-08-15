package com.hotelwii.feature.imprimir.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * 📦 ModeloImpresion.kt — Modelo de datos serializable para la tabla `cola_impresiones` de Supabase.
 */
@Serializable
data class ModeloImpresion(
    val id: String = UUID.randomUUID().toString(),
    @SerialName("userId") val userId: String = "",
    @SerialName("empresa_id") val empresaId: String = "",
    @SerialName("venta_id") val ventaId: String? = null,
    val tipo: String = "prueba", // 'boleta', 'factura', 'checkin', 'precuenta', 'arqueo', 'prueba'
    val titulo: String = "",
    val data: JsonObject = buildJsonObject {},
    val estado: String = "pendiente", // 'pendiente', 'impreso', 'error'
    @SerialName("ip_destino") val ipDestino: String = "192.168.0.110",
    @SerialName("impreso_por") val impresoPor: String = "",
    @SerialName("impreso_fecha") val impresoFecha: String? = null,
    @SerialName("error_mensaje") val errorMensaje: String = "",
    val creado: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(Date()),
    val actualizado: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(Date())
)
