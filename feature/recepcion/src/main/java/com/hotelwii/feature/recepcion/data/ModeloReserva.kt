package com.hotelwii.feature.recepcion.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 📅 ModeloReserva.kt — DTO para la tabla public.reservas en Supabase (Calendario Mensual & Canales OTAs).
 */
@Serializable
data class ModeloReserva(
    @SerialName("id") val id: String? = null,
    @SerialName("userId") val smileId: String = "",
    @SerialName("empresa_id") val empresaId: String = "",
    @SerialName("habitacion_id") val habitacionId: String = "",
    @SerialName("canal") val canal: String = "directo", // 'directo', 'booking', 'expedia', 'airbnb'
    @SerialName("fecha_inicio") val fechaInicio: String = "",
    @SerialName("fecha_fin") val fechaFin: String = "",
    @SerialName("cliente_nombre") val clienteNombre: String = "",
    @SerialName("cliente_telefono") val clienteTelefono: String = "",
    @SerialName("monto_adelanto") val montoAdelanto: Double = 0.0,
    @SerialName("estado") val estado: String = "confirmada", // 'confirmada', 'cancelada', 'completada'
    @SerialName("observaciones") val observaciones: String = ""
)
