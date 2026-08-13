package com.hotelwii.feature.cuenta.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 🔒 SeguridadModelo.kt — Modelo DTO para la tabla public.seguridad en Supabase (Limpia sin empresa_id).
 */
@Serializable
data class SeguridadModelo(
    @SerialName("userId") val userId: String = "",
    @SerialName("pin_seguridad") val pinSeguridad: String = "",
    @SerialName("r2_access_key") val r2AccessKey: String = "",
    @SerialName("r2_secret_key") val r2SecretKey: String = "",
    @SerialName("r2_endpoint") val r2Endpoint: String = "",
    @SerialName("r2_bucket") val r2Bucket: String = "hotelwii-docs",
    @SerialName("api_decolecta") val apiDecolecta: String = "",
    @SerialName("gemini_key") val geminiKey: String = ""
)
