package com.hotelwii.feature.cuenta.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.core.kidev.Encriptar
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 🌐 CuentaApi.kt — Servicio PostgREST para actualizar public.smiles y public.seguridad en Supabase.
 * Cifra automáticamente todas las llaves API con AES-256 (Encriptar.cifrar) y el PIN con SHA-256 (Encriptar.hashPin).
 */
class CuentaApi {
    private val client get() = HotelWiiSupabase.instancia

    suspend fun actualizarPerfil(
        id: String,
        nombre: String,
        apellidos: String,
        usuario: String,
        email: String,
        bio: String?,
        tema: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["smiles"].update({
                set("nombre", nombre)
                set("apellidos", apellidos)
                set("usuario", usuario)
                set("email", email)
                set("bio", bio)
                set("tema", tema)
            }) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarTema(
        id: String,
        tema: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["smiles"].update({
                set("tema", tema)
            }) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza la configuración de seguridad y llaves API cifradas con public.seguridad en Supabase.
     */
    suspend fun guardarSeguridadDSL(
        userId: String,
        pinSeguridad: String,
        apiDecolecta: String,
        r2AccessKey: String,
        r2SecretKey: String,
        geminiKey: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        if (userId.isBlank()) return@withContext Result.failure(IllegalArgumentException("userId vacío"))
        try {
            // Cifrar valores con Encriptar (AES-256 con prefijo ENC: y PIN SHA-256)
            val hashedPin = if (pinSeguridad.isNotBlank()) Encriptar.hashPin(pinSeguridad) else ""
            val encDecolecta = Encriptar.cifrar(apiDecolecta)
            val encR2Key = Encriptar.cifrar(r2AccessKey)
            val encR2Secret = Encriptar.cifrar(r2SecretKey)
            val encGemini = Encriptar.cifrar(geminiKey)

            // 1. Intentar actualizar registro existente por userId
            val filaSexistente = client.postgrest["seguridad"].select {
                filter { eq("userId", userId) }
            }.decodeList<SeguridadModelo>()

            if (filaSexistente.isNotEmpty()) {
                client.postgrest["seguridad"].update({
                    if (hashedPin.isNotBlank()) set("pin_seguridad", hashedPin)
                    set("api_decolecta", encDecolecta)
                    set("r2_access_key", encR2Key)
                    set("r2_secret_key", encR2Secret)
                    set("gemini_key", encGemini)
                }) {
                    filter { eq("userId", userId) }
                }
            } else {
                // 2. Si no existe, insertar nueva fila cifrada
                client.postgrest["seguridad"].insert(
                    SeguridadModelo(
                        userId = userId,
                        pinSeguridad = hashedPin,
                        apiDecolecta = encDecolecta,
                        r2AccessKey = encR2Key,
                        r2SecretKey = encR2Secret,
                        geminiKey = encGemini
                    )
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene y descifra las credenciales de seguridad guardadas en Supabase para el usuario.
     */
    suspend fun obtenerSeguridad(userId: String): Result<SeguridadModelo?> = withContext(Dispatchers.IO) {
        if (userId.isBlank()) return@withContext Result.success(null)
        try {
            val lista = client.postgrest["seguridad"].select {
                filter { eq("userId", userId) }
            }.decodeList<SeguridadModelo>()

            val modelo = lista.firstOrNull() ?: return@withContext Result.success(null)
            val descifrado = modelo.copy(
                apiDecolecta = Encriptar.descifrar(modelo.apiDecolecta),
                r2AccessKey = Encriptar.descifrar(modelo.r2AccessKey),
                r2SecretKey = Encriptar.descifrar(modelo.r2SecretKey),
                geminiKey = Encriptar.descifrar(modelo.geminiKey)
            )
            Result.success(descifrado)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
