// WiSelect.kt — Selector desplegable con efecto Glassmorphism y buscador integrado
package com.hotelwii.core.kidev

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hotelwii.core.kicss.*

/**
 * WiSelect — Picker desplegable Glassmorphic elegante.
 */
@Composable
fun WiSelect(
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    label: String = "Seleccionar opción",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) options
        else options.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = modifier) {
        // Trigger Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(WiCss.inp)
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = label, style = WiText.tiny, color = WiCss.tx3)
                    Text(
                        text = selectedOption.ifEmpty { "Elegir..." },
                        style = WiText.body,
                        color = if (selectedOption.isNotEmpty()) WiCss.tx1 else WiCss.tx3,
                        fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    tint = WiCss.mco
                )
            }
        }

        // Dropdown Panel
        if (expanded) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                intensity = 0.85f
            ) {
                // Buscador si hay más de 4 opciones
                if (options.size > 4) {
                    WiField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = "Buscar...",
                        leadingIcon = Icons.Rounded.Search,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(filteredOptions) { option ->
                        val isSelected = option == selectedOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) WiCss.mco.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent)
                                .clickable {
                                    onOptionSelected(option)
                                    expanded = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                style = WiText.body,
                                color = if (isSelected) WiCss.mco else WiCss.tx,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = WiCss.mco
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
