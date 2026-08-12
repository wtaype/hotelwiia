package com.hotelwii.feature.cuenta.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 🌐 CuentaApi.kt — Servicio PostgREST para actualizar public.smiles en Supabase.
 */
class CuentaApi {
    private val client get() = HotelWiiSupabase.client

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
}
