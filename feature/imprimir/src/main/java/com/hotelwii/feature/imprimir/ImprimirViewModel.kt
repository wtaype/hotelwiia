package com.hotelwii.feature.imprimir

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.imprimir.servicios.Configurar
import com.hotelwii.feature.imprimir.servicios.EscanerRed
import com.hotelwii.feature.imprimir.servicios.ImpresoraDetectada
import com.hotelwii.feature.imprimir.servicios.ImprimirServicio
import com.hotelwii.feature.imprimir.servicios.ModeloConfigImpresora
import com.hotelwii.feature.imprimir.servicios.ResultadoImpresion
import com.hotelwii.feature.imprimir.servicios.TipoConexion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ImprimirViewModel — Gestor de estado reactivo, escaneo de red Wi-Fi y jobs de impresión térmica.
 */
class ImprimirViewModel(private val context: Context) : ViewModel() {
    private val gestorConfig = Configurar(context)

    private val _config = MutableStateFlow(gestorConfig.obtener())
    val config: StateFlow<ModeloConfigImpresora> = _config.asStateFlow()

    private val _isProbando = MutableStateFlow(false)
    val isProbando: StateFlow<Boolean> = _isProbando.asStateFlow()

    private val _isImprimiendo = MutableStateFlow(false)
    val isImprimiendo: StateFlow<Boolean> = _isImprimiendo.asStateFlow()

    private val _isEscaneando = MutableStateFlow(false)
    val isEscaneando: StateFlow<Boolean> = _isEscaneando.asStateFlow()

    private val _impresorasDetectadas = MutableStateFlow<List<ImpresoraDetectada>>(emptyList())
    val impresorasDetectadas: StateFlow<List<ImpresoraDetectada>> = _impresorasDetectadas.asStateFlow()

    private val _ultimoMensaje = MutableStateFlow<String?>(null)
    val ultimoMensaje: StateFlow<String?> = _ultimoMensaje.asStateFlow()

    private val _esError = MutableStateFlow(false)
    val esError: StateFlow<Boolean> = _esError.asStateFlow()

    fun guardarConfiguracion(nuevaConfig: ModeloConfigImpresora) {
        gestorConfig.guardar(nuevaConfig)
        _config.value = nuevaConfig
        _ultimoMensaje.value = "Configuración guardada correctamente."
        _esError.value = false
    }

    fun escanearRedLocal() {
        if (_isEscaneando.value) return
        _isEscaneando.value = true
        _ultimoMensaje.value = "Escaneando dispositivos en tu red Wi-Fi / Ethernet..."
        _esError.value = false

        viewModelScope.launch {
            try {
                val encontradas = EscanerRed.escanearRedLocal(context)
                _impresorasDetectadas.value = encontradas
                _isEscaneando.value = false

                if (encontradas.isEmpty()) {
                    val ipLocal = EscanerRed.obtenerIpLocal(context) ?: "desconocida"
                    val subred = EscanerRed.obtenerPrefijoSubred(ipLocal)
                    _ultimoMensaje.value = "No se detectaron impresoras abiertas en el rango ${subred}1-254 (Puerto 9100). Revisa la guía de conexión."
                    _esError.value = true
                } else {
                    _ultimoMensaje.value = "Se encontraron ${encontradas.size} impresora(s) en la red."
                    _esError.value = false
                }
            } catch (e: Exception) {
                _isEscaneando.value = false
                _ultimoMensaje.value = "Error al escanear la red: ${e.localizedMessage}"
                _esError.value = true
            }
        }
    }

    fun seleccionarImpresoraDetectada(impresora: ImpresoraDetectada) {
        val nuevaConfig = _config.value.copy(
            tipoConexion = TipoConexion.RED_TCP,
            ip = impresora.ip,
            puerto = impresora.puerto,
            estaConfigurada = true
        )
        guardarConfiguracion(nuevaConfig)
        _ultimoMensaje.value = "Impresora vinculada: ${impresora.ip}:${impresora.puerto}"
        _esError.value = false
    }

    fun comprobarConexion() {
        if (_isProbando.value) return
        _isProbando.value = true
        _ultimoMensaje.value = "Comprobando conexión con ${_config.value.ip}:${_config.value.puerto}..."
        _esError.value = false

        viewModelScope.launch {
            val resultado = ImprimirServicio.probarConexion(_config.value)
            _isProbando.value = false
            when (resultado) {
                is ResultadoImpresion.Exito -> {
                    _ultimoMensaje.value = "Conexión exitosa con la impresora 3nStar."
                    _esError.value = false
                }
                is ResultadoImpresion.Error -> {
                    _ultimoMensaje.value = resultado.mensaje
                    _esError.value = true
                }
            }
        }
    }

    fun imprimirBytes(bytes: ByteArray, descripcion: String) {
        if (_isImprimiendo.value) return
        _isImprimiendo.value = true
        _ultimoMensaje.value = "Enviando $descripcion a la impresora..."
        _esError.value = false

        viewModelScope.launch {
            val resultado = ImprimirServicio.enviar(bytes, _config.value)
            _isImprimiendo.value = false
            when (resultado) {
                is ResultadoImpresion.Exito -> {
                    _ultimoMensaje.value = "$descripcion impresa con éxito."
                    _esError.value = false
                }
                is ResultadoImpresion.Error -> {
                    _ultimoMensaje.value = "Error al imprimir: ${resultado.mensaje}"
                    _esError.value = true
                }
            }
        }
    }
}
