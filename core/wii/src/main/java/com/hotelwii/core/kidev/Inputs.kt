package com.hotelwii.core.kidev

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.FzSmart
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiIcons
import com.hotelwii.core.kicss.WiText
import com.hotelwii.core.kicss.fPoppins

/**
 * ✏️ Inputs.kt — Campos de Entrada y Formularios Atómicos de HotelWii.
 */
@Composable
fun WiField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    isSuccess: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontFamily = fPoppins, fontSize = FzSmart.field) },
            leadingIcon = leadingIcon?.let { { Icon(it, null, tint = if (isError) WiCss.error else if (isSuccess) WiCss.success else WiCss.mco, modifier = Modifier.size(FzSmart.fieldIcon)) } },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = singleLine,
            maxLines = maxLines,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = wiFieldColors(isSuccess = isSuccess, isError = isError),
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = WiText.small,
                color = WiCss.error,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun WiPassword(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var visible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontFamily = fPoppins, fontSize = FzSmart.field) },
            leadingIcon = { Icon(Icons.Rounded.Lock, null, tint = if (isError) WiCss.error else if (isSuccess) WiCss.success else WiCss.mco, modifier = Modifier.size(FzSmart.fieldIcon)) },
            trailingIcon = {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) WiIcons.VisibilityOff else WiIcons.Visibility,
                        contentDescription = null,
                        tint = if (isError) WiCss.error else if (isSuccess) WiCss.success else WiCss.mco
                    )
                }
            },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = isError,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = wiFieldColors(isSuccess = isSuccess, isError = isError),
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = WiText.small,
                color = WiCss.error,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
    }
}

/**
 * 🎚️ WiSwitch — Switch Toggle estilo Apple/iOS ultra-pro con animaciones y visibilidad contrastada.
 */
@Composable
fun WiSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
    activeTrackColor: Color = WiCss.success
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "thumbOffset"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) activeTrackColor else WiCss.tx3.copy(alpha = 0.30f),
        animationSpec = tween(durationMillis = 200),
        label = "trackColor"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(WiCss.wb)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = WiText.body,
                color = WiCss.tx,
                fontWeight = FontWeight.SemiBold
            )
            if (!sublabel.isNullOrBlank()) {
                Text(
                    text = sublabel,
                    style = WiText.tiny,
                    color = WiCss.tx3
                )
            }
        }

        Box(
            modifier = Modifier
                .width(48.dp)
                .height(28.dp)
                .clip(CircleShape)
                .background(trackColor)
                .padding(1.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(WiCss.white)
                    .border(
                        width = 0.5.dp,
                        color = if (checked) Color.Transparent else WiCss.tx3.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun wiFieldColors(isSuccess: Boolean = false, isError: Boolean = false) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = if (isError) WiCss.error else if (isSuccess) WiCss.success else WiCss.mco,
    unfocusedBorderColor = if (isError) WiCss.error else if (isSuccess) WiCss.success else WiCss.brd.copy(alpha = 0.60f),
    focusedContainerColor = WiCss.inp.copy(alpha = 0.90f),
    unfocusedContainerColor = WiCss.inp.copy(alpha = 0.60f),
    focusedTextColor = WiCss.tx,
    unfocusedTextColor = WiCss.tx,
    errorTextColor = WiCss.tx,
    errorBorderColor = WiCss.error,
    errorLeadingIconColor = WiCss.error,
    errorTrailingIconColor = WiCss.error,
    errorLabelColor = WiCss.error,
    focusedLabelColor = if (isError) WiCss.error else if (isSuccess) WiCss.success else WiCss.mco,
    unfocusedLabelColor = WiCss.tx3
)
