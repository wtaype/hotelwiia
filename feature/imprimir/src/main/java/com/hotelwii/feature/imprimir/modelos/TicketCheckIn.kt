package com.hotelwii.feature.imprimir.modelos

import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.Generador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🛏️ Datos para el Voucher / Ticket de Check-In del huésped.
 */
data class DatosCheckInTicket(
    val nombreHotel: String = "HOTEL WII & SUITES",
    val direccionHotel: String = "Huacachina, Ica - Perú",
    val telefonoRecepcion: String = "+51 956 123 456",
    val wifiRed: String = "HotelWii_Guest",
    val wifiClave: String = "Huacachina2026",
    val horaCheckOut: String = "12:00 PM",
    val habitacionNumero: String = "201",
    val tipoHabitacion: String = "Matrimonial Superior",
    val huespedNombre: String = "JUAN CARLOS PEREZ GOMEZ",
    val huespedDni: String = "72345678",
    val numPersonas: Int = 2,
    val fechaIngreso: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
    val fechaSalidaEstimada: String = "",
    val montoAdelanto: Double = 50.0,
    val montoPendiente: Double = 70.0,
    val notasRecepcion: String = "Desayuno incluido de 7:30 AM a 10:00 AM."
)

/**
 * 🛏️ TicketCheckIn.kt — Voucher de Registro & Bienvenida para el huésped.
 */
object TicketCheckIn {

    fun generar(
        datos: DatosCheckInTicket,
        anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM
    ): ByteArray {
        val driver = Generador(anchoPapel)
        driver.inicializar()

        // 🏨 Cabecera
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAlto()
            .linea(datos.nombreHotel)
            .fuenteNormal()
            .negrita(false)
            .linea(datos.direccionHotel)
            .linea("TEL. RECEPCION: ${datos.telefonoRecepcion}")
            .separadorDoble()

        // 🏷️ Título Voucher
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAncho()
            .linea("VOUCHER DE CHECK-IN")
            .fuenteNormal()
            .negrita(false)
            .linea("REGISTRO DE HOSPEDAJE")
            .separador()

        // 🔑 Habitación destacada
        driver.alinearCentro()
            .negrita(true)
            .fuenteGrande()
            .linea("HABITACION ${datos.habitacionNumero}")
            .fuenteNormal()
            .linea(datos.tipoHabitacion)
            .negrita(false)
            .separador()

        // 👤 Datos del Huésped
        driver.alinearIzquierda()
            .dosColumnas("HUESPED TITULAR:", datos.huespedNombre)
            .dosColumnas("DNI / DOC:", datos.huespedDni)
            .dosColumnas("NRO PERSONAS:", "${datos.numPersonas} Persona(s)")
            .dosColumnas("FECHA INGRESO:", datos.fechaIngreso)
        if (datos.fechaSalidaEstimada.isNotBlank()) {
            driver.dosColumnas("CHECK-OUT ESTIMADO:", datos.fechaSalidaEstimada)
        }
        driver.separador()

        // 💰 Estado de Cuenta
        driver.alinearIzquierda()
            .dosColumnas("ADELANTO / PAGO:", "S/ ${String.format(Locale.US, "%.2f", datos.montoAdelanto)}")
            .dosColumnas("SALDO PENDIENTE:", "S/ ${String.format(Locale.US, "%.2f", datos.montoPendiente)}")
            .separador()

        // 📶 Datos de Wi-Fi e Información Útil
        driver.alinearCentro()
            .negrita(true)
            .linea("--- DATOS DE CONEXION ---")
            .negrita(false)
            .dosColumnas("RED WI-FI:", datos.wifiRed)
            .dosColumnas("CLAVE WI-FI:", datos.wifiClave)
            .dosColumnas("HORA MAX. SALIDA:", datos.horaCheckOut)
        
        if (datos.notasRecepcion.isNotBlank()) {
            driver.salto(1)
                .alinearIzquierda()
                .linea("NOTA:")
                .linea(datos.notasRecepcion)
        }
        driver.separador()

        // 📱 QR con enlace a servicios del hotel / Carta digital
        driver.alinearCentro()
            .codigoQr("https://hotelwii.com/guest/${datos.habitacionNumero}", tamano = 5)
            .salto(1)
            .linea("Escanee para ver servicios y carta")
            .linea("¡Le deseamos una feliz estancia!")
            .cortarPapel(lineasAvance = 4)

        return driver.construir()
    }
}
