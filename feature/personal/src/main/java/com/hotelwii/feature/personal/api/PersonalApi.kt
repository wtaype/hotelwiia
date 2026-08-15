package com.hotelwii.feature.personal.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.feature.personal.data.ModeloPersonal
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 👥 PersonalApi.kt — Servicio de datos remoto PostgREST oficial para public.personal en Supabase.
 */
object PersonalApi {
    private val client get() = HotelWiiSupabase.instancia

    suspend fun obtenerPersonalPorEmpresa(empresaId: String): Result<List<ModeloPersonal>> = withContext(Dispatchers.IO) {
        try {
            if (empresaId.isBlank()) return@withContext Result.success(emptyList())
            val lista = client.postgrest["personal"]
                .select {
                    filter { eq("empresa_id", empresaId) }
                }
                .decodeList<ModeloPersonal>()
            Result.success(lista)
        } catch (e: RestException) {
            Result.failure(Exception("Error en Supabase (${e.statusCode}): ${e.error}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun guardarPersonal(personal: ModeloPersonal): Result<ModeloPersonal> = withContext(Dispatchers.IO) {
        try {
            if (personal.id.isNullOrBlank()) {
                // Crear nuevo colaborador
                val creado = client.postgrest["personal"]
                    .insert(personal) { select() }
                    .decodeSingle<ModeloPersonal>()
                Result.success(creado)
            } else {
                // Actualizar existente
                val actualizado = client.postgrest["personal"]
                    .update(personal) {
                        filter { eq("id", personal.id) }
                        select()
                    }
                    .decodeSingle<ModeloPersonal>()
                Result.success(actualizado)
            }
        } catch (e: RestException) {
            Result.failure(Exception("Error RLS en Supabase (${e.statusCode}): ${e.error}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun conmutarEstadoActivo(id: String, activo: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["personal"].update({
                set("activo", activo)
            }) {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarPersonal(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["personal"].delete {
                filter { eq("id", id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
