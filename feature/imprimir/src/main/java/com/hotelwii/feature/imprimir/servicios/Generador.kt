package com.hotelwii.feature.imprimir.servicios

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

/**
 * 🖨️ Generador.kt — Generador universal de comandos de bytes ESC/POS.
 * Diseñado para impresoras térmicas de tickets y facturación (3nStar, Epson, POS-80, etc.).
 */
class Generador(
    val anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM,
    private val charset: Charset = Charset.forName("CP437")
) {
    private val buffer = ByteArrayOutputStream()

    init {
        inicializar()
    }

    /**
     * 🔄 Inicializa o resetea la impresora a valores predeterminados (ESC @).
     */
    fun inicializar(): Generador {
        buffer.write(byteArrayOf(0x1B, 0x40))
        return this
    }

    // ==========================================
    // 📐 ALINEACIÓN
    // ==========================================
    fun alinearIzquierda(): Generador {
        buffer.write(byteArrayOf(0x1B, 0x61, 0x00))
        return this
    }

    fun alinearCentro(): Generador {
        buffer.write(byteArrayOf(0x1B, 0x61, 0x01))
        return this
    }

    fun alinearDerecha(): Generador {
        buffer.write(byteArrayOf(0x1B, 0x61, 0x02))
        return this
    }

    // ==========================================
    // 🔠 ESTILOS DE FUENTE Y FORMATO
    // ==========================================
    fun negrita(activar: Boolean = true): Generador {
        buffer.write(byteArrayOf(0x1B, 0x45, if (activar) 0x01 else 0x00))
        return this
    }

    fun subrayado(activar: Boolean = true): Generador {
        buffer.write(byteArrayOf(0x1B, 0x2D, if (activar) 0x01 else 0x00))
        return this
    }

    fun invertido(activar: Boolean = true): Generador {
        buffer.write(byteArrayOf(0x1D, 0x42, if (activar) 0x01 else 0x00))
        return this
    }

    fun fuenteNormal(): Generador {
        buffer.write(byteArrayOf(0x1D, 0x21, 0x00))
        return this
    }

    fun fuenteDobleAlto(): Generador {
        buffer.write(byteArrayOf(0x1D, 0x21, 0x01))
        return this
    }

    fun fuenteDobleAncho(): Generador {
        buffer.write(byteArrayOf(0x1D, 0x21, 0x10))
        return this
    }

    fun fuenteGrande(): Generador {
        buffer.write(byteArrayOf(0x1D, 0x21, 0x11))
        return this
    }

    // ==========================================
    // 📝 TEXTO Y LÍNEAS
    // ==========================================
    fun texto(texto: String): Generador {
        buffer.write(texto.toByteArray(charset))
        return this
    }

    fun linea(texto: String = ""): Generador {
        buffer.write("$texto\n".toByteArray(charset))
        return this
    }

    fun separador(caracter: Char = '-'): Generador {
        val repeticiones = anchoPapel.columnas
        val lineaStr = caracter.toString().repeat(repeticiones)
        return linea(lineaStr)
    }

    fun separadorDoble(): Generador {
        return separador('=')
    }

    fun separadorPunteado(): Generador {
        return separador('-')
    }

    fun salto(lineas: Int = 1): Generador {
        for (i in 0 until lineas) {
            buffer.write(0x0A)
        }
        return this
    }

    // ==========================================
    // 📊 FORMATO MULTICOLUMNA
    // ==========================================
    fun dosColumnas(izquierda: String, derecha: String): Generador {
        val total = anchoPapel.columnas
        val maxIzq = total - derecha.length - 1
        val izqRecortada = if (izquierda.length > maxIzq && maxIzq > 0) {
            izquierda.substring(0, maxIzq)
        } else {
            izquierda
        }
        val espacios = (total - izqRecortada.length - derecha.length).coerceAtLeast(1)
        val fila = izqRecortada + " ".repeat(espacios) + derecha
        return linea(fila)
    }

    fun tresColumnas(col1: String, col2: String, col3: String, ancho1: Int = 4, ancho3: Int = 9): Generador {
        val total = anchoPapel.columnas
        val ancho2 = (total - ancho1 - ancho3 - 2).coerceAtLeast(6)
        
        val c1 = col1.padEnd(ancho1).take(ancho1)
        val c2 = col2.padEnd(ancho2).take(ancho2)
        val c3 = col3.padStart(ancho3).take(ancho3)

        return linea("$c1 $c2 $c3")
    }

    // ==========================================
    // 📱 CÓDIGO QR NATIVO ESC/POS
    // ==========================================
    fun codigoQr(contenido: String, tamano: Int = 6): Generador {
        val bytesContenido = contenido.toByteArray(charset)
        val len = bytesContenido.size + 3
        val pL = (len and 0xFF).toByte()
        val pH = ((len shr 8) and 0xFF).toByte()

        // 1. Modelo 2
        buffer.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00))

        // 2. Tamaño del módulo (1 a 16)
        val sizeByte = tamano.coerceIn(1, 16).toByte()
        buffer.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, sizeByte))

        // 3. Nivel de corrección de errores (Nivel M: 0x31)
        buffer.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x31))

        // 4. Guardar datos en el búfer QR
        buffer.write(byteArrayOf(0x1D, 0x28, 0x6B, pL, pH, 0x31, 0x50, 0x30))
        buffer.write(bytesContenido)

        // 5. Imprimir QR
        buffer.write(byteArrayOf(0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30))

        return this
    }

    // ==========================================
    // ✂️ CORTE Y CONTROL DE HARDWARE
    // ==========================================
    fun cortarPapel(lineasAvance: Int = 4): Generador {
        salto(lineasAvance)
        buffer.write(byteArrayOf(0x1D, 0x56, 0x42, 0x00))
        return this
    }

    fun abrirCajon(): Generador {
        buffer.write(byteArrayOf(0x1B, 0x70, 0x00, 0x19, 0xFA.toByte()))
        return this
    }

    fun construir(): ByteArray {
        return buffer.toByteArray()
    }
}
