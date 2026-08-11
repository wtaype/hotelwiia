package com.hotelwii.core.kidev

import android.content.Context
import com.hotelwii.core.kicss.*

/**
 * Nombre.kt — Utilidades de formato de nombres y avatares para HotelWii
 */

/** Obtiene el nombre del usuario guardado en sesión */
fun nombrePerfil(context: Context): String {
    return wiStore(context).get("user_name", "Usuario")
}

/** Formatea String a "Nombre Apellido" */
fun String.NombreApellido(): String {
    val partes = this.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        partes.isEmpty() -> ""
        partes.size == 1 -> partes[0].Capi()
        else -> "${partes[0].Capi()} ${partes[1].Capi()}"
    }
}

/** Obtiene solo el primer nombre */
fun String.getNombre(): String {
    val partes = this.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return if (partes.isNotEmpty()) partes[0].Capi() else ""
}

/** Genera iniciales de avatar ("Juan Pérez" -> "JP") */
fun String.avatar(): String {
    val partes = this.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    return when {
        partes.isEmpty() -> "W"
        partes.size == 1 -> partes[0].take(2).uppercase()
        else -> "${partes[0].take(1)}${partes[1].take(1)}".uppercase()
    }
}
