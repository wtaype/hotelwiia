package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

fun wiDia(): String {
    val now = LocalDate.now()
    val dias = listOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
    val meses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    return "${dias[now.dayOfWeek.value % 7]}, ${now.dayOfMonth} ${meses[now.monthValue - 1]}"
}

fun fechaHoy(): String =
    LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")).let(::Capit)

fun formatearFechaHora(value: Any?): String {
    val dateTime = value.toLocalDateTimeOrNull() ?: return ""
    return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
}

fun formatearFechaParaInput(value: Any?): String {
    val dateTime = value.toLocalDateTimeOrNull() ?: return ""
    return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
}

fun wiTiempo(value: Any?): String {
    val dateTime = value.toLocalDateTimeOrNull() ?: return ""
    val diff = Duration.between(dateTime, LocalDateTime.now())
    val seconds = diff.seconds.coerceAtLeast(0)
    return when {
        seconds < 45 -> "Ahora"
        seconds < 90 -> "Hace 1 min"
        seconds < 3600 -> "Hace ${seconds / 60} min"
        seconds < 7200 -> "Hace 1 h"
        seconds < 86_400 -> "Hace ${seconds / 3600} h"
        seconds < 172_800 -> "Ayer"
        seconds < 2_592_000 -> "Hace ${seconds / 86_400} días"
        else -> formatearFechaHora(dateTime).substringBefore(" ")
    }
}

private fun Any?.toLocalDateTimeOrNull(): LocalDateTime? = when (this) {
    null -> null
    is LocalDateTime -> this
    is LocalDate -> atStartOfDay()
    is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    is Long -> Date(this).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    is String -> runCatching { LocalDateTime.parse(this) }.getOrNull()
        ?: runCatching { LocalDate.parse(this).atStartOfDay() }.getOrNull()
    else -> null
}

