package com.hotelwii.feature.imprimir.servicios

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.util.UUID

/**
 * 📊 Resultado de la operación de impresión o test de conexión.
 */
sealed class ResultadoImpresion {
    object Exito : ResultadoImpresion()
    data class Error(val mensaje: String, val excepcion: Throwable? = null) : ResultadoImpresion()
}

/**
 * 📡 ImprimirServicio.kt — Emisor asíncrono universal de alta velocidad vía TCP Socket (Port 9100) y Bluetooth.
 */
object ImprimirServicio {
    private const val TAG = "HotelWii_Imprimir"
    private const val TIMEOUT_MS = 4000
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    /**
     * 🚀 Envía un flujo de bytes ESC/POS a la impresora configurada.
     */
    suspend fun enviar(
        bytes: ByteArray,
        config: ModeloConfigImpresora
    ): ResultadoImpresion = withContext(Dispatchers.IO) {
        try {
            when (config.tipoConexion) {
                TipoConexion.RED_TCP -> enviarTcp(bytes, config.ip, config.puerto)
                TipoConexion.BLUETOOTH -> enviarBluetooth(bytes, config.macBluetooth)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al imprimir en 3nStar: ${e.message}", e)
            ResultadoImpresion.Error("Error al comunicar con la impresora: ${e.localizedMessage ?: e.message}", e)
        }
    }

    /**
     * 🔍 Prueba rápida de conectividad con la impresora (Ping Socket TCP).
     */
    suspend fun probarConexion(config: ModeloConfigImpresora): ResultadoImpresion = withContext(Dispatchers.IO) {
        try {
            when (config.tipoConexion) {
                TipoConexion.RED_TCP -> {
                    val socket = Socket()
                    socket.connect(InetSocketAddress(config.ip, config.puerto), TIMEOUT_MS)
                    socket.close()
                    ResultadoImpresion.Exito
                }
                TipoConexion.BLUETOOTH -> {
                    val adapter = BluetoothAdapter.getDefaultAdapter()
                        ?: return@withContext ResultadoImpresion.Error("El dispositivo no soporta Bluetooth")
                    if (!adapter.isEnabled) {
                        return@withContext ResultadoImpresion.Error("Bluetooth apagado")
                    }
                    val device = adapter.getRemoteDevice(config.macBluetooth)
                    val socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                    socket.connect()
                    socket.close()
                    ResultadoImpresion.Exito
                }
            }
        } catch (e: Exception) {
            ResultadoImpresion.Error("No se pudo conectar con la impresora en ${config.ip}:${config.puerto} (${e.message})", e)
        }
    }

    // ==========================================
    // 🌐 TRANSPORTE RED TCP / ETHERNET (3nStar)
    // ==========================================
    private fun enviarTcp(bytes: ByteArray, ip: String, puerto: Int): ResultadoImpresion {
        var socket: Socket? = null
        var outputStream: OutputStream? = null
        return try {
            socket = Socket()
            socket.connect(InetSocketAddress(ip, puerto), TIMEOUT_MS)
            outputStream = socket.getOutputStream()
            outputStream.write(bytes)
            outputStream.flush()
            ResultadoImpresion.Exito
        } catch (e: Exception) {
            ResultadoImpresion.Error("Fallo de red TCP al conectar con $ip:$puerto -> ${e.message}", e)
        } finally {
            try { outputStream?.close() } catch (_: Exception) {}
            try { socket?.close() } catch (_: Exception) {}
        }
    }

    // ==========================================
    // 🔵 TRANSPORTE BLUETOOTH SPP
    // ==========================================
    private fun enviarBluetooth(bytes: ByteArray, mac: String): ResultadoImpresion {
        if (mac.isBlank()) {
            return ResultadoImpresion.Error("Dirección MAC de Bluetooth no configurada")
        }
        val adapter = BluetoothAdapter.getDefaultAdapter()
            ?: return ResultadoImpresion.Error("Bluetooth no disponible en este dispositivo")
        
        var socket: BluetoothSocket? = null
        var outputStream: OutputStream? = null
        return try {
            val device = adapter.getRemoteDevice(mac)
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            adapter.cancelDiscovery()
            socket.connect()
            outputStream = socket.outputStream
            outputStream.write(bytes)
            outputStream.flush()
            ResultadoImpresion.Exito
        } catch (e: Exception) {
            ResultadoImpresion.Error("Error Bluetooth con dispositivo $mac -> ${e.message}", e)
        } finally {
            try { outputStream?.close() } catch (_: Exception) {}
            try { socket?.close() } catch (_: Exception) {}
        }
    }
}
