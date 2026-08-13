package com.hotelwii.feature.empresas.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 🏢 ModeloEmpresa.kt — Modelo de dominio 1:1 de la entidad public.empresas en Supabase.
 * Contiene datos generales del hotel e indicación de selección Local-First y ajustes fiscales.
 */
@Serializable
data class ModeloEmpresa(
    val id: String? = null,
    @SerialName("userId") val smileId: String? = null,
    @SerialName("empresa") val nombreComercial: String = "",
    @SerialName("razon_social") val razonSocial: String = "",
    @SerialName("empresa_ruc") val ruc: String = "",
    val direccion: String? = null,
    val departamento: String? = null,
    val provincia: String? = null,
    val distrito: String? = null,
    val ubigeo: String? = null,
    val telefono: String? = null,
    val celular: String? = null,
    val email: String? = null,
    val logo: String? = null,
    @SerialName("sitio_web") val sitioWeb: String? = null,

    // 🧾 Ajustes de Facturación y Comprobantes SUNAT
    @SerialName("nota_venta") val notaVenta: Boolean = true,
    val boleta: Boolean = true,
    val factura: Boolean = true,
    @SerialName("serie_boleta") val serieBoleta: String = "B001",
    @SerialName("serie_factura") val serieFactura: String = "F001",
    @SerialName("serie_nota") val serieNota: String = "NV01",
    @SerialName("impuesto_porcentaje") val impuestoPorcentaje: Double = 18.00,
    val moneda: String = "PEN",
    @SerialName("pin_sol") val pinSol: String? = null,

    // ⚙️ Estado y Selección Local-First
    val principal: Boolean = false,
    val activo: Boolean = true,
    val estado: String? = "activo",
    val creado: String? = null,
    val actualizado: String? = null
) {
    val esEmpresaActiva: Boolean
        get() = activo && (estado == null || estado.equals("activo", ignoreCase = true))

    val ubigeoFormateado: String
        get() {
            val ubs = listOfNotNull(distrito, provincia, departamento).filter { it.isNotBlank() }
            return if (ubs.isNotEmpty()) ubs.joinToString(", ") else ubigeo ?: "Sin Ubigeo"
        }
}
