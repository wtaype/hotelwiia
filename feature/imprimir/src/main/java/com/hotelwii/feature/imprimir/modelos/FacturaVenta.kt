package com.hotelwii.feature.imprimir.modelos

import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.Generador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🏢 Datos requeridos para la impresión de una Factura Electrónica.
 */
data class DatosFactura(
    val razonSocialHotel: String = "HOTEL WII & SUITES S.A.C.",
    val rucHotel: String = "20609876543",
    val direccionHotel: String = "Av. Angela Perotti s/n, Huacachina, Ica",
    val telefonoHotel: String = "+51 956 123 456",
    val serieNumero: String = "F001-0000048",
    val fechaEmision: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
    val clienteRuc: String = "20543219876",
    val clienteRazonSocial: String = "CONSTRUCTORA & SERVICIOS DEL SUR S.A.C.",
    val clienteDireccionFiscal: String = "Av. Los Maestros 450, Ica",
    val habitacionNumero: String = "302",
    val tipoHabitacion: String = "Suite Ejecutiva",
    val diasNoches: Int = 2,
    val items: List<ItemComprobante> = listOf(),
    val subtotal: Double = 338.98,
    val igv: Double = 61.02,
    val total: Double = 400.00,
    val metodoPago: String = "Transferencia / Tarjeta",
    val codigoHash: String = "9B8C7D6E5F4A3B2C",
    val qrSunatTexto: String = "20609876543|01|F001|0000048|61.02|400.00|15/08/2026|6|20543219876|"
)

/**
 * 🏢 FacturaVenta.kt — Generador de Factura Electrónica SUNAT para ticketera térmica 3nStar.
 */
object FacturaVenta {

    fun generar(
        datos: DatosFactura,
        anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM
    ): ByteArray {
        val driver = Generador(anchoPapel)
        driver.inicializar()

        // 🏨 Cabecera Hotel Emisor
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAlto()
            .linea(datos.razonSocialHotel)
            .fuenteNormal()
            .negrita(false)
            .linea("RUC: ${datos.rucHotel}")
            .linea(datos.direccionHotel)
            .linea("TEL: ${datos.telefonoHotel}")
            .separadorDoble()

        // 🏷️ Título Factura y Serie
        driver.alinearCentro()
            .negrita(true)
            .linea("FACTURA ELECTRONICA")
            .fuenteDobleAncho()
            .linea(datos.serieNumero)
            .fuenteNormal()
            .negrita(false)
            .separador()

        // 🏢 Datos de la Empresa Cliente
        driver.alinearIzquierda()
            .dosColumnas("FECHA EMISION:", datos.fechaEmision)
            .dosColumnas("R.U.C. CLIENTE:", datos.clienteRuc)
            .linea("RAZON SOCIAL:")
            .negrita(true).linea("  ${datos.clienteRazonSocial}").negrita(false)
        
        if (datos.clienteDireccionFiscal.isNotBlank()) {
            driver.linea("DIR. FISCAL:")
                .linea("  ${datos.clienteDireccionFiscal}")
        }
        driver.dosColumnas("HABITACION:", "${datos.habitacionNumero} (${datos.tipoHabitacion})")
        driver.separador()

        // 📦 Detalle de Servicios
        driver.alinearIzquierda()
            .tresColumnas("CANT", "DESCRIPCION", "IMPORTE")
            .separadorPunteado()

        if (datos.items.isEmpty()) {
            val desc = "Servicio de Hospedaje Hab. ${datos.habitacionNumero} (${datos.diasNoches} Noches)"
            driver.tresColumnas(datos.diasNoches.toString(), desc, String.format(Locale.US, "%.2f", datos.total))
        } else {
            datos.items.forEach { item ->
                driver.tresColumnas(
                    item.cantidad.toString(),
                    item.descripcion,
                    String.format(Locale.US, "%.2f", item.total)
                )
            }
        }
        driver.separador()

        // 💰 Totales e Impuestos
        driver.alinearIzquierda()
            .dosColumnas("OP. GRAVADA:", "S/ ${String.format(Locale.US, "%.2f", datos.subtotal)}")
            .dosColumnas("I.G.V. (18%):", "S/ ${String.format(Locale.US, "%.2f", datos.igv)}")
            .negrita(true)
            .fuenteDobleAlto()
            .dosColumnas("TOTAL FACTURA:", "S/ ${String.format(Locale.US, "%.2f", datos.total)}")
            .fuenteNormal()
            .negrita(false)
            .separador()

        // 💳 Medio de Pago
        driver.alinearIzquierda()
            .dosColumnas("FORMA DE PAGO:", datos.metodoPago.uppercase())
            .separador()

        // 📱 Código QR SUNAT
        if (datos.qrSunatTexto.isNotBlank()) {
            driver.alinearCentro()
                .codigoQr(datos.qrSunatTexto, tamano = 5)
                .salto(1)
        }

        // 🔒 Hash & Leyenda Fiscal
        driver.alinearCentro()
            .linea("CODIGO HASH: ${datos.codigoHash}")
            .linea("Representacion impresa de la")
            .linea("FACTURA ELECTRONICA")
            .linea("Consulte su comprobante en www.hotelwii.com")
            .cortarPapel(lineasAvance = 4)

        return driver.construir()
    }
}
