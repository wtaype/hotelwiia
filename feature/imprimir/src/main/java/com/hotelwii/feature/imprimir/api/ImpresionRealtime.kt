package com.hotelwii.feature.imprimir.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.feature.imprimir.data.ModeloImpresion
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * ⚡ ImpresionRealtime.kt — Escucha eventos WebSockets en tiempo real en la tabla `cola_impresiones`.
 */
class ImpresionRealtime(private val scope: CoroutineScope) {
    private val client get() = HotelWiiSupabase.instancia
    private val json = Json { ignoreUnknownKeys = true }
    private var jobEscucha: Job? = null

    fun iniciarEscucha(empresaId: String, onNuevoTrabajo: (ModeloImpresion) -> Unit) {
        if (empresaId.isBlank()) return
        jobEscucha?.cancel()
        jobEscucha = scope.launch(Dispatchers.IO) {
            try {
                val canal = client.realtime.channel("canal_impresiones_$empresaId")
                val cambioFlow = canal.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "impresiones"
                }

                cambioFlow.onEach { accion ->
                    try {
                        val record = json.decodeFromJsonElement<ModeloImpresion>(accion.record)
                        if (record.empresaId == empresaId && record.estado == "pendiente") {
                            onNuevoTrabajo(record)
                        }
                    } catch (_: Exception) {}
                }.launchIn(this)

                canal.subscribe()
            } catch (_: Exception) {}
        }
    }

    fun detenerEscucha() {
        jobEscucha?.cancel()
        jobEscucha = null
    }
}
