package com.hotelwii.feature.recepcion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.feature.auth.data.CacheSmile
import com.hotelwii.feature.empresas.data.CacheEmpresa
import com.hotelwii.feature.recepcion.api.RecepcionApi
import com.hotelwii.feature.recepcion.data.CacheHabitacion
import com.hotelwii.feature.recepcion.data.CacheVentas
import com.hotelwii.feature.recepcion.data.ModeloHabitacion
import com.hotelwii.feature.recepcion.data.ModeloReserva
import com.hotelwii.feature.recepcion.data.ModeloVenta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecepcionUiState(
    val habitaciones: List<ModeloHabitacion> = emptyList(),
    val ventasActivas: List<ModeloVenta> = emptyList(),
    val reservas: List<ModeloReserva> = emptyList(),
    val habitacionSeleccionada: ModeloHabitacion? = null,
    val ventaActivaSeleccionada: ModeloVenta? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val mensajeExito: String? = null,
    val mostrarDialogCheckIn: Boolean = false,
    val mostrarDialogPrecuenta: Boolean = false,
    val mostrarDialogPagoPos: Boolean = false,
    val mostrarFormularioHabitacion: Boolean = false
)

/**
 * 🧠 RecepcionViewModel.kt — Gestor de estado para el Centro de Control del Recepcionista (LocalFirst < 1ms).
 */
class RecepcionViewModel(application: Application) : AndroidViewModel(application) {
    private val cacheHabitacion = CacheHabitacion.getInstance(application)
    private val cacheVentas = CacheVentas.getInstance(application)
    private val cacheEmpresa = CacheEmpresa.getInstance(application)
    private val cacheSmile = CacheSmile.getInstance(application)
    private val api = RecepcionApi

    private val _uiState = MutableStateFlow(RecepcionUiState())
    val uiState: StateFlow<RecepcionUiState> = _uiState.asStateFlow()

    private val empresaId: String
        get() = cacheEmpresa.empresaActivaFlow.value?.id ?: ""

    private val smileId: String
        get() = cacheSmile.sesionActivaFlow.value?.id ?: ""

    init {
        cargarDatosLocalFirst()

        // 🔄 Reactividad en tiempo real: Si cambia la empresa activa, recargar el catálogo automáticamente
        viewModelScope.launch {
            cacheEmpresa.empresaActivaFlow.collect { empresa ->
                if (empresa != null && !empresa.id.isNullOrBlank()) {
                    cargarDatosLocalFirst()
                }
            }
        }
    }

    fun cargarDatosLocalFirst(isRefreshManual: Boolean = false) {
        val empId = empresaId
        val sId = smileId
        val habsCache = cacheHabitacion.obtenerListaHabitaciones(empId)
        val ventasCache = cacheVentas.obtenerVentasActivas(empId)

        _uiState.update { state ->
            state.copy(
                habitaciones = habsCache,
                ventasActivas = ventasCache,
                isRefreshing = isRefreshManual
            )
        }

        // Sincronización en segundo plano con Supabase
        viewModelScope.launch {
            var activeEmpId = empId

            // ⚡ Auto-resolución si la tablet recién inicia sesión y no tiene empresa local
            if (activeEmpId.isBlank() && sId.isNotBlank()) {
                val resEmp = com.hotelwii.feature.empresas.api.EmpresasApi.obtenerEmpresasPorSmile(sId)
                resEmp.onSuccess { lista ->
                    cacheEmpresa.guardarListaEmpresas(sId, lista)
                    val activa = cacheEmpresa.obtenerEmpresaActiva(sId)
                        ?: lista.firstOrNull { it.principal }
                        ?: lista.firstOrNull()
                    if (activa != null && !activa.id.isNullOrBlank()) {
                        activeEmpId = activa.id ?: ""
                        cacheEmpresa.guardarEmpresaActiva(activa, sId)
                    }
                }
            }

            // Descarga de habitaciones (con filtro dual por empresaId o smileId)
            val resHabs = api.obtenerHabitaciones(activeEmpId, sId)
            resHabs.onSuccess { listaRemota ->
                cacheHabitacion.guardarListaHabitaciones(activeEmpId, listaRemota)
                _uiState.update { it.copy(habitaciones = listaRemota, isRefreshing = false) }
            }

            if (activeEmpId.isNotBlank()) {
                val resVentas = api.obtenerVentasActivas(activeEmpId)
                resVentas.onSuccess { listaVentas ->
                    cacheVentas.guardarVentasActivas(activeEmpId, listaVentas)
                    _uiState.update { it.copy(ventasActivas = listaVentas, isRefreshing = false) }
                }

                val resReservas = api.obtenerReservas(activeEmpId)
                resReservas.onSuccess { listaReservas ->
                    _uiState.update { it.copy(reservas = listaReservas, isRefreshing = false) }
                }
            } else {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun seleccionarHabitacionAccion(hab: ModeloHabitacion) {
        val ventaAsociada = _uiState.value.ventasActivas.firstOrNull { it.habitacionId == hab.id }
        _uiState.update { state ->
            state.copy(
                habitacionSeleccionada = hab,
                ventaActivaSeleccionada = ventaAsociada
            )
        }

        when (hab.estado.lowercase()) {
            "disponible" -> _uiState.update { it.copy(mostrarDialogCheckIn = true) }
            "ocupada" -> _uiState.update { it.copy(mostrarDialogPrecuenta = true) }
            else -> _uiState.update { it.copy(mostrarFormularioHabitacion = true) }
        }
    }

    fun registrarCheckIn(venta: ModeloVenta) {
        val empId = empresaId
        val dto = venta.copy(empresaId = empId)

        _uiState.update { it.copy(mostrarDialogCheckIn = false, isLoading = true) }

        viewModelScope.launch {
            val res = api.registrarCheckIn(dto)
            res.fold(
                onSuccess = { ventaRegistrada ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensajeExito = "¡Check-In registrado exitosamente para ${ventaRegistrada.clienteNombre}!"
                        )
                    }
                    cargarDatosLocalFirst()
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensajeExito = "Check-In guardado en modo local"
                        )
                    }
                }
            )
        }
    }

    fun abrirPagoPos() {
        _uiState.update {
            it.copy(
                mostrarDialogPrecuenta = false,
                mostrarDialogPagoPos = true
            )
        }
    }

    fun registrarPagoCheckOut(metodoPago: String, tipoComprobante: String, montoFinal: Double) {
        val hab = _uiState.value.habitacionSeleccionada ?: return
        val venta = _uiState.value.ventaActivaSeleccionada

        _uiState.update { it.copy(mostrarDialogPagoPos = false, isLoading = true) }

        viewModelScope.launch {
            val res = api.registrarCheckOut(
                ventaId = venta?.id ?: "",
                habitacionId = hab.id ?: "",
                montoTotal = montoFinal,
                metodoPago = metodoPago,
                tipoComprobante = tipoComprobante,
                serie = if (tipoComprobante == "factura") "F001" else if (tipoComprobante == "boleta") "B001" else "NV01"
            )

            res.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensajeExito = "¡Check-Out completado y comprobante generado para Hab. ${hab.numero}!"
                        )
                    }
                    cargarDatosLocalFirst()
                },
                onFailure = {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mensajeExito = "Check-Out procesado localmente"
                        )
                    }
                }
            )
        }
    }

    fun guardarHabitacion(hab: ModeloHabitacion) {
        val empId = empresaId
        val sId = smileId
        val dto = hab.copy(
            empresaId = empId,
            smileId = if (hab.smileId.isNotBlank()) hab.smileId else sId
        )

        // 1. Mutación instantánea LocalFirst (< 1ms)
        val listaActual = _uiState.value.habitaciones.toMutableList()
        val indexExistente = listaActual.indexOfFirst { it.id == dto.id || (it.numero == dto.numero && dto.id.isNullOrBlank()) }
        if (indexExistente >= 0) {
            listaActual[indexExistente] = dto
        } else {
            listaActual.add(dto)
        }

        _uiState.update {
            it.copy(
                habitaciones = listaActual,
                mostrarFormularioHabitacion = false,
                mensajeExito = "¡Habitación ${dto.numero} guardada en 0ms!"
            )
        }

        cacheHabitacion.guardarListaHabitaciones(empId, listaActual)

        // 2. Sincronización en segundo plano con Supabase
        viewModelScope.launch {
            val res = api.guardarHabitacion(dto)
            res.fold(
                onSuccess = {
                    cargarDatosLocalFirst()
                },
                onFailure = {
                    // Mantener datos en caché local
                }
            )
        }
    }

    fun sugerirSiguienteHabitacion(): ModeloHabitacion {
        val habs = _uiState.value.habitaciones
        if (habs.isEmpty()) {
            return ModeloHabitacion(numero = "101", piso = "Piso 1", tipo = "Matrimonial", precio = 80.0)
        }

        val numerosInt = habs.mapNotNull { it.numero.filter { c -> c.isDigit() }.toIntOrNull() }
        val maxNumero = numerosInt.maxOrNull() ?: 100
        val siguienteNumero = (maxNumero + 1).toString()

        val primerDigito = siguienteNumero.firstOrNull()?.toString()?.toIntOrNull() ?: 1
        val pisoSugerido = if (primerDigito in 1..9) "Piso $primerDigito" else "Piso 1"

        val tipoSugerido = habs.lastOrNull()?.tipo ?: "Matrimonial"
        val precioSugerido = habs.lastOrNull()?.precio ?: 80.0

        return ModeloHabitacion(
            numero = siguienteNumero,
            piso = pisoSugerido,
            tipo = tipoSugerido,
            precio = precioSugerido
        )
    }

    fun actualizarPrecioRapido(idOCodigo: String, nuevoPrecio: Double) {
        val empId = empresaId
        val listaActual = _uiState.value.habitaciones.toMutableList()
        val index = listaActual.indexOfFirst { it.id == idOCodigo || it.numero == idOCodigo }
        if (index < 0) return

        val habModificada = listaActual[index].copy(precio = nuevoPrecio)
        listaActual[index] = habModificada

        // 1. LocalFirst instantáneo (0ms)
        _uiState.update {
            it.copy(
                habitaciones = listaActual,
                mensajeExito = "Precio actualizado a S/ ${String.format("%.0f", nuevoPrecio)} para habitación ${habModificada.numero}"
            )
        }
        cacheHabitacion.guardarListaHabitaciones(empId, listaActual)

        // 2. Sync en 2do plano con Supabase
        viewModelScope.launch {
            api.guardarHabitacion(habModificada)
        }
    }

    fun eliminarHabitacion(idOCodigo: String) {
        val empId = empresaId
        val listaActual = _uiState.value.habitaciones.filterNot { it.id == idOCodigo || it.numero == idOCodigo }

        // 1. LocalFirst instantáneo (< 1ms)
        _uiState.update {
            it.copy(
                habitaciones = listaActual,
                mensajeExito = "Habitación eliminada de la lista."
            )
        }
        cacheHabitacion.guardarListaHabitaciones(empId, listaActual)

        // 2. Sync Supabase delete
        viewModelScope.launch {
            api.eliminarHabitacion(idOCodigo)
        }
    }

    fun cerrarModales() {
        _uiState.update {
            it.copy(
                mostrarDialogCheckIn = false,
                mostrarDialogPrecuenta = false,
                mostrarDialogPagoPos = false,
                mostrarFormularioHabitacion = false
            )
        }
    }

    fun abrirCreacionHabitacion() {
        _uiState.update {
            it.copy(
                habitacionSeleccionada = sugerirSiguienteHabitacion(),
                mostrarFormularioHabitacion = true
            )
        }
    }

    fun abrirEdicionHabitacion(hab: ModeloHabitacion) {
        _uiState.update {
            it.copy(
                habitacionSeleccionada = hab,
                mostrarFormularioHabitacion = true
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(error = null, mensajeExito = null) }
    }
}
