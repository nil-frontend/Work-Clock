package com.example.workclock.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.model.BrightnessMode
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.TimeFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: ClockSettings,
    onDismiss: () -> Unit,
    onOpenAppearanceScreen: () -> Unit,
    onClockStyleChange: (ClockStyle) -> Unit,
    onTimeFormatChange: (TimeFormat) -> Unit,
    onShowSecondsChange: (Boolean) -> Unit,
    onShowDateChange: (Boolean) -> Unit,
    onShowTimeZoneChange: (Boolean) -> Unit,
    onKeepAwakeChange: (Boolean) -> Unit,
    onThemeChange: (ClockTheme) -> Unit,
    onBrightnessModeChange: (BrightnessMode) -> Unit,
    onBrightnessLevelChange: (Float) -> Unit,
    onImmersiveModeChange: (Boolean) -> Unit,
    onOledCareChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)
    val textColor = Color(settings.theme.timeColor)
    val secondaryText = Color(settings.theme.secColor)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = surfaceColor,
        contentColor = textColor,
        modifier = modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WorkClock Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("settings_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Settings",
                        tint = secondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Appearance Screen Entry Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.15f)),
                border = BorderStroke(1.5.dp, accentColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                        onOpenAppearanceScreen()
                    }
                    .testTag("open_appearance_screen_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Clock Appearance & Styles",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textColor
                        )
                        Text(
                            text = "Customize styles, fonts, animations & themes with live preview",
                            fontSize = 12.sp,
                            color = secondaryText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Display Options
            SettingsSectionHeader(title = "Time & Date Display", icon = Icons.Default.Visibility, color = accentColor)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Quick Clock Style", fontWeight = FontWeight.SemiBold, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    ClockStyle.entries.forEach { style ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClockStyleChange(style) }
                                .padding(vertical = 4.dp)
                                .testTag("radio_style_${style.name.lowercase()}")
                        ) {
                            RadioButton(
                                selected = settings.clockStyle == style,
                                onClick = { onClockStyleChange(style) },
                                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(style.displayName, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    style.description,
                                    color = secondaryText,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    Text("Time Format", fontWeight = FontWeight.SemiBold, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))
                    TimeFormat.entries.forEach { format ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTimeFormatChange(format) }
                                .padding(vertical = 4.dp)
                                .testTag("radio_format_${format.name.lowercase()}")
                        ) {
                            RadioButton(
                                selected = settings.timeFormat == format,
                                onClick = { onTimeFormatChange(format) },
                                colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(format.label, color = textColor, fontSize = 14.sp)
                        }
                    }

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    SettingsToggleRow(
                        title = "Show Seconds",
                        subtitle = "Display rolling seconds indicator",
                        checked = settings.showSeconds,
                        onCheckedChange = onShowSecondsChange,
                        accentColor = accentColor,
                        testTag = "switch_show_seconds"
                    )

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    SettingsToggleRow(
                        title = "Show Date",
                        subtitle = "Display day, month, and year",
                        checked = settings.showDate,
                        onCheckedChange = onShowDateChange,
                        accentColor = accentColor,
                        testTag = "switch_show_date"
                    )

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    SettingsToggleRow(
                        title = "Show Time Zone",
                        subtitle = "Display time zone identifier (e.g. IST, GMT, EST)",
                        checked = settings.showTimeZone,
                        onCheckedChange = onShowTimeZoneChange,
                        accentColor = accentColor,
                        testTag = "switch_show_timezone"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Screen & Brightness
            SettingsSectionHeader(title = "Screen & Power", icon = Icons.Default.WbSunny, color = accentColor)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsToggleRow(
                        title = "Keep Screen Awake",
                        subtitle = "Prevents screen timeout while clock is open. Power button still locks the screen.",
                        checked = settings.keepScreenAwake,
                        onCheckedChange = onKeepAwakeChange,
                        accentColor = accentColor,
                        testTag = "switch_keep_awake"
                    )

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    Text("App Brightness Override", fontWeight = FontWeight.SemiBold, color = textColor)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBrightnessModeChange(BrightnessMode.SYSTEM) }
                            .padding(vertical = 4.dp)
                            .testTag("radio_brightness_system")
                    ) {
                        RadioButton(
                            selected = settings.brightnessMode == BrightnessMode.SYSTEM,
                            onClick = { onBrightnessModeChange(BrightnessMode.SYSTEM) },
                            colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Follow system brightness", color = textColor, fontSize = 14.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBrightnessModeChange(BrightnessMode.CUSTOM) }
                            .padding(vertical = 4.dp)
                            .testTag("radio_brightness_custom")
                    ) {
                        RadioButton(
                            selected = settings.brightnessMode == BrightnessMode.CUSTOM,
                            onClick = { onBrightnessModeChange(BrightnessMode.CUSTOM) },
                            colors = RadioButtonDefaults.colors(selectedColor = accentColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Custom app brightness", color = textColor, fontSize = 14.sp)
                    }

                    if (settings.brightnessMode == BrightnessMode.CUSTOM) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.BrightnessMedium, contentDescription = null, tint = secondaryText, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Slider(
                                value = settings.brightnessLevel,
                                onValueChange = onBrightnessLevelChange,
                                valueRange = 0.05f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = accentColor, activeTrackColor = accentColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("slider_brightness")
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("${(settings.brightnessLevel * 100).toInt()}%", color = textColor, fontSize = 13.sp)
                        }
                    }

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    SettingsToggleRow(
                        title = "Full-Screen Immersive",
                        subtitle = "Hide Android navigation and status bars for a pure display clock.",
                        checked = settings.immersiveMode,
                        onCheckedChange = onImmersiveModeChange,
                        accentColor = accentColor,
                        testTag = "switch_immersive_mode"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: OLED Display Care
            SettingsSectionHeader(title = "Display Care", icon = Icons.Default.Shield, color = accentColor)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingsToggleRow(
                        title = "OLED Burn-In Care",
                        subtitle = "Subtly shifts display coordinates by a few pixels every 2 minutes to protect AMOLED panels during prolonged sessions.",
                        checked = settings.oledCareEnabled,
                        onCheckedChange = onOledCareChange,
                        accentColor = accentColor,
                        testTag = "switch_oled_care"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 4: Themes
            SettingsSectionHeader(title = "Color Themes", icon = Icons.Default.ColorLens, color = accentColor)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ClockTheme.entries.forEach { theme ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onThemeChange(theme) }
                                .padding(vertical = 8.dp)
                                .testTag("theme_option_${theme.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(theme.bg))
                                    .border(
                                        width = if (settings.theme == theme) 3.dp else 1.dp,
                                        color = if (settings.theme == theme) accentColor else Color.Gray.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(theme.accentColor))
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(theme.displayName, fontWeight = FontWeight.Medium, color = textColor, fontSize = 14.sp)
                            }
                            if (settings.theme == theme) {
                                Text("Active", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 5: Privacy & About
            SettingsSectionHeader(title = "Privacy & About", icon = Icons.Default.Info, color = accentColor)
            Card(
                colors = CardDefaults.cardColors(containerColor = surfaceColor.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Local & Private", fontWeight = FontWeight.SemiBold, color = textColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "WorkClock operates completely on your device. Zero personal data, location tracking, or telemetry is gathered.",
                        color = secondaryText,
                        fontSize = 12.sp
                    )

                    HorizontalDivider(color = secondaryText.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                    Text("Disclaimer", fontWeight = FontWeight.SemiBold, color = textColor, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "WorkClock displays your device's system time. It is not an official or certified timekeeping instrument.",
                        color = secondaryText,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("WorkClock Version 1.0.0", color = secondaryText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 12.sp, color = LocalContentColor.current.copy(alpha = 0.72f))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = accentColor
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
