package com.hotelwii.feature.empresas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.auth.data.CacheSmile
import com.hotelwii.feature.empresas.api.EmpresasApi
import com.hotelwii.feature.empresas.api.SunatApi
import com.hotelwii.feature.empresas.data.CacheEmpresa
import com.hotelwii.feature.empresas.data.ModeloEmpresa
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EmpresaUiState(
    val empresas: List<ModeloEmpresa> = emptyList(),
    val hotelActivo: ModeloEmpresa? = null,
    val hotelAjustesSeleccionado: ModeloEmpresa? = null,
    val empresaEdicion: ModeloEmpresa? = null,
    val isLoading: Boolean = false,
    val isBuscandoRuc: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null
)

class EmpresaViewModel(application: Application) : AndroidViewModel(application) {
    private val cacheSmile = CacheSmile.getInstance(application)
    private val cacheEmpresa = CacheEmpresa.getInstance(application)

    private val _uiState = MutableStateFlow(EmpresaUiState())
    val uiState: StateFlow<EmpresaUiState> = _uiState.asStateFlow()

    private val smileId: String
        get() = cacheSmile.sesionActivaFlow.value?.id ?: ""

    init {
        cargarEmpresasLocalFirst()
    }

    fun cargarEmpresasLocalFirst() {
        val sId = smileId
        val enCache = cacheEmpresa.obtenerListaEmpresas(sId)
        val activaEnCache = cacheEmpresa.obtenerEmpresaActiva(sId) ?: enCache.firstOrNull { it.principal } ?: enCache.firstOrNull()

        _uiState.value = _uiState.value.copy(
            empresas = enCache,
            hotelActivo = activaEnCache,
            hotelAjustesSeleccionado = _uiState.value.hotelAjustesSeleccionado ?: activaEnCache
        )

        // Sincronización en segundo plano con Supabase
        if (sId.isNotBlank()) {
            viewModelScope.launch {
                val res = EmpresasApi.obtenerEmpresasPorSmile(sId)
                res.fold(
                    onSuccess = { listaRemota ->
                        cacheEmpresa.guardarListaEmpresas(sId, listaRemota)
                        val activaRes = cacheEmpresa.obtenerEmpresaActiva(sId)
                            ?: listaRemota.firstOrNull { it.principal }
                            ?: listaRemota.firstOrNull()

                        if (activaRes != null) {
                            cacheEmpresa.guardarEmpresaActiva(activaRes, sId)
                        }

                        _uiState.value = _uiState.value.copy(
                            empresas = listaRemota,
                            hotelActivo = activaRes,
                            hotelAjustesSeleccionado = _uiState.value.hotelAjustesSeleccionado ?: activaRes,
                            isLoading = false
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                )
            }
        }
    }

    fun seleccionarHotelActivo(empresa: ModeloEmpresa, onExito: () -> Unit = {}) {
        val sId = smileId
        // ⚡ 1. Actualización Optimista Local-First en 0ms
        cacheEmpresa.guardarEmpresaActiva(empresa, sId)
        val listaActualizada = _uiState.value.empresas.map {
            it.copy(principal = (it.id == empresa.id))
        }
        _uiState.value = _uiState.value.copy(
            hotelActivo = empresa,
            empresas = listaActualizada,
            mensajeExito = "Hotel activo cambiado a: ${empresa.nombreComercial}"
        )
        onExito()

        // ⚡ 2. Sincronización silenciosa en background con Supabase
        val empId = empresa.id
        if (sId.isNotBlank() && !empId.isNullOrBlank()) {
            viewModelScope.launch {
                EmpresasApi.marcarEmpresaPrincipal(sId, empId)
            }
        }
    }

    fun seleccionarHotelParaAjustes(empresa: ModeloEmpresa) {
        _uiState.value = _uiState.value.copy(hotelAjustesSeleccionado = empresa)
    }

    fun consultarRuc(
        ruc: String,
        onResultado: (razonSocial: String, direccion: String, departamento: String, provincia: String, distrito: String, ubigeo: String) -> Unit
    ) {
        if (ruc.length != 11) {
            _uiState.value = _uiState.value.copy(error = "Ingresa un RUC válido de 11 dígitos")
            return
        }

        _uiState.value = _uiState.value.copy(isBuscandoRuc = true)
        viewModelScope.launch {
            val res = SunatApi.consultarRuc(ruc)
            res.fold(
                onSuccess = { rucData ->
                    _uiState.value = _uiState.value.copy(
                        isBuscandoRuc = false,
                        mensajeExito = "RUC encontrado: ${rucData.nombreEmpresa}"
                    )
                    onResultado(
                        rucData.nombreEmpresa,
                        rucData.direccionCompleta,
                        rucData.departamento ?: "",
                        rucData.provincia ?: "",
                        rucData.distrito ?: "",
                        ""
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isBuscandoRuc = false,
                        error = err.localizedMessage ?: "No se pudo consultar el RUC en SUNAT"
                    )
                }
            )
        }
    }

    fun guardarHotel(empresa: ModeloEmpresa, onExito: () -> Unit) {
        if (empresa.nombreComercial.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "El nombre comercial del hotel es obligatorio")
            return
        }

        val sId = smileId
        val dto = empresa.copy(smileId = if (empresa.smileId.isNullOrBlank()) sId else empresa.smileId)
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val res = if (dto.id.isNullOrBlank()) {
                EmpresasApi.crearEmpresa(dto)
            } else {
                EmpresasApi.actualizarEmpresa(dto)
            }

            res.fold(
                onSuccess = { guardada ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        empresaEdicion = null,
                        mensajeExito = "Hotel guardado correctamente: ${guardada.nombreComercial}"
                    )
                    cargarEmpresasLocalFirst()
                    onExito()
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = err.localizedMessage ?: "Error al guardar el hotel en Supabase"
                    )
                }
            )
        }
    }

    fun guardarAjustesFacturacion(
        empresaId: String,
        notaVenta: Boolean,
        boleta: Boolean,
        factura: Boolean,
        impuesto: Double,
        moneda: String
    ) {
        if (empresaId.isBlank()) return

        val listaActualizada = _uiState.value.empresas.map {
            if (it.id == empresaId) {
                it.copy(
                    notaVenta = notaVenta,
                    boleta = boleta,
                    factura = factura,
                    impuestoPorcentaje = impuesto,
                    moneda = moneda
                )
            } else it
        }

        val hotelSeleccionadoActualizado = _uiState.value.hotelAjustesSeleccionado?.copy(
            notaVenta = notaVenta,
            boleta = boleta,
            factura = factura,
            impuestoPorcentaje = impuesto,
            moneda = moneda
        )

        val hotelActivoActualizado = if (_uiState.value.hotelActivo?.id == empresaId) hotelSeleccionadoActualizado else _uiState.value.hotelActivo

        if (hotelActivoActualizado != null && hotelActivoActualizado.id == empresaId) {
            cacheEmpresa.guardarEmpresaActiva(hotelActivoActualizado, smileId)
        }

        _uiState.value = _uiState.value.copy(
            empresas = listaActualizada,
            hotelActivo = hotelActivoActualizado,
            hotelAjustesSeleccionado = hotelSeleccionadoActualizado,
            mensajeExito = "Ajustes de facturación guardados correctamente"
        )

        // Sincronización background Supabase
        viewModelScope.launch {
            EmpresasApi.actualizarAjustesFacturacion(empresaId, notaVenta, boleta, factura, impuesto, moneda)
        }
    }

    fun prepararEdicion(empresa: ModeloEmpresa) {
        _uiState.value = _uiState.value.copy(empresaEdicion = empresa)
    }

    fun cancelarEdicion() {
        _uiState.value = _uiState.value.copy(empresaEdicion = null)
    }

    fun eliminarHotel(empresa: ModeloEmpresa) {
        val empId = empresa.id ?: return
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val res = EmpresasApi.eliminarEmpresa(empId)
            res.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        mensajeExito = "Hotel eliminado"
                    )
                    cargarEmpresasLocalFirst()
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = err.localizedMessage ?: "Error al eliminar hotel"
                    )
                }
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.value = _uiState.value.copy(error = null, mensajeExito = null)
    }
}
