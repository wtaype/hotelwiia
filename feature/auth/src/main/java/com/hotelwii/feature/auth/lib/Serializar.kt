package com.hotelwii.feature.auth.lib

/**
 * 🧼 Serializar.kt — Sanitizador y formateador de entradas exclusivo de :feature:auth para HotelWii.
 */
object Serializar {
    /**
     * Limpia comillas escapadas, espacios redundantes y caracteres de control.
     */
    fun textoLimpio(input: String): String {
        return input.replace("\"", "").replace("'", "").trim()
    }

    /**
     * Limpia el correo electrónico:
     * - Convierte todo a minúsculas.
     * - Elimina comillas y espacios en blanco.
     */
    fun email(input: String): String {
        return textoLimpio(input).lowercase().replace(" ", "")
    }

    /**
     * Limpia el nombre de usuario:
     * - Convierte a minúsculas.
     * - Elimina espacios y tildes/caracteres especiales.
     * - Permite ÚNICAMENTE caracteres a-z, 0-9 y guión bajo (_).
     */
    fun usuario(input: String): String {
        return textoLimpio(input).lowercase()
            .replace(" ", "")
            .replace(Regex("[^a-z0-9_]"), "")
    }

    /**
     * Limpia nombres y apellidos personales (elimina espacios múltiples).
     */
    fun nombre(input: String): String {
        return textoLimpio(input).replace(Regex("\\s+"), " ")
    }

    /**
     * Separa de forma inteligente un nombre completo ("Wilder Taype") en Pair("Wilder", "Taype").
     */
    fun separarNombreCompleto(fullName: String, givenName: String = "", familyName: String = ""): Pair<String, String> {
        val cleanGiven = nombre(givenName)
        val cleanFamily = nombre(familyName)

        if (cleanGiven.isNotBlank()) {
            return Pair(cleanGiven, cleanFamily)
        }

        val cleanFull = nombre(fullName)
        if (cleanFull.isBlank()) return Pair("Usuario", "")

        val partes = cleanFull.split(" ")
        return if (partes.size > 1) {
            val primNombre = partes.first()
            val restoApellidos = partes.drop(1).joinToString(" ")
            Pair(primNombre, restoApellidos)
        } else {
            Pair(cleanFull, "")
        }
    }
}
