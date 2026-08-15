package com.hotelwii.feature.imprimir.modelos

import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.Generador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🧾 Datos necesarios para la impresión de una Boleta de Venta Electrónica.
 */
data class DatosBoleta(
    val razonSocialHotel: String = "HOTEL WII & SUITES S.A.C.",
    val rucHotel: String = "20609876543",
    val direccionHotel: String = "Av. Angela Perotti s/n, Huacachina, Ica",
    val telefonoHotel: String = "+51 956 123 456",
    val serieNumero: String = "B001-0000104",
    val fechaEmision: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
    val clienteNombre: String = "JUAN CARLOS PEREZ GOMEZ",
    val clienteDni: String = "72345678",
    val habitacionNumero: String = "201",
    val tipoHabitacion: String = "Matrimonial Superior",
    val fechaIngreso: String = "",
    val fechaSalida: String = "",
    val diasNoches: Int = 1,
    val items: List<ItemComprobante> = listOf(),
    val subtotal: Double = 101.69,
    val igv: Double = 18.31,
    val total: Double = 120.00,
    val metodoPago: String = "Yape",
    val montoRecibido: Double = 120.00,
    val vuelto: Double = 0.0,
    val codigoHash: String = "4F5A9B1C8E7D6A3F",
    val qrSunatTexto: String = "20609876543|03|B001|0000104|18.31|120.00|15/08/2026|1|72345678|"
)

data class ItemComprobante(
    val cantidad: Int = 1,
    val descripcion: String,
    val precioUnitario: Double,
    val total: Double
)

/**
 * 🧾 BoletaVenta.kt — Generador de Boleta de Venta Electrónica SUNAT para ticketera térmica.
 */
object BoletaVenta {

    fun generar(
        datos: DatosBoleta,
        anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM
    ): ByteArray {
        val driver = Generador(anchoPapel)
        driver.inicializar()

        // 🏨 Cabecera Hotel
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

        // 🏷️ Tipo y Serie
        driver.alinearCentro()
            .negrita(true)
            .linea("BOLETA DE VENTA ELECTRONICA")
            .fuenteDobleAncho()
            .linea(datos.serieNumero)
            .fuenteNormal()
            .negrita(false)
            .separador()

        // 👤 Datos del Cliente y Recepción
        driver.alinearIzquierda()
            .dosColumnas("FECHA EMISION:", datos.fechaEmision)
            .dosColumnas("CLIENTE / HUESPED:", datos.clienteNombre)
            .dosColumnas("DNI / DOC:", datos.clienteDni)
            .dosColumnas("HABITACION:", "${datos.habitacionNumero} (${datos.tipoHabitacion})")
        
        if (datos.fechaIngreso.isNotBlank() && datos.fechaSalida.isNotBlank()) {
            driver.dosColumnas("ESTANCIA:", "${datos.fechaIngreso} al ${datos.fechaSalida}")
        }
        driver.separador()

        // 📦 Detalle de Ítems
        driver.alinearIzquierda()
            .tresColumnas("CANT", "DESCRIPCION", "IMPORTE")
            .separadorPunteado()

        if (datos.items.isEmpty()) {
            val desc = "Alojamiento Hab. ${datos.habitacionNumero} (${datos.diasNoches} Noche)"
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

        // 💰 Totales
        driver.alinearIzquierda()
            .dosColumnas("OP. GRAVADA:", "S/ ${String.format(Locale.US, "%.2f", datos.subtotal)}")
            .dosColumnas("I.G.V. (18%):", "S/ ${String.format(Locale.US, "%.2f", datos.igv)}")
            .negrita(true)
            .fuenteDobleAlto()
            .dosColumnas("TOTAL A PAGAR:", "S/ ${String.format(Locale.US, "%.2f", datos.total)}")
            .fuenteNormal()
            .negrita(false)
            .separador()

        // 💳 Información de Pago
        driver.alinearIzquierda()
            .dosColumnas("FORMA DE PAGO:", datos.metodoPago.uppercase())
        if (datos.montoRecibido > datos.total) {
            driver.dosColumnas("IMPORTE RECIBIDO:", "S/ ${String.format(Locale.US, "%.2f", datos.montoRecibido)}")
                .dosColumnas("VUELTO:", "S/ ${String.format(Locale.US, "%.2f", datos.vuelto)}")
        }
        driver.separador()

        // 📱 Código QR SUNAT
        if (datos.qrSunatTexto.isNotBlank()) {
            driver.alinearCentro()
                .codigoQr(datos.qrSunatTexto, tamano = 5)
                .salto(1)
        }

        // 🔒 Hash & Leyenda
        driver.alinearCentro()
            .linea("CODIGO HASH: ${datos.codigoHash}")
            .linea("Representacion impresa de la")
            .linea("BOLETA DE VENTA ELECTRONICA")
            .linea("Autorizado mediante Resolucion SUNAT")
            .linea("¡Gracias por su preferencia!")
            .cortarPapel(lineasAvance = 4)

        return driver.construir()
    }
}
