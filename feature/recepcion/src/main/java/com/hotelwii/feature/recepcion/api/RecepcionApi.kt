package com.hotelwii.feature.recepcion.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloReserva
import com.hotelwii.feature.recepcion.data.ModeloVenta
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 🌐 RecepcionApi.kt — Servicio PostgREST oficial para public.habitaciones, public.ventas y public.reservas.
 */
object RecepcionApi {
    private val client get() = HotelWiiSupabase.instancia

    // 🏨 HABITACIONES
    suspend fun obtenerHabitaciones(empresaId: String, smileId: String = ""): Result<List<ModeloHabitacion>> = withContext(Dispatchers.IO) {
        if (empresaId.isBlank() && smileId.isBlank()) return@withContext Result.success(emptyList())
        try {
            val lista = client.postgrest["habitaciones"]
                .select {
                    filter {
                        if (empresaId.isNotBlank()) {
                            eq("empresa_id", empresaId)
                        } else {
                            eq("userId", smileId)
                        }
                    }
                }
                .decodeList<ModeloHabitacion>()
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun guardarHabitacion(hab: ModeloHabitacion): Result<ModeloHabitacion> = withContext(Dispatchers.IO) {
        try {
            val res = if (hab.id.isNullOrBlank()) {
                client.postgrest["habitaciones"].insert(hab) { select() }.decodeSingle<ModeloHabitacion>()
            } else {
                client.postgrest["habitaciones"].update({
                    set("numero", hab.numero)
                    set("piso", hab.piso)
                    set("tipo", hab.tipo)
                    set("precio", hab.precio)
                    set("capacidad", hab.capacidad)
                    set("estado", hab.estado)
                    set("con_desayuno", hab.conDesayuno)
                    set("con_bano", hab.conBano)
                    set("con_tv", hab.conTv)
                    set("amenidades", hab.amenidades)
                    set("observaciones", hab.observaciones)
                }) {
                    filter { eq("id", hab.id) }
                    select()
                }.decodeSingle<ModeloHabitacion>()
            }
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarHabitacion(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (id.isBlank()) return@withContext Result.failure(IllegalArgumentException("ID vacío"))
        try {
            client.postgrest["habitaciones"].delete {
                filter { eq("id", id) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 💳 VENTAS & CHECK-IN / CHECK-OUT
    suspend fun obtenerVentasActivas(empresaId: String): Result<List<ModeloVenta>> = withContext(Dispatchers.IO) {
        if (empresaId.isBlank()) return@withContext Result.success(emptyList())
        try {
            val lista = client.postgrest["ventas"]
                .select {
                    filter {
                        eq("empresa_id", empresaId)
                        eq("estado_pago", "pendiente")
                    }
                }
                .decodeList<ModeloVenta>()
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarCheckIn(venta: ModeloVenta): Result<ModeloVenta> = withContext(Dispatchers.IO) {
        try {
            val creada = client.postgrest["ventas"].insert(venta) { select() }.decodeSingle<ModeloVenta>()
            client.postgrest["habitaciones"].update({
                set("estado", "ocupada")
            }) {
                filter { eq("id", venta.habitacionId) }
            }
            Result.success(creada)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registrarCheckOut(
        ventaId: String,
        habitacionId: String,
        montoTotal: Double,
        metodoPago: String,
        tipoComprobante: String,
        serie: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            client.postgrest["ventas"].update({
                set("estado_pago", "pagado")
                set("monto_total", montoTotal)
                set("metodo_pago", metodoPago)
                set("tipo_comprobante", tipoComprobante)
                set("serie", serie)
            }) {
                filter { eq("id", ventaId) }
            }
            client.postgrest["habitaciones"].update({
                set("estado", "disponible")
            }) {
                filter { eq("id", habitacionId) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 📅 RESERVAS
    suspend fun obtenerReservas(empresaId: String): Result<List<ModeloReserva>> = withContext(Dispatchers.IO) {
        if (empresaId.isBlank()) return@withContext Result.success(emptyList())
        try {
            val lista = client.postgrest["reservas"]
                .select { filter { eq("empresa_id", empresaId) } }
                .decodeList<ModeloReserva>()
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
