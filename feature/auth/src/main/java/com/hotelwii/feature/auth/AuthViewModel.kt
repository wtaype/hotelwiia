package com.hotelwii.feature.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hotelwii.core.data.session.SessionManager
import com.hotelwii.feature.auth.api.AuthApi
import com.hotelwii.feature.auth.data.CacheSmile
import com.hotelwii.feature.auth.lib.Serializar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val cacheSmile = CacheSmile.getInstance(application)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var tempGoogleUserId: String = ""
    private var tempGoogleEmail: String = ""
    private var tempGoogleNombre: String = ""
    private var tempGoogleApellidos: String = ""
    private var tempGoogleAvatar: String = ""

    fun ingresar(emailOrUser: String, pass: String, onExito: () -> Unit) {
        val cleanInput = Serializar.email(emailOrUser)
        if (cleanInput.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState(error = "Por favor ingresa tu usuario/correo y contraseña")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val res = AuthApi.ingresar(cleanInput, pass)
            res.fold(
                onSuccess = { smile ->
                    cacheSmile.guardarSesion(smile)
                    SessionManager.actualizarSession(
                        hotelNombre = "Hotel Wii",
                        usuarioNombre = smile.nombre.ifBlank { smile.usuario }
                    )
                    _uiState.value = AuthUiState(isSuccess = true)
                    onExito()
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState(error = err.localizedMessage ?: "Error al ingresar credenciales")
                }
            )
        }
    }

    fun registrar(
        email: String,
        pass: String,
        usuario: String,
        nombre: String,
        apellidos: String,
        onExito: () -> Unit
    ) {
        val cleanMail = Serializar.email(email)
        val cleanUser = Serializar.usuario(usuario)
        val cleanName = Serializar.nombre(nombre)
        val cleanLastName = Serializar.nombre(apellidos)

        if (cleanMail.isBlank() || pass.isBlank() || cleanUser.isBlank() || cleanName.isBlank()) {
            _uiState.value = AuthUiState(error = "Por favor completa todos los campos requeridos")
            return
        }

        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            if (AuthApi.existeEmail(cleanMail)) {
                _uiState.value = AuthUiState(error = "El correo electrónico ya está registrado. Intenta ingresar.")
                return@launch
            }

            if (AuthApi.existeUsuario(cleanUser)) {
                _uiState.value = AuthUiState(error = "El nombre de usuario '@$cleanUser' ya está ocupado. Elige otro.")
                return@launch
            }

            val res = AuthApi.registrar(
                emailStr = cleanMail,
                passStr = pass,
                usuarioStr = cleanUser,
                nombreStr = cleanName,
                apellidosStr = cleanLastName
            )
            res.fold(
                onSuccess = { smile ->
                    cacheSmile.guardarSesion(smile)
                    SessionManager.actualizarSession(
                        hotelNombre = "Hotel Wii",
                        usuarioNombre = smile.nombre.ifBlank { smile.usuario }
                    )
                    _uiState.value = AuthUiState(isSuccess = true)
                    onExito()
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState(error = err.localizedMessage ?: "Error al crear cuenta")
                }
            )
        }
    }

    fun ingresarConGoogle(
        idToken: String,
        onAuthExitosa: () -> Unit,
        onRequerirPerfilGoogle: (email: String) -> Unit
    ) {
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val res = AuthApi.ingresarConGoogleIdToken(idToken)
            res.fold(
                onSuccess = { resultado ->
                    val smile = resultado.smile
                    if (!resultado.esNuevoUsuario && smile != null) {
                        cacheSmile.guardarSesion(smile)
                        SessionManager.actualizarSession(
                            hotelNombre = "Hotel Wii",
                            usuarioNombre = smile.nombre.ifBlank { smile.usuario }
                        )
                        _uiState.value = AuthUiState(isSuccess = true)
                        onAuthExitosa()
                    } else {
                        tempGoogleUserId = resultado.userId
                        tempGoogleEmail = resultado.email
                        tempGoogleNombre = resultado.nombre
                        tempGoogleApellidos = resultado.apellidos
                        tempGoogleAvatar = resultado.avatar
                        _uiState.value = AuthUiState(isLoading = false)
                        onRequerirPerfilGoogle(resultado.email)
                    }
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState(error = err.localizedMessage ?: "Error en autenticación con Google")
                }
            )
        }
    }

    fun completarRegistroGoogle(usuario: String, aceptoTerminos: Boolean, onAuthExitosa: () -> Unit) {
        val cleanUser = Serializar.usuario(usuario)
        if (cleanUser.isBlank() || !aceptoTerminos) {
            _uiState.value = AuthUiState(error = "Por favor ingresa un usuario válido y acepta los términos")
            return
        }
        _uiState.value = AuthUiState(isLoading = true)

        viewModelScope.launch {
            val res = AuthApi.completarRegistroGoogle(
                userId = tempGoogleUserId,
                email = tempGoogleEmail,
                usuarioStr = cleanUser,
                nombreStr = tempGoogleNombre,
                apellidosStr = tempGoogleApellidos,
                avatarStr = tempGoogleAvatar
            )
            res.fold(
                onSuccess = { smile ->
                    cacheSmile.guardarSesion(smile)
                    SessionManager.actualizarSession(
                        hotelNombre = "Hotel Wii",
                        usuarioNombre = smile.nombre.ifBlank { smile.usuario }
                    )
                    _uiState.value = AuthUiState(isSuccess = true)
                    onAuthExitosa()
                },
                onFailure = { err ->
                    _uiState.value = AuthUiState(error = err.localizedMessage ?: "Error al completar registro Google")
                }
            )
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
