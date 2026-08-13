package com.hotelwii.feature.empresas.api

import com.hotelwii.core.data.supabase.HotelWiiSupabase
import com.hotelwii.feature.empresas.data.ModeloEmpresa
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 🏢 EmpresasApi.kt — Servicio de datos remoto PostgREST oficial para public.empresas en Supabase.
 * Utiliza el DSL nativo `update { set(...) }` de Supabase Kotlin SDK para garantizar operaciones CRUD limpias.
 */
object EmpresasApi {
    private val client get() = HotelWiiSupabase.instancia

    suspend fun obtenerEmpresasPorSmile(smileId: String): Result<List<ModeloEmpresa>> = withContext(Dispatchers.IO) {
        try {
            if (smileId.isBlank()) return@withContext Result.success(emptyList())
            val lista = client.postgrest["empresas"]
                .select { filter { eq("userId", smileId) } }
                .decodeList<ModeloEmpresa>()
            Result.success(lista)
        } catch (e: RestException) {
            Result.failure(Exception("Error de consulta en Supabase (${e.statusCode}): ${e.error}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearEmpresa(empresa: ModeloEmpresa): Result<ModeloEmpresa> = withContext(Dispatchers.IO) {
        try {
            val creada = client.postgrest["empresas"]
                .insert(empresa) { select() }
                .decodeSingle<ModeloEmpresa>()
            Result.success(creada)
        } catch (e: RestException) {
            Result.failure(Exception("Error RLS en Supabase (${e.statusCode}): La tabla public.empresas rechazó el registro."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEmpresa(empresa: ModeloEmpresa): Result<ModeloEmpresa> = withContext(Dispatchers.IO) {
        try {
            val idActualizar = empresa.id ?: return@withContext Result.failure(IllegalArgumentException("ID de empresa nulo"))

            val desdedb = client.postgrest["empresas"].update({
                set("empresa", empresa.nombreComercial)
                set("razon_social", empresa.razonSocial)
                set("empresa_ruc", empresa.ruc)
                set("direccion", empresa.direccion ?: "")
                set("departamento", empresa.departamento ?: "")
                set("provincia", empresa.provincia ?: "")
                set("distrito", empresa.distrito ?: "")
                set("ubigeo", empresa.ubigeo ?: "")
                set("telefono", empresa.telefono ?: "")
                set("celular", empresa.celular ?: "")
                set("email", empresa.email ?: "")
                set("logo", empresa.logo ?: "")
                set("activo", empresa.activo)
                set("estado", empresa.estado ?: if (empresa.activo) "activo" else "inactivo")
                set("principal", empresa.principal)
                set("nota_venta", empresa.notaVenta)
                set("boleta", empresa.boleta)
                set("factura", empresa.factura)
                set("serie_boleta", empresa.serieBoleta)
                set("serie_factura", empresa.serieFactura)
                set("serie_nota", empresa.serieNota)
                set("impuesto_porcentaje", empresa.impuestoPorcentaje)
                set("moneda", empresa.moneda)
                set("pin_sol", empresa.pinSol ?: "")
                set("sitio_web", empresa.sitioWeb ?: "")
            }) {
                filter { eq("id", idActualizar) }
                select()
            }.decodeSingle<ModeloEmpresa>()

            Result.success(desdedb)
        } catch (e: RestException) {
            Result.failure(Exception("Error al actualizar en Supabase (${e.statusCode}): ${e.error}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarAjustesFacturacionAvanzados(
        empresaId: String,
        notaVenta: Boolean,
        boleta: Boolean,
        factura: Boolean,
        serieBoleta: String,
        serieFactura: String,
        serieNota: String,
        impuesto: Double,
        moneda: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (empresaId.isBlank()) return@withContext Result.failure(IllegalArgumentException("ID vacío"))

            client.postgrest["empresas"].update({
                set("nota_venta", notaVenta)
                set("boleta", boleta)
                set("factura", factura)
                set("serie_boleta", serieBoleta)
                set("serie_factura", serieFactura)
                set("serie_nota", serieNota)
                set("impuesto_porcentaje", impuesto)
                set("moneda", moneda)
            }) {
                filter { eq("id", empresaId) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarCampoBoolean(
        empresaId: String,
        campo: String,
        valor: Boolean
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (empresaId.isBlank()) return@withContext Result.failure(IllegalArgumentException("ID nulo"))

            val columnaDB = when (campo) {
                "nota_venta" -> "nota_venta"
                "boleta" -> "boleta"
                "factura" -> "factura"
                "activo" -> "activo"
                else -> campo
            }

            client.postgrest["empresas"].update({
                set(columnaDB, valor)
                if (campo == "activo") {
                    set("estado", if (valor) "activo" else "inactivo")
                }
            }) {
                filter { eq("id", empresaId) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun marcarEmpresaPrincipal(smileId: String, empresaId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (smileId.isBlank() || empresaId.isBlank()) return@withContext Result.failure(IllegalArgumentException("ID nulo"))

            // 1. Desmarcar todas las empresas del usuario
            client.postgrest["empresas"].update({
                set("principal", false)
            }) {
                filter { eq("userId", smileId) }
            }

            // 2. Marcar exclusivamente la seleccionada
            client.postgrest["empresas"].update({
                set("principal", true)
            }) {
                filter { eq("id", empresaId) }
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarEmpresa(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (id.isBlank()) return@withContext Result.failure(IllegalArgumentException("ID de empresa vacío"))
            client.postgrest["empresas"].delete {
                filter { eq("id", id) }
            }
            Result.success(true)
        } catch (e: RestException) {
            Result.failure(Exception("Error al eliminar empresa en Supabase (${e.statusCode}): ${e.error}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
