package com.hotelwii.core.kidev

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.WiCss
import com.hotelwii.core.kicss.WiText

/**
 * 🏷️ WiSelectOption — Modelo de opción con valor, etiqueta y descripción.
 */
data class WiSelectOption(
    val value: String,
    val label: String,
    val description: String? = null
)

/**
 * 💎 WiSelect.kt — Selector Desplegable Premium 10/10 (Sin Emojis).
 * Inspirado en wiselect.js (pancitawii-windows) con rotación 180° del chevron, animación Glassmorphism
 * y destaque exclusivo con check dorado ÚNICAMENTE para la opción seleccionada.
 */
@Composable
fun WiSelect(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    label: String = "Seleccionar opción",
    placeholder: String = "Elegir...",
    modifier: Modifier = Modifier
) {
    val optionObjects = remember(options) {
        options.map { WiSelectOption(value = it, label = it) }
    }

    WiSelectAvance(
        selectedValue = selectedOption,
        options = optionObjects,
        onOptionSelected = { onOptionSelected(it.value) },
        label = label,
        placeholder = placeholder,
        modifier = modifier
    )
}

/**
 * WiSelectAvance — Selector avanzado con soporte de descripciones y objetos WiSelectOption.
 */
@Composable
fun WiSelectAvance(
    selectedValue: String,
    options: List<WiSelectOption>,
    onOptionSelected: (WiSelectOption) -> Unit,
    label: String = "Seleccionar opción",
    placeholder: String = "Elegir...",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val selectedOption = options.find { it.value == selectedValue }
    val arrowRotation by animateFloatAsState(targetValue = if (expanded) 180f else 0f, label = "ChevronRotation")

    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) options
        else options.filter {
            it.label.contains(searchQuery, ignoreCase = true) ||
            (it.description?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(modifier = modifier) {
        // Trigger Box (Contenedor Principal de Selección)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(WiCss.inp)
                .border(
                    width = if (expanded) 1.5.dp else 1.dp,
                    color = if (expanded) WiCss.mco else WiCss.brd.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = label,
                        style = WiText.tiny,
                        color = WiCss.tx3,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = selectedOption?.label ?: placeholder,
                        style = WiText.body,
                        color = if (selectedOption != null) WiCss.tx1 else WiCss.tx3,
                        fontWeight = if (selectedOption != null) FontWeight.SemiBold else FontWeight.Normal
                    )
                }

                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    tint = if (expanded) WiCss.mco else WiCss.tx2,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(arrowRotation)
                )
            }
        }

        // Panel Desplegable Glassmorphic con Animación Vertical Fluida
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(WiCss.wb)
                    .border(
                        width = 1.dp,
                        color = WiCss.mco.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(18.dp)
                    )
                    .padding(12.dp)
            ) {
                Column {
                    // Buscador Integrado (Si la lista tiene más de 3 elementos)
                    if (options.size > 3) {
                        WiField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = "Buscar...",
                            leadingIcon = Icons.Rounded.Search,
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = "Limpiar",
                                            tint = WiCss.tx3,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        )
                    }

                    if (filteredOptions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin resultados encontrados",
                                style = WiText.small,
                                color = WiCss.tx3
                            )
                        }
                    } else {
                        LazyColumn(modifier = Modifier.heightIn(max = 220.dp)) {
                            items(filteredOptions) { item ->
                                val isSelected = item.value == selectedValue

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) WiCss.mco.copy(alpha = 0.12f)
                                            else Color.Transparent
                                        )
                                        .clickable {
                                            onOptionSelected(item)
                                            expanded = false
                                            searchQuery = ""
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.label,
                                            style = WiText.body,
                                            color = if (isSelected) WiCss.mco else WiCss.tx1,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )

                                        if (!item.description.isNullOrBlank()) {
                                            Text(
                                                text = item.description,
                                                style = WiText.tiny,
                                                color = if (isSelected) WiCss.mco.copy(alpha = 0.8f) else WiCss.tx3
                                            )
                                        }
                                    }

                                    // Check EXCLUSIVO ÚNICAMENTE para la opción seleccionada
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = "Seleccionado",
                                            tint = WiCss.mco,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
