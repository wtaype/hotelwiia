package com.hotelwii.feature.imprimir.servicios

import android.content.Context
import com.hotelwii.core.kidev.WiStore
import org.json.JSONObject

/**
 * 📏 Ancho de papel para impresoras térmicas (como 3nStar).
 */
enum class AnchoPapel(val columnas: Int, val milimetros: Int) {
    PAPEL_80MM(columnas = 48, milimetros = 80),
    PAPEL_58MM(columnas = 32, milimetros = 58)
}

/**
 * 🔌 Tipo de conexión física o de red con la impresora 3nStar.
 */
enum class TipoConexion {
    RED_TCP,      // Socket Ethernet / Wi-Fi (ej. 192.168.1.100:9100)
    BLUETOOTH     // Conexión inalámbrica SPP
}

/**
 * ⚙️ Modelo de datos para la configuración de la impresora.
 */
data class ModeloConfigImpresora(
    val tipoConexion: TipoConexion = TipoConexion.RED_TCP,
    val ip: String = "192.168.1.100",
    val puerto: Int = 9100,
    val nombreBluetooth: String = "3nStar POS Printer",
    val macBluetooth: String = "",
    val anchoPapel: AnchoPapel = AnchoPapel.PAPEL_80MM,
    val cortarPapel: Boolean = true,
    val abrirCajon: Boolean = false,
    val numCopias: Int = 1,
    val estaConfigurada: Boolean = false
)

/**
 * 💾 Configurar.kt — Gestor de persistencia de configuración de impresora en HotelWii.
 */
class Configurar(context: Context) {
    private val store = WiStore(context)
    private val KEY_CONFIG = "hotelwii_impresora_config"

    fun guardar(config: ModeloConfigImpresora): Boolean {
        val json = JSONObject().apply {
            put("tipoConexion", config.tipoConexion.name)
            put("ip", config.ip)
            put("puerto", config.puerto)
            put("nombreBluetooth", config.nombreBluetooth)
            put("macBluetooth", config.macBluetooth)
            put("anchoPapel", config.anchoPapel.name)
            put("cortarPapel", config.cortarPapel)
            put("abrirCajon", config.abrirCajon)
            put("numCopias", config.numCopias)
            put("estaConfigurada", true)
        }
        return store.save(KEY_CONFIG, json.toString())
    }

    fun obtener(): ModeloConfigImpresora {
        val jsonStr = store.get(KEY_CONFIG, "")
        if (jsonStr.isEmpty()) {
            return ModeloConfigImpresora(estaConfigurada = false)
        }
        return try {
            val json = JSONObject(jsonStr)
            ModeloConfigImpresora(
                tipoConexion = TipoConexion.valueOf(json.optString("tipoConexion", TipoConexion.RED_TCP.name)),
                ip = json.optString("ip", "192.168.1.100"),
                puerto = json.optInt("puerto", 9100),
                nombreBluetooth = json.optString("nombreBluetooth", "3nStar POS Printer"),
                macBluetooth = json.optString("macBluetooth", ""),
                anchoPapel = AnchoPapel.valueOf(json.optString("anchoPapel", AnchoPapel.PAPEL_80MM.name)),
                cortarPapel = json.optBoolean("cortarPapel", true),
                abrirCajon = json.optBoolean("abrirCajon", false),
                numCopias = json.optInt("numCopias", 1),
                estaConfigurada = json.optBoolean("estaConfigurada", true)
            )
        } catch (e: Exception) {
            ModeloConfigImpresora(estaConfigurada = false)
        }
    }
}
