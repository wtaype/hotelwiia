package com.hotelwii.feature.recepcion.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 🏨 ModeloHabitacion.kt — DTO para la tabla public.habitaciones en Supabase.
 */
@Serializable
data class ModeloHabitacion(
    @SerialName("id") val id: String? = null,
    @SerialName("userId") val smileId: String = "",
    @SerialName("empresa_id") val empresaId: String = "",
    @SerialName("numero") val numero: String = "",
    @SerialName("piso") val piso: String = "Piso 1",
    @SerialName("tipo") val tipo: String = "Matrimonial",
    @SerialName("precio") val precio: Double = 80.0,
    @SerialName("capacidad") val capacidad: Int = 2,
    @SerialName("estado") val estado: String = "disponible", // 'disponible', 'ocupada', 'limpieza', 'mantenimiento'
    @SerialName("con_desayuno") val conDesayuno: Boolean = false,
    @SerialName("con_bano") val conBano: Boolean = true,
    @SerialName("con_tv") val conTv: Boolean = true,
    @SerialName("amenidades") val amenidades: String = "Wi-Fi, Agua Caliente, TV",
    @SerialName("observaciones") val observaciones: String = ""
)
