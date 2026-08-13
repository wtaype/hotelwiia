package com.hotelwii.core.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ─────────────────────────────────────────────────────────────────────────
// 📄 Modelos de Respuesta de Decolecta API (Estructura Oficial 2026)
// ─────────────────────────────────────────────────────────────────────────
@Serializable
data class DniResponse(
    @SerialName("document_number") val numeroDocumento: String? = null,
    @SerialName("first_name") val nombres: String? = null,
    @SerialName("first_last_name") val apellidoPaterno: String? = null,
    @SerialName("second_last_name") val apellidoMaterno: String? = null,
    @SerialName("full_name") val nombreCompletoAlt: String? = null,

    // Compatibilidad de nombres tradicionales
    @SerialName("numero") val numeroLegacy: String? = null,
    @SerialName("nombre") val nombreLegacy: String? = null
) {
    val nombreCompleto: String
        get() = nombreCompletoAlt ?: when {
            !nombres.isNullOrBlank() -> "$nombres ${apellidoPaterno ?: ""} ${apellidoMaterno ?: ""}".trim()
            !nombreLegacy.isNullOrBlank() -> nombreLegacy
            else -> "Nombre no encontrado"
        }

    val documento: String
        get() = numeroDocumento ?: numeroLegacy ?: ""
}

@Serializable
data class RucResponse(
    @SerialName("numero_documento") val numeroDocumento: String? = null,
    @SerialName("razon_social") val razonSocial: String? = null,
    @SerialName("direccion") val direccion: String? = null,
    @SerialName("estado") val estado: String? = null,
    @SerialName("condicion") val condicion: String? = null,
    @SerialName("departamento") val departamento: String? = null,
    @SerialName("provincia") val provincia: String? = null,
    @SerialName("distrito") val distrito: String? = null,
    @SerialName("ubigeo") val ubigeo: String? = null,

    // Compatibilidad legacy
    @SerialName("numero") val numeroLegacy: String? = null,
    @SerialName("razonSocial") val razonSocialLegacy: String? = null
) {
    val nombreEmpresa: String
        get() = razonSocial ?: razonSocialLegacy ?: "Razón Social no encontrada"

    val direccionCompleta: String
        get() = when {
            !direccion.isNullOrBlank() && direccion != "-" -> direccion
            !distrito.isNullOrBlank() -> "$distrito, $provincia, $departamento"
            else -> "Dirección no registrada"
        }
}

@Serializable
data class TipoCambioResponse(
    @SerialName("date") val fecha: String? = null,
    @SerialName("buy_price") val precioCompraStr: String? = null,
    @SerialName("sell_price") val precioVentaStr: String? = null,
    @SerialName("compra") val compraNum: Double? = null,
    @SerialName("venta") val ventaNum: Double? = null,
    @SerialName("base_currency") val baseCurrency: String? = "USD",
    @SerialName("quote_currency") val quoteCurrency: String? = "PEN"
) {
    val compra: Double
        get() = precioCompraStr?.toDoubleOrNull() ?: compraNum ?: 0.0

    val venta: Double
        get() = precioVentaStr?.toDoubleOrNull() ?: ventaNum ?: 0.0
}

sealed class ApiResultado<out T> {
    data class Exito<T>(val data: T) : ApiResultado<T>()
    data class Error(val mensaje: String) : ApiResultado<Nothing>()
    object Cargando : ApiResultado<Nothing>()
}

// ─────────────────────────────────────────────────────────────────────────
// 🌐 Servicio HTTP Decolecta (Ktor Client)
// ─────────────────────────────────────────────────────────────────────────
object DecolectaService {
    private const val BASE_URL = "https://api.decolecta.com"

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val client = HttpClient(Android)

    /**
     * Consulta de DNI (RENIEC)
     */
    suspend fun consultarDni(dni: String, token: String = ""): ApiResultado<DniResponse> {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/reniec/dni") {
                parameter("numero", dni)
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/json")
            }

            val bodyText = response.bodyAsText()
            if (response.status.isSuccess()) {
                val result = jsonConfig.decodeFromString<DniResponse>(bodyText)
                ApiResultado.Exito(result)
            } else {
                ApiResultado.Error("RENIEC: HTTP ${response.status.value} - $bodyText")
            }
        } catch (e: Exception) {
            ApiResultado.Error("Error de red: ${e.localizedMessage ?: "Consulte su conexión"}")
        }
    }

    /**
     * Consulta de RUC (SUNAT)
     */
    suspend fun consultarRuc(ruc: String, token: String = ""): ApiResultado<RucResponse> {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/sunat/ruc") {
                parameter("numero", ruc)
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/json")
            }

            val bodyText = response.bodyAsText()
            if (response.status.isSuccess()) {
                val result = jsonConfig.decodeFromString<RucResponse>(bodyText)
                ApiResultado.Exito(result)
            } else {
                ApiResultado.Error("SUNAT: HTTP ${response.status.value} - $bodyText")
            }
        } catch (e: Exception) {
            ApiResultado.Error("Error de red: ${e.localizedMessage ?: "Consulte su conexión"}")
        }
    }

    /**
     * Consulta de Tipo de Cambio Dólar / Soles (SUNAT)
     */
    suspend fun consultarTipoCambio(fecha: String = "", token: String): ApiResultado<TipoCambioResponse> {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/v1/tipo-cambio/sunat") {
                if (fecha.isNotBlank()) {
                    parameter("date", fecha)
                }
                header(HttpHeaders.Authorization, "Bearer $token")
                header(HttpHeaders.Accept, "application/json")
            }

            val bodyText = response.bodyAsText()
            if (response.status.isSuccess()) {
                val result = jsonConfig.decodeFromString<TipoCambioResponse>(bodyText)
                ApiResultado.Exito(result)
            } else {
                ApiResultado.Error("Tipo de Cambio: HTTP ${response.status.value} - $bodyText")
            }
        } catch (e: Exception) {
            ApiResultado.Error("Error de red: ${e.localizedMessage ?: "Consulte su conexión"}")
        }
    }
}
