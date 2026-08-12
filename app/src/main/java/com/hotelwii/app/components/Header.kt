package com.hotelwii.app.components

import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hotelwii.app.MetaRuta
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kidev.wiStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.hotelwii.feature.auth.data.CacheSmile

/**
 * 🧩 Header.kt — Encabezado 100% Ancho (0ms Latencia con Caché RAM WiStore & Avatar Reactivo Sin Parpadeo).
 */
@Composable
fun Header(
    meta: MetaRuta,
    onToggleSidebar: () -> Unit = {},
    onClickAvatar: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val store = remember { wiStore(context) }
    val cacheSmile = remember { CacheSmile.getInstance(context) }
    val sesionActiva by cacheSmile.sesionActivaFlow.collectAsState()

    val avatarUrl = sesionActiva?.avatar
    val nom = sesionActiva?.nombre
    val usr = sesionActiva?.usuario
    val nombreUsuario = if (!nom.isNullOrBlank()) nom else if (!usr.isNullOrBlank()) usr else "Usuario"
    val iniciales = cacheSmile.getInicialesNombre()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RectangleShape)
            .background(WiCss.wb)
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Izquierda: Hamburguesa + Ícono del Feature + Título y Subtítulo
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onToggleSidebar,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = WiIcons.Menu,
                        contentDescription = "Menú",
                        tint = WiCss.mco
                    )
                }

                Spacer(Modifier.width(8.dp))

                Icon(
                    imageVector = meta.icono,
                    contentDescription = null,
                    tint = WiCss.mco,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = meta.titulo,
                        style = WiText.body,
                        color = WiCss.tx,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = meta.subtitulo,
                        style = WiText.tiny,
                        color = WiCss.tx3,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Derecha: Avatar Interactivo Dinámico (Latencia 0ms RAM Cache + Iniciales Elegantes)
            WiAvatarHeader(
                store = store,
                avatarUrl = avatarUrl,
                nombre = nombreUsuario,
                iniciales = iniciales,
                onClick = onClickAvatar
            )
        }
    }
}

@Composable
fun WiAvatarHeader(
    store: com.hotelwii.core.kidev.WiStore,
    avatarUrl: String?,
    nombre: String,
    iniciales: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ⚡ 1. Consulta SÍNCRONA en RAM (< 0.01ms - Cero Suspensión / Cero Espera en renderizado)
    val ramBitmap = remember(avatarUrl) { store.getAvatarBitmapRam(avatarUrl ?: "") }
    var loadedBitmap by remember(avatarUrl) { mutableStateOf(ramBitmap) }

    // ⚡ 2. Si no está en RAM, descargar en 2do plano SILENCIOSO en background (Dispatchers.IO)
    LaunchedEffect(avatarUrl) {
        if (loadedBitmap == null && !avatarUrl.isNullOrBlank()) {
            val bmp = store.getAvatarBitmap(avatarUrl)
            if (bmp != null) {
                loadedBitmap = bmp
            }
        }
    }

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(WiCss.mco)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val bmp = loadedBitmap
        if (bmp != null) {
            Image(
                bitmap = bmp,
                contentDescription = "Avatar de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 🌟 Badge de Iniciales Local-First 0ms (Sin Bloqueos ni Consultas de Red en Primer Plano)
            Text(
                text = iniciales.ifBlank { "W" },
                style = WiText.small.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

