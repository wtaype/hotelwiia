package com.hotelwii.feature.recepcion.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText

/**
 * 🔒 Fijo.kt — Pantalla Completa 100vh x 100vw Dedicada.
 * Cubre el 100% de la pantalla tapando todo el header superior y las pestañas.
 */
@Composable
fun Fijo(
    onCerrar: () -> Unit,
    titulo: String,
    subtitulo: String? = null,
    icono: ImageVector? = null,
    onVolver: (() -> Unit)? = null,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WiCss.bg)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Propio Fijo 100% Ancho ([ ← Volver ] + Título + [ ✕ Cerrar ])
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WiCss.wb)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Izquierda: Botón Circular [ ← Volver ]
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(WiCss.inp)
                                    .clickable(onClick = { onVolver?.invoke() ?: onCerrar() }),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = WiCss.tx1,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Centro: Título + Acción + # Habitación
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (icono != null) {
                                    Icon(
                                        imageVector = icono,
                                        contentDescription = null,
                                        tint = WiCss.mco,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = titulo,
                                        style = WiText.h4,
                                        color = WiCss.tx1,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (subtitulo != null) {
                                        Text(
                                            text = subtitulo,
                                            style = WiText.small,
                                            color = WiCss.tx3
                                        )
                                    }
                                }
                            }

                            // Derecha: Botón Circular [ ✕ Cerrar ]
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(WiCss.inp)
                                    .clickable(onClick = onCerrar),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Cerrar",
                                    tint = WiCss.tx1,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Línea separadora sutil
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(WiCss.brd)
                    )

                    // Contenido del Flujo Dedicado 100% Pantalla
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        if (scrollable) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                content = content
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                content = content
                            )
                        }
                    }
                }
            }
        }
    }
}
