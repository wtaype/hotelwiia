package com.hotelwii.feature.auth.lib

import android.util.Patterns

/**
 * ✅ Validar.kt — Asistente de Validación de Reglas de Negocio en Tiempo Real para :feature:auth.
 */
object Validar {
    /**
     * Valida el formato del correo electrónico.
     */
    fun esEmailValido(email: String): Boolean {
        val clean = Serializar.email(email)
        return clean.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(clean).matches()
    }

    /**
     * Valida el nombre de usuario (mínimo 3 caracteres, solo a-z, 0-9 y _).
     */
    fun esUsuarioValido(usuario: String): Boolean {
        val clean = Serializar.usuario(usuario)
        return clean.length >= 3 && clean.matches(Regex("^[a-z0-9_]+$"))
    }

    /**
     * Valida nombres y apellidos (mínimo 2 caracteres).
     */
    fun esNombreValido(nombre: String): Boolean {
        return Serializar.nombre(nombre).length >= 2
    }

    /**
     * Valida contraseña (mínimo 6 caracteres).
     */
    fun esPasswordValida(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida que las contraseñas coincidan.
     */
    fun coincidenPasswords(p1: String, p2: String): Boolean {
        return p1.isNotBlank() && p1 == p2
    }
}
