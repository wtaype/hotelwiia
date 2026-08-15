package com.hotelwii.feature.imprimir.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.feature.imprimir.data.ModeloImpresion
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🌐 ImprimirApi.kt — Operaciones PostgREST con Supabase para la tabla `cola_impresiones`.
 */
object ImprimirApi {
    private const val TABLA = "impresiones"

    suspend fun enviarCola(impresion: ModeloImpresion): Result<ModeloImpresion> = withContext(Dispatchers.IO) {
        try {
            HotelWiiSupabase.client.from(TABLA).insert(impresion)
            Result.success(impresion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEstado(
        id: String,
        estado: String,
        impresoPor: String = "",
        errorMensaje: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val nowStr = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(Date())
            val patch = buildJsonObject {
                put("estado", estado)
                if (impresoPor.isNotBlank()) put("impreso_por", impresoPor)
                if (estado == "impreso") put("impreso_fecha", nowStr)
                if (errorMensaje.isNotBlank()) put("error_mensaje", errorMensaje)
                put("actualizado", nowStr)
            }
            HotelWiiSupabase.client.from(TABLA).update(patch) {
                filter {
                    eq("id", id)
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun obtenerUltimas(empresaId: String, limite: Long = 20): List<ModeloImpresion> = withContext(Dispatchers.IO) {
        if (empresaId.isBlank()) return@withContext emptyList()
        try {
            HotelWiiSupabase.client.from(TABLA)
                .select {
                    filter {
                        eq("empresa_id", empresaId)
                    }
                    order("creado", Order.DESCENDING)
                    limit(limite)
                }
                .decodeList<ModeloImpresion>()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
