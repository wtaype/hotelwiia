package com.hotelwii.feature.imprimir

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.auth.data.CacheSmile
import com.hotelwii.feature.empresas.data.CacheEmpresa
import com.hotelwii.feature.imprimir.api.ImpresionRealtime
import com.hotelwii.feature.imprimir.api.ImprimirApi
import com.hotelwii.feature.imprimir.data.CacheImprimir
import com.hotelwii.feature.imprimir.data.ModeloImpresion
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
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * ⚡ ImprimirViewModel.kt — Gestor de estado reactivo, despacho híbrido (Local/Nube) y auto-validación de conexión.
 */
class ImprimirViewModel(private val context: Context) : ViewModel() {
    private val gestorConfig = Configurar(context)
    private val cacheImprimir = CacheImprimir.getInstance(context)
    private val cacheSmile = CacheSmile.getInstance(context)
    private val cacheEmpresa = CacheEmpresa.getInstance(context)
    private val realtimeListener = ImpresionRealtime(viewModelScope)

    private val _config = MutableStateFlow(gestorConfig.obtener())
    val config: StateFlow<ModeloConfigImpresora> = _config.asStateFlow()

    private val _isConectadoLocal = MutableStateFlow(false)
    val isConectadoLocal: StateFlow<Boolean> = _isConectadoLocal.asStateFlow()

    private val _isConectadoNube = MutableStateFlow(false)
    val isConectadoNube: StateFlow<Boolean> = _isConectadoNube.asStateFlow()

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

    val historial: StateFlow<List<ModeloImpresion>> = cacheImprimir.historialImpresionesFlow
    val esReceptorActivo: StateFlow<Boolean> = cacheImprimir.esReceptorNubeActivoFlow

    init {
        // Auto-validación reactiva en segundo plano
        verificarEstadosConexion()
        sincronizarEscuchaRealtime()
    }

    fun verificarEstadosConexion() {
        viewModelScope.launch {
            // 1. Verificar Estado Local
            val resLocal = ImprimirServicio.probarConexion(_config.value)
            _isConectadoLocal.value = (resLocal is ResultadoImpresion.Exito)

            // 2. Verificar Estado Nube
            val smile = cacheSmile.sesionActivaFlow.value
            val smileId = smile?.id ?: ""
            val empresa = cacheEmpresa.obtenerEmpresaActiva(smileId)
            val empresaId = empresa?.id ?: ""
            _isConectadoNube.value = empresaId.isNotBlank() && cacheImprimir.esReceptorActivo()
        }
    }

    private fun sincronizarEscuchaRealtime() {
        val smile = cacheSmile.sesionActivaFlow.value
        val smileId = smile?.id ?: ""
        val empresa = cacheEmpresa.obtenerEmpresaActiva(smileId)
        val empresaId = empresa?.id ?: ""

        if (empresaId.isNotBlank() && cacheImprimir.esReceptorActivo()) {
            _isConectadoNube.value = true
            realtimeListener.iniciarEscucha(empresaId) { trabajo ->
                procesarTrabajoRemoto(trabajo)
            }
        } else {
            _isConectadoNube.value = false
        }
    }

    fun setReceptorActivo(activo: Boolean) {
        cacheImprimir.setReceptorActivo(activo)
        if (activo) {
            sincronizarEscuchaRealtime()
        } else {
            _isConectadoNube.value = false
            realtimeListener.detenerEscucha()
        }
    }

    private fun procesarTrabajoRemoto(trabajo: ModeloImpresion) {
        if (cacheImprimir.yaFueProcesado(trabajo.id)) return
        cacheImprimir.registrarProcesado(trabajo.id)

        viewModelScope.launch {
            val resultado = ImprimirServicio.probarConexion(_config.value)
            if (resultado is ResultadoImpresion.Exito) {
                _isConectadoLocal.value = true
                ImprimirApi.actualizarEstado(
                    id = trabajo.id,
                    estado = "impreso",
                    impresoPor = "Receptor Local (${_config.value.ip})"
                )
                cacheImprimir.agregarHistorial(trabajo.copy(estado = "impreso"))
            } else {
                _isConectadoLocal.value = false
                ImprimirApi.actualizarEstado(
                    id = trabajo.id,
                    estado = "error",
                    errorMensaje = "Receptor no pudo conectar con 3nStar en ${_config.value.ip}"
                )
            }
        }
    }

    fun guardarConfiguracion(nuevaConfig: ModeloConfigImpresora) {
        gestorConfig.guardar(nuevaConfig)
        _config.value = nuevaConfig
        _ultimoMensaje.value = "Configuración guardada. Comprobando conexión..."
        _esError.value = false
        verificarEstadosConexion()
    }

    fun escanearRedLocal() {
        if (_isEscaneando.value) return
        _isEscaneando.value = true
        _ultimoMensaje.value = "Escaneando dispositivos en tu red Wi-Fi..."
        _esError.value = false

        viewModelScope.launch {
            try {
                val encontradas = EscanerRed.escanearRedLocal(context)
                _impresorasDetectadas.value = encontradas
                _isEscaneando.value = false

                if (encontradas.isEmpty()) {
                    val ipLocal = EscanerRed.obtenerIpLocal(context) ?: "desconocida"
                    val subred = EscanerRed.obtenerPrefijoSubred(ipLocal)
                    _ultimoMensaje.value = "No se detectaron impresoras en el rango ${subred}1-254. Verifica que estés conectado al Wi-Fi del hotel."
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
        verificarEstadosConexion()
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
                    _isConectadoLocal.value = true
                    _ultimoMensaje.value = "Conexión exitosa con la impresora 3nStar."
                    _esError.value = false
                }
                is ResultadoImpresion.Error -> {
                    _isConectadoLocal.value = false
                    _ultimoMensaje.value = resultado.mensaje
                    _esError.value = true
                }
            }
        }
    }

    fun imprimirBytes(
        bytes: ByteArray,
        descripcion: String,
        tipo: String = "prueba",
        data: JsonObject = buildJsonObject {}
    ) {
        if (_isImprimiendo.value) return
        _isImprimiendo.value = true
        _ultimoMensaje.value = "Procesando $descripcion..."
        _esError.value = false

        viewModelScope.launch {
            val smile = cacheSmile.sesionActivaFlow.value ?: cacheSmile.getSmileGuardado()
            val smileId = smile?.id ?: ""
            val empresa = cacheEmpresa.obtenerEmpresaActiva(smileId) ?: cacheEmpresa.empresaActivaFlow.value
            val empresaId = empresa?.id ?: "1533250d-2b87-4698-91a1-877619fa810e" // ID default hotel
            val usuarioNombre = smile?.nombre?.ifBlank { smile.usuario } ?: "Recepcionista"

            // Si está conectado en Wi-Fi local, intenta enviar por socket directo (15 ms)
            if (_isConectadoLocal.value) {
                val resultadoLocal = ImprimirServicio.enviar(bytes, _config.value)
                if (resultadoLocal is ResultadoImpresion.Exito) {
                    _isImprimiendo.value = false
                    _ultimoMensaje.value = "$descripcion impresa con éxito (Local Wi-Fi)."
                    _esError.value = false

                    val modelo = ModeloImpresion(
                        userId = smileId.ifBlank { "caa5c2f7-6584-48b7-8112-47c018c491c8" },
                        empresaId = empresaId,
                        tipo = tipo,
                        titulo = descripcion,
                        data = data,
                        estado = "impreso",
                        ipDestino = _config.value.ip,
                        impresoPor = usuarioNombre
                    )
                    cacheImprimir.agregarHistorial(modelo)

                    if (empresaId.isNotBlank()) {
                        ImprimirApi.enviarCola(modelo)
                    }
                    return@launch
                } else {
                    _isConectadoLocal.value = false
                }
            }

            // Modo Nube Directo (En 4G o fuera del Wi-Fi): Despacho en ~150 ms
            _ultimoMensaje.value = "Enviando $descripcion a la Nube Supabase (Recepción)..."
            val modeloRemoto = ModeloImpresion(
                userId = smileId.ifBlank { "caa5c2f7-6584-48b7-8112-47c018c491c8" },
                empresaId = empresaId,
                tipo = tipo,
                titulo = descripcion,
                data = data,
                estado = "pendiente",
                ipDestino = _config.value.ip,
                impresoPor = usuarioNombre
            )

            val resNube = ImprimirApi.enviarCola(modeloRemoto)
            _isImprimiendo.value = false
            if (resNube.isSuccess) {
                _ultimoMensaje.value = "$descripcion enviada a la Nube Supabase. Se imprimirá en la 3nStar de recepción."
                _esError.value = false
                cacheImprimir.agregarHistorial(modeloRemoto)
            } else {
                _ultimoMensaje.value = "No se pudo enviar a la nube: ${resNube.exceptionOrNull()?.localizedMessage}"
                _esError.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realtimeListener.detenerEscucha()
    }
}
