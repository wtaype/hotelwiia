package com.hotelwii.core.kidev

import com.hotelwii.core.kicss.*


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun WiDialog(
    show: Boolean,
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirmar",
    dismissText: String = "Cancelar"
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            shape = RoundedCornerShape(22.dp),
            containerColor = WiCss.wb,
            titleContentColor = WiCss.tx1,
            textContentColor = WiCss.tx2,
            title = { Text(title, style = WiText.h3) },
            text = { Text(text, style = WiText.body) },
            confirmButton = {
                WiButton(
                    text = confirmText,
                    onClick = onConfirm,
                    containerColor = WiCss.success
                )
            },
            dismissButton = {
                WiButton(
                    text = dismissText,
                    onClick = onDismiss,
                    containerColor = WiCss.offline
                )
            }
        )
    }
}

