package com.hotelwii.feature.imprimir.modelos

import com.hotelwii.feature.imprimir.servicios.AnchoPapel
import com.hotelwii.feature.imprimir.servicios.Generador
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 🧪 Prueba.kt — Modelo de Ticket de Diagnóstico, Calibración y Test de Impresión.
 * Permite verificar conectividad, alineación, estilos de texto, código QR y corte de papel en impresoras 3nStar.
 */
object Prueba {

    fun generar(
        nombreHotel: String = "HOTEL WII & SUITES",
        direccion: String = "Huacachina, Ica - Perú",
        ruc: String = "20601234567",
        anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM,
        ipOpcional: String = "192.168.1.100"
    ): ByteArray {
        val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        val driver = Generador(anchoPapel)

        driver.inicializar()

        // 🏨 Encabezado
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAlto()
            .linea(nombreHotel)
            .fuenteNormal()
            .negrita(false)
            .linea("RUC: $ruc")
            .linea(direccion)
            .linea("TEL: +51 956 000 000")
            .separadorDoble()

        // 🧪 Título de la prueba
        driver.alinearCentro()
            .negrita(true)
            .fuenteDobleAncho()
            .linea("TICKET DE PRUEBA")
            .fuenteNormal()
            .negrita(false)
            .linea("DIAGNOSTICO DE IMPRESORA")
            .separador()

        // ℹ️ Datos del sistema
        driver.alinearIzquierda()
            .dosColumnas("FECHA / HORA:", fechaActual)
            .dosColumnas("SISTEMA:", "HotelWii v3.0 POS")
            .dosColumnas("HARDWARE:", "3nStar Thermal POS")
            .dosColumnas("ANCHO DE PAPEL:", "${anchoPapel.milimetros}mm (${anchoPapel.columnas} cols)")
            .dosColumnas("IP / ENLACE:", ipOpcional)
            .separador()

        // 🔠 Prueba de estilos de texto
        driver.alinearIzquierda()
            .negrita(true).linea("1. PRUEBA DE ESTILOS DE FUENTE:").negrita(false)
            .linea("Texto normal estandar")
            .negrita(true).linea("Texto en Negrita (Bold)").negrita(false)
            .subrayado(true).linea("Texto Subrayado (Underline)").subrayado(false)
            .fuenteDobleAlto().linea("Doble Altura").fuenteNormal()
            .fuenteDobleAncho().linea("Doble Ancho").fuenteNormal()
            .invertido(true).linea(" TEXTO MODO INVERTIDO ").invertido(false)
            .separador()

        // 📊 Prueba de Columnas y Justificación
        driver.alinearIzquierda()
            .negrita(true).linea("2. PRUEBA DE JUSTIFICACION & COLUMNAS:").negrita(false)
            .dosColumnas("Izquierda", "Derecha")
            .dosColumnas("Habitacion Matrimonial 201", "S/ 140.00")
            .dosColumnas("Servicio Minibar / Bebidas", "S/ 25.00")
            .tresColumnas("CANT", "DESCRIPCION", "TOTAL")
            .tresColumnas("1", "Noche Hab. Suite", "180.00")
            .tresColumnas("2", "Agua Mineral 500ml", "10.00")
            .separador()

        // 📱 Código QR de Prueba
        driver.alinearCentro()
            .negrita(true).linea("3. PRUEBA DE CODIGO QR ESC/POS:").negrita(false)
            .codigoQr("https://hotelwii.com/test-print", tamano = 6)
            .salto(1)
            .linea("Escanee para verificar resolucion QR")
            .separadorDoble()

        // ✨ Pie de página y corte
        driver.alinearCentro()
            .negrita(true)
            .linea("¡COMUNICACION EXITOSA CON 3nStar!")
            .negrita(false)
            .linea("HotelWii - Software Hotelero Local-First")
            .linea("www.hotelwii.com")
            .cortarPapel(lineasAvance = 4)

        return driver.construir()
    }
}
