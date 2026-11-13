package com.example.workclock.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.TimeFormat

/**
 * Portrait quick controls layout: horizontal floating bar across the bottom.
 */
@Composable
fun QuickControls(
    visible: Boolean,
    settings: ClockSettings,
    onToggleStyle: () -> Unit,
    onToggleSeconds: () -> Unit,
    onToggleDate: () -> Unit,
    onToggleTimeZone: () -> Unit,
    onCycleTimeFormat: () -> Unit,
    onToggleKeepAwake: () -> Unit,
    onCycleTheme: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)
    val textColor = Color(settings.theme.timeColor)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically { it / 2 },
        exit = fadeOut() + slideOutVertically { it / 2 },
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = surfaceColor.copy(alpha = 0.92f),
            shadowElevation = 8.dp,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("quick_controls_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                val styleLabel = when (settings.clockStyle) {
                    ClockStyle.SPLIT_FLAP -> "Flip"
                    ClockStyle.MINIMAL_DIGITAL -> "Digital"
                    ClockStyle.LED_SEVEN_SEGMENT -> "LED"
                    ClockStyle.LCD -> "LCD"
                    ClockStyle.TYPOGRAPHY -> "Typo"
                    ClockStyle.ODOMETER -> "Odometer"
                }
                FilterChip(
                    selected = true,
                    onClick = onToggleStyle,
                    label = { Text(styleLabel, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Toggle Clock Style",
                            tint = accentColor
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_style")
                )

                // Seconds Toggle
                FilterChip(
                    selected = settings.showSeconds,
                    onClick = onToggleSeconds,
                    label = { Text("Sec", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Toggle Seconds",
                            tint = if (settings.showSeconds) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_seconds")
                )

                // Date Toggle
                FilterChip(
                    selected = settings.showDate,
                    onClick = onToggleDate,
                    label = { Text("Date", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Toggle Date",
                            tint = if (settings.showDate) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_date")
                )

                // Time Zone Toggle
                FilterChip(
                    selected = settings.showTimeZone,
                    onClick = onToggleTimeZone,
                    label = { Text("Zone", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Toggle Time Zone",
                            tint = if (settings.showTimeZone) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_timezone")
                )

                // Time Format Toggle
                FilterChip(
                    selected = settings.timeFormat != TimeFormat.SYSTEM,
                    onClick = onCycleTimeFormat,
                    label = {
                        val label = when (settings.timeFormat) {
                            TimeFormat.SYSTEM -> "Format: Sys"
                            TimeFormat.HOUR_12 -> "Format: 12h"
                            TimeFormat.HOUR_24 -> "Format: 24h"
                        }
                        Text(label, fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Cycle Time Format",
                            tint = textColor.copy(alpha = 0.7f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_format")
                )

                // Keep Awake Toggle
                FilterChip(
                    selected = settings.keepScreenAwake,
                    onClick = onToggleKeepAwake,
                    label = {
                        Text(if (settings.keepScreenAwake) "Awake: ON" else "Awake: OFF", fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Toggle Screen Awake",
                            tint = if (settings.keepScreenAwake) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_awake")
                )

                // Theme Quick Cycle
                FilterChip(
                    selected = true,
                    onClick = onCycleTheme,
                    label = { Text(settings.theme.displayName, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Change Theme",
                            tint = accentColor
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.15f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_theme")
                )
                }

                // Subtle separator between scrollable chips and pinned Settings button
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(1.dp)
                        .height(22.dp)
                        .background(textColor.copy(alpha = 0.2f))
                )

                // Pinned Full Settings Button (always visible without horizontal scroll)
                IconButton(
                    onClick = onOpenSettings,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = accentColor
                    ),
                    modifier = Modifier.testTag("quick_open_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Open Full Settings"
                    )
                }
            }
        }
    }
}

/**
 * Landscape LEFT options panel: vertically organized display toggles (Seconds, Date, Time Zone).
 */
@Composable
fun QuickControlsLandscapeLeft(
    visible: Boolean,
    settings: ClockSettings,
    onToggleStyle: () -> Unit,
    onToggleSeconds: () -> Unit,
    onToggleDate: () -> Unit,
    onToggleTimeZone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)
    val textColor = Color(settings.theme.timeColor)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally { -it },
        exit = fadeOut() + slideOutHorizontally { -it },
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = surfaceColor.copy(alpha = 0.94f),
            shadowElevation = 8.dp,
            modifier = Modifier.testTag("quick_controls_landscape_left")
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start
            ) {
                val styleLabel = when (settings.clockStyle) {
                    ClockStyle.SPLIT_FLAP -> "Flip"
                    ClockStyle.MINIMAL_DIGITAL -> "Digital"
                    ClockStyle.LED_SEVEN_SEGMENT -> "LED"
                    ClockStyle.LCD -> "LCD"
                    ClockStyle.TYPOGRAPHY -> "Typo"
                    ClockStyle.ODOMETER -> "Odometer"
                }
                FilterChip(
                    selected = true,
                    onClick = onToggleStyle,
                    label = { Text(styleLabel, fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = "Toggle Clock Style",
                            tint = accentColor
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_style_landscape")
                )

                // Seconds Toggle
                FilterChip(
                    selected = settings.showSeconds,
                    onClick = onToggleSeconds,
                    label = { Text("Sec", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Toggle Seconds",
                            tint = if (settings.showSeconds) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_seconds")
                )

                // Date Toggle
                FilterChip(
                    selected = settings.showDate,
                    onClick = onToggleDate,
                    label = { Text("Date", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Toggle Date",
                            tint = if (settings.showDate) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_date")
                )

                // Time Zone Toggle
                FilterChip(
                    selected = settings.showTimeZone,
                    onClick = onToggleTimeZone,
                    label = { Text("Zone", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Toggle Time Zone",
                            tint = if (settings.showTimeZone) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_timezone")
                )
            }
        }
    }
}

/**
 * Landscape RIGHT options panel: format, keep-awake, theme cycle, and full settings.
 */
@Composable
fun QuickControlsLandscapeRight(
    visible: Boolean,
    settings: ClockSettings,
    onCycleTimeFormat: () -> Unit,
    onToggleKeepAwake: () -> Unit,
    onCycleTheme: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)
    val textColor = Color(settings.theme.timeColor)

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally { it },
        exit = fadeOut() + slideOutHorizontally { it },
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = surfaceColor.copy(alpha = 0.94f),
            shadowElevation = 8.dp,
            modifier = Modifier.testTag("quick_controls_landscape_right")
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Time Format Toggle
                FilterChip(
                    selected = settings.timeFormat != TimeFormat.SYSTEM,
                    onClick = onCycleTimeFormat,
                    label = {
                        val label = when (settings.timeFormat) {
                            TimeFormat.SYSTEM -> "Format: Sys"
                            TimeFormat.HOUR_12 -> "Format: 12h"
                            TimeFormat.HOUR_24 -> "Format: 24h"
                        }
                        Text(label, fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Cycle Time Format",
                            tint = textColor.copy(alpha = 0.7f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_format")
                )

                // Keep Awake Toggle
                FilterChip(
                    selected = settings.keepScreenAwake,
                    onClick = onToggleKeepAwake,
                    label = {
                        Text(if (settings.keepScreenAwake) "Awake: ON" else "Awake: OFF", fontSize = 12.sp)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Toggle Screen Awake",
                            tint = if (settings.keepScreenAwake) accentColor else textColor.copy(alpha = 0.6f)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_awake")
                )

                // Theme Quick Cycle
                FilterChip(
                    selected = true,
                    onClick = onCycleTheme,
                    label = { Text(settings.theme.displayName, fontSize = 12.sp, maxLines = 1) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Change Theme",
                            tint = accentColor
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.15f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_toggle_theme")
                )

                // Dedicated Full Settings Chip (separated from Theme)
                FilterChip(
                    selected = false,
                    onClick = onOpenSettings,
                    label = { Text("Settings", fontSize = 12.sp, maxLines = 1) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open Full Settings",
                            tint = accentColor
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = accentColor.copy(alpha = 0.2f),
                        selectedLabelColor = accentColor,
                        labelColor = textColor
                    ),
                    modifier = Modifier.testTag("quick_open_settings")
                )
            }
        }
    }
}
