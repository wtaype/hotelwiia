package com.hotelwii.feature.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kicss.fPoppins
import com.hotelwii.core.wii.R
import com.hotelwii.feature.auth.components.GoogleProfileModal
import com.hotelwii.feature.auth.tabs.Login
import com.hotelwii.feature.auth.tabs.Registro
import kotlinx.coroutines.launch

import androidx.compose.runtime.LaunchedEffect
import com.hotelwii.core.kidev.WiMessengerHost
import com.hotelwii.core.kidev.WiMsgType
import com.hotelwii.core.kidev.rememberWiMessenger

/**
 * 🔐 AuthPantalla.kt — Pantalla Maestra de Autenticación para HotelWii.
 * Integrada con Google Credential Manager Nativo + Modal de Perfil + WiMessengerHost (Notificaciones Flotantes 0ms).
 */
@Composable
fun AuthPantalla(
    onAuthExitosa: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val messenger = rememberWiMessenger()
    var tabSeleccionada by remember { mutableStateOf("login") } // "login" | "registro" | "google_modal"
    var googleEmailTemp by remember { mutableStateOf("") }

    LaunchedEffect(uiState.exitoMensaje, uiState.error) {
        uiState.exitoMensaje?.let { msg ->
            messenger.Notificacion(msg, type = WiMsgType.Success)
            viewModel.limpiarMensajes()
        }
        uiState.error?.let { err ->
            messenger.Notificacion("❌ $err", type = WiMsgType.Error)
            viewModel.limpiarMensajes()
        }
    }

    fun ejecutarGoogleAuthNativo() {
        val credentialManager = CredentialManager.create(context)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("823253788519-l88gkecmg4nr3vem8ainj5nciebnt1ms.apps.googleusercontent.com")
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCred = GoogleIdTokenCredential.createFrom(credential.data)
                    viewModel.ingresarConGoogle(
                        idToken = googleIdTokenCred.idToken,
                        onAuthExitosa = onAuthExitosa,
                        onRequerirPerfilGoogle = { email ->
                            googleEmailTemp = email
                            tabSeleccionada = "google_modal"
                        }
                    )
                }
            } catch (e: GetCredentialException) {
                // Cancelación del selector de cuentas Google por el usuario
            } catch (e: Exception) {
                // Manejo de error de credenciales
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WiCss.bg)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // 🏨 LOGO CENTRALIZADO HOTELWII CON ANILLO RADIANTE
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .shadow(
                        elevation = 28.dp,
                        shape = CircleShape,
                        ambientColor = WiCss.mco.copy(alpha = 0.28f),
                        spotColor = WiCss.mco.copy(alpha = 0.45f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(WiCss.mco.copy(alpha = 0.55f), WiCss.mco.copy(alpha = 0.18f))
                            ),
                            CircleShape
                        )
                )

                Image(
                    painter = painterResource(R.drawable.splash_logo),
                    contentDescription = "HotelWii Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 🏷️ TÍTULO Y SUBTÍTULO
            Text(
                text = "HotelWii OS",
                fontFamily = fPoppins,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = WiCss.mco,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Sistema Inteligente de Gestión Hotelera & Recepción",
                fontFamily = fPoppins,
                fontSize = 13.sp,
                color = WiCss.tx3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🔀 TOGGLE SWITCH SEGMENTADO (Ingresar | Registrarme)
            if (tabSeleccionada != "google_modal") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (tabSeleccionada == "login") WiCss.mco else Color.Transparent)
                            .clickable { tabSeleccionada = "login" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Ingresar",
                            style = WiText.body,
                            color = if (tabSeleccionada == "login") Color.Black else WiCss.tx,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (tabSeleccionada == "registro") WiCss.mco else Color.Transparent)
                            .clickable { tabSeleccionada = "registro" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Registrarme",
                            style = WiText.body,
                            color = if (tabSeleccionada == "registro") Color.Black else WiCss.tx,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // ⚠️ ALERTA DE ERROR DE AUTENTICACIÓN
            if (uiState.error != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FF5252))
                        .padding(14.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        style = WiText.small,
                        color = Color(0xFFFF5252),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 🎞️ TRANSICIÓN ANIMADA DE CONTENIDO (AnimatedContent 60fps)
            AnimatedContent(
                targetState = tabSeleccionada,
                transitionSpec = {
                    if (targetState == "registro" || targetState == "google_modal") {
                        (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "auth-transition"
            ) { state ->
                when (state) {
                    "login" -> {
                        Login(
                            onIngresar = { user, pass ->
                                viewModel.ingresar(user, pass, onAuthExitosa)
                            },
                            onGoogleAuth = {
                                ejecutarGoogleAuthNativo()
                            },
                            onIrARegistro = {
                                tabSeleccionada = "registro"
                            },
                            isLoading = uiState.isLoading
                        )
                    }
                    "registro" -> {
                        Registro(
                            onRegistrar = { email, pass, usuario, nombre, apellidos, aceptoTerminos ->
                                viewModel.registrar(email, pass, usuario, nombre, apellidos, onAuthExitosa)
                            },
                            onGoogleAuth = {
                                ejecutarGoogleAuthNativo()
                            },
                            onIrALogin = {
                                tabSeleccionada = "login"
                            },
                            isLoading = uiState.isLoading
                        )
                    }
                    "google_modal" -> {
                        GoogleProfileModal(
                            googleEmail = googleEmailTemp,
                            onCompletarRegistro = { username, acepto ->
                                viewModel.completarRegistroGoogle(username, acepto, onAuthExitosa)
                            },
                            onCancelar = {
                                tabSeleccionada = "login"
                            },
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // 🌟 Notificaciones flotantes estilo Apple (kidev WiMessengerHost)
        WiMessengerHost(messenger = messenger)
    }
}
