package com.hotelwii.feature.imprimir.modelos

import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.Generador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🧾 PrecuentaTicket.kt — Modelo de Pre-cuenta de Habitación (Comprobante informativo de consumo).
 */
object PrecuentaTicket {

    fun generar(
        nombreHotel: String = "HOTEL WII & SUITES",
        habitacionNumero: String = "201",
        huespedNombre: String = "JUAN CARLOS PEREZ",
        items: List<ItemComprobante> = listOf(),
        montoHospedaje: Double = 120.00,
        montoConsumos: Double = 35.00,
        montoAdelanto: Double = 50.00,
        totalNeto: Double = 105.00,
        anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM
    ): ByteArray {
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val driver = Generador(anchoPapel)
        driver.inicializar()

        // 🏨 Encabezado
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAlto()
            .linea(nombreHotel)
            .fuenteNormal()
            .negrita(false)
            .separadorDoble()

        // 🏷️ Título
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAncho()
            .linea("ESTADO DE PRE-CUENTA")
            .fuenteNormal()
            .negrita(false)
            .linea("NO VALIDO COMO COMPROBANTE DE PAGO")
            .separador()

        // ℹ️ Datos
        driver.alinearIzquierda()
            .dosColumnas("FECHA / HORA:", fecha)
            .dosColumnas("HABITACION:", habitacionNumero)
            .dosColumnas("HUESPED:", huespedNombre)
            .separador()

        // 📦 Detalle
        driver.alinearIzquierda()
            .tresColumnas("CANT", "CONCEPTO", "IMPORTE")
            .separadorPunteado()
            .tresColumnas("1", "Alojamiento / Hospedaje", String.format(Locale.US, "%.2f", montoHospedaje))

        if (items.isNotEmpty()) {
            items.forEach { item ->
                driver.tresColumnas(item.cantidad.toString(), item.descripcion, String.format(Locale.US, "%.2f", item.total))
            }
        } else if (montoConsumos > 0) {
            driver.tresColumnas("1", "Consumos / Servicios extra", String.format(Locale.US, "%.2f", montoConsumos))
        }
        driver.separador()

        // 💰 Liquidación
        val subtotalSuma = montoHospedaje + montoConsumos
        driver.alinearIzquierda()
            .dosColumnas("TOTAL CONSUMOS:", "S/ ${String.format(Locale.US, "%.2f", subtotalSuma)}")
            .dosColumnas("ADELANTOS PAGADOS:", "S/ ${String.format(Locale.US, "%.2f", montoAdelanto)}")
            .negrita(true)
            .fuenteDobleAlto()
            .dosColumnas("TOTAL A PAGAR:", "S/ ${String.format(Locale.US, "%.2f", totalNeto)}")
            .fuenteNormal()
            .negrita(false)
            .separadorDoble()

        driver.alinearCentro()
            .linea("Por favor verifique sus consumos antes de pagar.")
            .linea("Recepcion HotelWii")
            .cortarPapel(lineasAvance = 4)

        return driver.construir()
    }
}
