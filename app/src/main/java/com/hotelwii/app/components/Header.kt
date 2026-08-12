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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

/**
 * 🧩 Header.kt — Encabezado 100% Ancho sin border-radius (0 Margin Top, Título Limpio & Avatar Interactivo).
 * Padding ajustado para maximizar la superficie táctil de la barra de pestañas.
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
    val avatarUrl = remember { store.getSmileAvatar() }

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

            // Derecha: Avatar Interactivo Dinámico (Cargador nativo de Google/Smile sin Coil)
            WiAvatarHeader(
                avatarUrl = avatarUrl,
                nombre = remember { store.getSmileNombre() },
                onClick = onClickAvatar
            )
        }
    }
}

@Composable
fun WiAvatarHeader(
    avatarUrl: String?,
    nombre: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bitmapState = produceState<ImageBitmap?>(initialValue = null, key1 = avatarUrl) {
        if (!avatarUrl.isNullOrBlank()) {
            withContext(Dispatchers.IO) {
                try {
                    val stream = URL(avatarUrl).openStream()
                    val bmp = BitmapFactory.decodeStream(stream)
                    value = bmp?.asImageBitmap()
                } catch (e: Exception) {
                    value = null
                }
            }
        } else {
            value = null
        }
    }

    val inicial = remember(nombre) {
        nombre.trim().firstOrNull()?.uppercase() ?: "U"
    }

    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(WiCss.mco.copy(alpha = 0.15f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val loadedBitmap = bitmapState.value
        if (loadedBitmap != null) {
            Image(
                bitmap = loadedBitmap,
                contentDescription = "Avatar de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(36.dp)
            )
        } else {
            androidx.compose.material3.Icon(
                painter = androidx.compose.ui.res.painterResource(id = com.hotelwii.core.wii.R.drawable.logo_circle),
                contentDescription = "Cuenta / Perfil",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
