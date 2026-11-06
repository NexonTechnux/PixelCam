package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProSettings
import com.example.ui.theme.PixelYellow

enum class ProControlTab(val label: String) {
    ISO("ISO"),
    SHUTTER("TEMPO"),
    FOCUS("FUOCO"),
    EV("EV"),
    WB("BIANCO (WB)")
}

@Composable
fun ProControlsBar(
    proSettings: ProSettings,
    isRawEnabled: Boolean,
    onProSettingsChanged: (ProSettings) -> Unit,
    onToggleRaw: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(ProControlTab.ISO) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color(0xCC121418))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Tab selector: ISO | TEMPO | FUOCO | EV | WB | [RAW DNG]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(ProControlTab.values()) { _, tab ->
                    val isSelected = tab == selectedTab
                    val valueSummary = when (tab) {
                        ProControlTab.ISO -> if (proSettings.iso == 0) "AUTO" else "${proSettings.iso}"
                        ProControlTab.SHUTTER -> ProSettings.SHUTTER_PRESETS.getOrNull(proSettings.shutterSpeedIndex)?.first ?: "AUTO"
                        ProControlTab.FOCUS -> if (proSettings.focusDistance < 0f) "AUTO" else String.format("%.1f", proSettings.focusDistance)
                        ProControlTab.EV -> String.format("%+.1f", proSettings.evCompensation)
                        ProControlTab.WB -> if (proSettings.wbKelvin == 0) "AUTO" else "${proSettings.wbKelvin}K"
                    }

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF2C3038) else Color(0x33202226))
                            .clickable { selectedTab = tab }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("pro_tab_${tab.name}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab.label,
                            color = if (isSelected) PixelYellow else Color(0x88FFFFFF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = valueSummary,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // RAW DNG Toggle Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isRawEnabled) PixelYellow else Color(0x4426282E))
                    .border(1.dp, if (isRawEnabled) PixelYellow else Color(0x66FFFFFF), RoundedCornerShape(12.dp))
                    .clickable { onToggleRaw() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("pro_raw_toggle_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "RAW",
                    color = if (isRawEnabled) Color.Black else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Sub-controls / Slider for selected tab
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            when (selectedTab) {
                ProControlTab.ISO -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(ProSettings.ISO_PRESETS) { _, isoVal ->
                            val isChosen = proSettings.iso == isoVal
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isChosen) PixelYellow else Color(0xFF202228))
                                    .clickable { onProSettingsChanged(proSettings.copy(iso = isoVal)) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isoVal == 0) "AUTO" else "$isoVal",
                                    color = if (isChosen) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                ProControlTab.SHUTTER -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(ProSettings.SHUTTER_PRESETS) { idx, preset ->
                            val isChosen = proSettings.shutterSpeedIndex == idx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isChosen) PixelYellow else Color(0xFF202228))
                                    .clickable { onProSettingsChanged(proSettings.copy(shutterSpeedIndex = idx)) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = preset.first,
                                    color = if (isChosen) Color.Black else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                ProControlTab.FOCUS -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Macro 🔍",
                                color = Color(0x99FFFFFF),
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (proSettings.focusDistance < 0f) "Autofocus AF" else "Fuoco Manuale MF",
                                color = PixelYellow,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Infinito ∞",
                                color = Color(0x99FFFFFF),
                                fontSize = 11.sp
                            )
                        }
                        Slider(
                            value = if (proSettings.focusDistance < 0f) 0.5f else proSettings.focusDistance,
                            onValueChange = { onProSettingsChanged(proSettings.copy(focusDistance = it)) },
                            colors = SliderDefaults.colors(
                                thumbColor = PixelYellow,
                                activeTrackColor = PixelYellow
                            ),
                            modifier = Modifier.testTag("focus_distance_slider")
                        )
                    }
                }

                ProControlTab.EV -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Compensazione Esposizione: ${String.format("%+.1f", proSettings.evCompensation)} EV",
                            color = PixelYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Slider(
                            value = proSettings.evCompensation,
                            onValueChange = { onProSettingsChanged(proSettings.copy(evCompensation = it)) },
                            valueRange = -3.0f..3.0f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = PixelYellow,
                                activeTrackColor = PixelYellow
                            ),
                            modifier = Modifier.testTag("ev_compensation_slider")
                        )
                    }
                }

                ProControlTab.WB -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            itemsIndexed(ProSettings.WB_PRESETS) { _, preset ->
                                val isChosen = (proSettings.wbKelvin == preset.second)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isChosen) PixelYellow else Color(0xFF202228))
                                        .clickable { onProSettingsChanged(proSettings.copy(wbKelvin = preset.second)) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = preset.first,
                                        color = if (isChosen) Color.Black else Color.White,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Kelvin Slider (2000K - 8500K)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("2000K 🟡", color = Color(0xFFFFB300), fontSize = 10.sp)
                            Text(
                                text = if (proSettings.wbKelvin == 0) "Auto AWB" else "${proSettings.wbKelvin} K",
                                color = PixelYellow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("8500K 🔵", color = Color(0xFF64B5F6), fontSize = 10.sp)
                        }
                        Slider(
                            value = if (proSettings.wbKelvin == 0) 5500f else proSettings.wbKelvin.toFloat(),
                            onValueChange = { onProSettingsChanged(proSettings.copy(wbKelvin = it.toInt())) },
                            valueRange = 2000f..8500f,
                            colors = SliderDefaults.colors(
                                thumbColor = PixelYellow,
                                activeTrackColor = PixelYellow
                            ),
                            modifier = Modifier.testTag("wb_kelvin_slider")
                        )
                    }
                }
            }
        }
    }
}
