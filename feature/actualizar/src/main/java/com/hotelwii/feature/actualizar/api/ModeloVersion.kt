package com.hotelwii.feature.actualizar.api

/**
 * 📦 ModeloVersion — Datos de la versión remota en Cloudflare R2
 */
data class ModeloVersion(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val releaseNotes: String = "",
    val sha256: String? = null,
    val isMandatory: Boolean = false
)
