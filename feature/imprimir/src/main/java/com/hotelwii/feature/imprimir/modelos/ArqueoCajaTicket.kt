package com.hotelwii.feature.imprimir.modelos

import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.Generador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 💵 ArqueoCajaTicket.kt — Reporte de Arqueo y Cierre de Caja del turno.
 */
object ArqueoCajaTicket {

    fun generar(
        nombreHotel: String = "HOTEL WII & SUITES",
        recepcionista: String = "CARLOS MENDOZA",
        turno: String = "TURNO MAÑANA (07:00 - 15:00)",
        totalEfectivo: Double = 850.00,
        totalYapePlin: Double = 420.00,
        totalTarjeta: Double = 630.00,
        totalTransferencias: Double = 150.00,
        totalGastosCajaChica: Double = 45.00,
        saldoInicial: Double = 100.00,
        anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM
    ): ByteArray {
        val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val totalIngresos = totalEfectivo + totalYapePlin + totalTarjeta + totalTransferencias
        val totalEfectivoEnCaja = saldoInicial + totalEfectivo - totalGastosCajaChica

        val driver = Generador(anchoPapel)
        driver.inicializar()

        // 🏨 Cabecera
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
            .linea("CIERRE DE CAJA")
            .fuenteNormal()
            .negrita(false)
            .linea("REPORTE DE ARQUEO DE TURNO")
            .separador()

        // 👤 Datos del Turno
        driver.alinearIzquierda()
            .dosColumnas("FECHA / HORA:", fecha)
            .dosColumnas("RECEPCIONISTA:", recepcionista)
            .dosColumnas("TURNO:", turno)
            .dosColumnas("FONDO INICIAL:", "S/ ${String.format(Locale.US, "%.2f", saldoInicial)}")
            .separador()

        // 💰 Desglose por Método de Pago
        driver.alinearIzquierda()
            .negrita(true).linea("DESGLOSE DE RECAUDACION:").negrita(false)
            .dosColumnas("EFECTIVO RECAUDADO:", "S/ ${String.format(Locale.US, "%.2f", totalEfectivo)}")
            .dosColumnas("YAPE / PLIN (QR):", "S/ ${String.format(Locale.US, "%.2f", totalYapePlin)}")
            .dosColumnas("TARJETAS (POS):", "S/ ${String.format(Locale.US, "%.2f", totalTarjeta)}")
            .dosColumnas("TRANSFERENCIAS:", "S/ ${String.format(Locale.US, "%.2f", totalTransferencias)}")
            .separadorPunteado()
            .negrita(true)
            .dosColumnas("TOTAL RECAUDADO:", "S/ ${String.format(Locale.US, "%.2f", totalIngresos)}")
            .negrita(false)
            .separador()

        // 📉 Egresos / Caja Chica
        if (totalGastosCajaChica > 0) {
            driver.alinearIzquierda()
                .dosColumnas("GASTOS CAJA CHICA (-):", "S/ ${String.format(Locale.US, "%.2f", totalGastosCajaChica)}")
                .separador()
        }

        // 💵 Resumen de Efectivo en Gaveta
        driver.alinearIzquierda()
            .negrita(true)
            .fuenteDobleAlto()
            .dosColumnas("EFECTIVO EN GAVETA:", "S/ ${String.format(Locale.US, "%.2f", totalEfectivoEnCaja)}")
            .fuenteNormal()
            .negrita(false)
            .separadorDoble()

        // ✍️ Firma
        driver.alinearCentro()
            .salto(2)
            .linea("_____________________________")
            .linea("FIRMA DE CONFORMIDAD")
            .linea(recepcionista)
            .cortarPapel(lineasAvance = 4)

        return driver.construir()
    }
}
