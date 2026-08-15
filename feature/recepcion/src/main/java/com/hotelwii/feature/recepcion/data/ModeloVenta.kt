package com.hotelwii.feature.recepcion.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 💳 ModeloVenta.kt — DTO para la tabla public.ventas en Supabase (Hospedajes, Check-in/out, Extranjeros y SUNAT).
 * 100% Sincronizado con recursos-hotelwii/tablas/ventas-tabla.txt
 */
@Serializable
data class ModeloVenta(
    @SerialName("id") val id: String? = null,
    @SerialName("userId") val smileId: String = "",
    @SerialName("empresa_id") val empresaId: String = "",
    @SerialName("habitacion_id") val habitacionId: String = "",
    @SerialName("personal_id") val personalId: String? = null,
    @SerialName("tipo_doc") val tipoDoc: String = "dni", // 'dni', 'ruc', 'pasaporte', 'ce'
    @SerialName("num_doc") val numDoc: String = "",
    @SerialName("cliente_nombre") val clienteNombre: String = "",
    @SerialName("celular") val celular: String = "",
    @SerialName("nacionalidad") val nacionalidad: String = "Perú",
    @SerialName("fecha_nacimiento") val fechaNacimiento: String = "",
    @SerialName("doc_imagenes") val docImagenes: List<String> = emptyList(), // Array URLs (Frente y Reverso)
    @SerialName("cantidad") val cantidad: Int = 1,
    @SerialName("noches") val noches: Int = 1,
    @SerialName("fecha_ingreso") val fechaIngreso: String = "",
    @SerialName("fecha_salida") val fechaSalida: String? = null,
    @SerialName("tipo_comprobante") val tipoComprobante: String = "nota_venta", // 'boleta', 'factura', 'nota_venta'
    @SerialName("serie") val serie: String = "NV01",
    @SerialName("numero") val numero: Int = 1,
    @SerialName("tarifa") val tarifa: Double = 0.0,
    @SerialName("monto_alquiler") val montoAlquiler: Double = 0.0,
    @SerialName("monto_consumos") val montoConsumos: Double = 0.0,
    @SerialName("monto_adelanto") val montoAdelanto: Double = 0.0,
    @SerialName("monto_vuelto") val montoVuelto: Double = 0.0,
    @SerialName("monto_descuento") val montoDescuento: Double = 0.0,
    @SerialName("monto_total") val montoTotal: Double = 0.0,
    @SerialName("metodo_pago") val metodoPago: String = "efectivo", // 'efectivo', 'yape', 'plin', 'tarjeta'
    @SerialName("estado_pago") val estadoPago: String = "pendiente", // 'pendiente', 'pagado', 'anulado'
    @SerialName("estado_sunat") val estadoSunat: String = "local", // 'local', 'aceptado', 'rechazado'
    @SerialName("consumos_json") val consumosJson: String = "[]",
    @SerialName("acompaniantes_json") val acompaniantesJson: String = "[]",
    @SerialName("observaciones") val observaciones: String = ""
)
