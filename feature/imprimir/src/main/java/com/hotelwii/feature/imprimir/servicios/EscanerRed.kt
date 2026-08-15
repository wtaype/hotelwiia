package com.hotelwii.feature.imprimir.servicios

import android.content.Context
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.util.Collections
import java.util.Locale

/**
 * Dispositivo de red detectado durante el escaneo.
 */
data class ImpresoraDetectada(
    val ip: String,
    val puerto: Int = 9100,
    val nombreSugerido: String = "Impresora Térmica ESC/POS (3nStar)",
    val tiempoRespuestaMs: Long = 0
)

/**
 * EscanerRed — Motor inteligente y asíncrono para descubrir impresoras 3nStar en la red local Wi-Fi / Ethernet.
 * Escanea 254 direcciones IP en paralelo (< 2.5 segundos) mediante Sockets TCP en puerto 9100.
 */
object EscanerRed {

    /**
     * Obtiene la dirección IP local asignada al dispositivo Android en la red Wi-Fi o Ethernet.
     */
    fun obtenerIpLocal(context: Context): String? {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val ipInt = wifiManager?.connectionInfo?.ipAddress ?: 0
            if (ipInt != 0) {
                return String.format(
                    Locale.US,
                    "%d.%d.%d.%d",
                    ipInt and 0xff,
                    ipInt shr 8 and 0xff,
                    ipInt shr 16 and 0xff,
                    ipInt shr 24 and 0xff
                )
            }

            // Fallback mediante NetworkInterface
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (!intf.isUp || intf.isLoopback) continue
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        val host = addr.hostAddress ?: ""
                        if (host.startsWith("192.168.") || host.startsWith("10.") || host.startsWith("172.")) {
                            return host
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return null
    }

    /**
     * Extrae el prefijo de la subred (ej: "192.168.1.") a partir de una IP local.
     */
    fun obtenerPrefijoSubred(ipLocal: String): String {
        val partes = ipLocal.split(".")
        return if (partes.size == 4) {
            "${partes[0]}.${partes[1]}.${partes[2]}."
        } else {
            "192.168.1."
        }
    }

    /**
     * Escanea toda la subred local buscando impresoras con el puerto 9100 (ESC/POS) abierto.
     */
    suspend fun escanearRedLocal(
        context: Context,
        puerto: Int = 9100,
        subredManual: String? = null
    ): List<ImpresoraDetectada> = withContext(Dispatchers.IO) {
        val ipLocal = obtenerIpLocal(context) ?: "192.168.1.100"
        val prefijo = subredManual ?: obtenerPrefijoSubred(ipLocal)

        val encontradas = Collections.synchronizedList(mutableListOf<ImpresoraDetectada>())

        // Escanear los 254 hosts en paralelo
        val tareas = (1..254).map { hostNum ->
            async {
                val ipObjetivo = "$prefijo$hostNum"
                val inicio = System.currentTimeMillis()
                try {
                    Socket().use { socket ->
                        socket.connect(InetSocketAddress(ipObjetivo, puerto), 350)
                        val latencia = System.currentTimeMillis() - inicio
                        encontradas.add(
                            ImpresoraDetectada(
                                ip = ipObjetivo,
                                puerto = puerto,
                                nombreSugerido = "Impresora 3nStar / ESC-POS ($ipObjetivo)",
                                tiempoRespuestaMs = latencia
                            )
                        )
                    }
                } catch (_: Exception) {
                    // Host no responde en puerto 9100
                }
            }
        }

        tareas.awaitAll()
        encontradas.toList().sortedBy { it.ip }
    }
}
