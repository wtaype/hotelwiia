package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

enum class WiMsgType { Success, Error, Warning, Info }

@Immutable
data class WiMsg(
    val text: String,
    val type: WiMsgType = WiMsgType.Info,
    val durationMs: Long = 1800L,
    val id: Long = System.nanoTime(),
)

@Stable
class WiMessenger {
    var mensaje by mutableStateOf<WiMsg?>(null)
        private set
    var notificacion by mutableStateOf<WiMsg?>(null)
        private set

    fun Mensaje(text: String, type: WiMsgType = WiMsgType.Success, durationMs: Long = 2600L) {
        mensaje = WiMsg(text = text, type = type, durationMs = durationMs)
    }

    fun Notificacion(text: String, type: WiMsgType = WiMsgType.Success, durationMs: Long = 2600L) {
        notificacion = WiMsg(text = text, type = type, durationMs = durationMs)
    }

    fun clearMensaje(id: Long) {
        if (mensaje?.id == id) mensaje = null
    }

    fun clearNotificacion(id: Long) {
        if (notificacion?.id == id) notificacion = null
    }
}

val LocalWiMessenger = compositionLocalOf { WiMessenger() }

@Composable
fun rememberWiMessenger(): WiMessenger = remember { WiMessenger() }

@Composable
fun WiMessengerProvider(messenger: WiMessenger = rememberWiMessenger(), content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWiMessenger provides messenger, content = content)
}

/**
 * 🌟 WiMessengerHost — Contenedor de notificaciones flotantes con alineación Top al 5% de pantalla (top = 10dp).
 */
@Composable
fun WiMessengerHost(messenger: WiMessenger, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().zIndex(50f)) {
        // Notificación flotante en el Top (5% de pantalla desde el inicio)
        WiNotificacionView(
            msg = messenger.notificacion,
            onDone = messenger::clearNotificacion,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp, start = 18.dp, end = 18.dp),
        )
        // Mensaje flotante en el Bottom
        WiMensajeView(
            msg = messenger.mensaje,
            onDone = messenger::clearMensaje,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = clampDp(86f, 9.0f, 124f), start = 18.dp, end = 18.dp),
        )
    }
}

@Composable
private fun WiMensajeView(msg: WiMsg?, onDone: (Long) -> Unit, modifier: Modifier = Modifier) {
    WiFloatingMsg(msg = msg, onDone = onDone, slideDir = 1, modifier = modifier)
}

@Composable
private fun WiNotificacionView(msg: WiMsg?, onDone: (Long) -> Unit, modifier: Modifier = Modifier) {
    WiFloatingMsg(msg = msg, onDone = onDone, slideDir = -1, modifier = modifier)
}

/**
 * WiFloatingMsg — Componente base unificado para mensajes y notificaciones flotantes.
 * @param slideDir 1 = entra/sale desde abajo (mensaje), -1 = entra/sale desde arriba (notificación)
 */
@Composable
private fun WiFloatingMsg(
    msg: WiMsg?,
    onDone: (Long) -> Unit,
    slideDir: Int,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(msg?.id) {
        val active = msg ?: return@LaunchedEffect
        delay(active.durationMs)
        onDone(active.id)
    }
    AnimatedVisibility(
        visible = msg != null,
        enter = fadeIn(animationSpec = WiAnim.trM) + slideInVertically(animationSpec = WiAnim.springBouncy()) { (it / 2) * slideDir },
        exit = fadeOut(animationSpec = WiAnim.trM) + slideOutVertically(animationSpec = WiAnim.springSmooth()) { (it / 2) * slideDir },
        modifier = modifier,
    ) {
        val active = msg ?: return@AnimatedVisibility
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(active.type.wiColor())
                .padding(horizontal = dpSmart(16f, 1.8f, 22f), vertical = dpSmart(8f, 1.0f, 12f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = active.text,
                color = WiCss.white,
                style = WiText.small.copy(
                    fontFamily = fPoppins,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun WiMsgType.wiColor(): Color = when (this) {
    WiMsgType.Success -> WiCss.success
    WiMsgType.Error   -> WiCss.error
    WiMsgType.Warning -> WiCss.warning
    WiMsgType.Info    -> WiCss.mco
}

fun WiMsgType.wiIcon(): ImageVector = when (this) {
    WiMsgType.Success -> Icons.Rounded.CheckCircle
    WiMsgType.Error   -> Icons.Rounded.Warning
    WiMsgType.Warning -> Icons.Rounded.Warning
    WiMsgType.Info    -> Icons.Rounded.Info
}
