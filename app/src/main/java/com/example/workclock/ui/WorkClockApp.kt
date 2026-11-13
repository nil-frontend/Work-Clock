package com.example.workclock.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.workclock.ads.BannerAdHost
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.TimeFormat
import com.example.workclock.viewmodel.ClockViewModel

import com.example.workclock.ui.appearance.ClockAppearanceScreen

@Composable
fun WorkClockApp(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings = uiState.settings

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAppearanceScreen by remember { mutableStateOf(false) }

    if (showAppearanceScreen) {
        ClockAppearanceScreen(
            currentSettings = settings,
            currentTime = uiState.currentTime,
            onApplySettings = { newSettings ->
                viewModel.applyAllSettings(newSettings)
            },
            onNavigateBack = { showAppearanceScreen = false },
            modifier = modifier
        )
        return
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val bgColor = Color(settings.theme.bg)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .testTag("workclock_root_container")
    ) {
        // Main Clock Display (centered)
        ClockDisplay(
            currentTime = uiState.currentTime,
            settings = settings,
            pixelShiftX = uiState.pixelShiftX,
            pixelShiftY = uiState.pixelShiftY,
            onClockTap = { viewModel.onClockTap() },
            modifier = Modifier.fillMaxSize()
        )

        if (isLandscape) {
            // Landscape Mode: Options are divided on BOTH sides to prevent crowding the clock/date
            // Left Side: Display options (Style, Seconds, Date, Time Zone)
            QuickControlsLandscapeLeft(
                visible = uiState.controlsVisible,
                settings = settings,
                onToggleStyle = {
                    viewModel.toggleClockStyle()
                    viewModel.resetAutoHideTimer()
                },
                onToggleSeconds = {
                    viewModel.toggleSeconds()
                    viewModel.resetAutoHideTimer()
                },
                onToggleDate = {
                    viewModel.toggleDate()
                    viewModel.resetAutoHideTimer()
                },
                onToggleTimeZone = {
                    viewModel.toggleTimeZone()
                    viewModel.resetAutoHideTimer()
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp, bottom = 52.dp)
            )

            // Right Side: Format, Awake, Theme, and Settings
            QuickControlsLandscapeRight(
                visible = uiState.controlsVisible,
                settings = settings,
                onCycleTimeFormat = {
                    val nextFormat = when (settings.timeFormat) {
                        TimeFormat.SYSTEM -> TimeFormat.HOUR_12
                        TimeFormat.HOUR_12 -> TimeFormat.HOUR_24
                        TimeFormat.HOUR_24 -> TimeFormat.SYSTEM
                    }
                    viewModel.setTimeFormat(nextFormat)
                    viewModel.resetAutoHideTimer()
                },
                onToggleKeepAwake = {
                    viewModel.toggleKeepScreenAwake()
                    viewModel.resetAutoHideTimer()
                },
                onCycleTheme = {
                    val entries = ClockTheme.entries
                    val nextIndex = (entries.indexOf(settings.theme) + 1) % entries.size
                    viewModel.setTheme(entries[nextIndex])
                    viewModel.resetAutoHideTimer()
                },
                onOpenSettings = {
                    viewModel.hideControls()
                    showSettingsSheet = true
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp, bottom = 52.dp)
            )

            // Dedicated, Anchored Bottom Ad Slot
            BannerAdHost(
                backgroundColor = Color(settings.theme.surfaceColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        } else {
            // Portrait Mode: Bottom floating horizontal bar + Anchored Banner Ad
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                QuickControls(
                    visible = uiState.controlsVisible,
                    settings = settings,
                    onToggleStyle = {
                        viewModel.toggleClockStyle()
                        viewModel.resetAutoHideTimer()
                    },
                    onToggleSeconds = {
                        viewModel.toggleSeconds()
                        viewModel.resetAutoHideTimer()
                    },
                    onToggleDate = {
                        viewModel.toggleDate()
                        viewModel.resetAutoHideTimer()
                    },
                    onToggleTimeZone = {
                        viewModel.toggleTimeZone()
                        viewModel.resetAutoHideTimer()
                    },
                    onCycleTimeFormat = {
                        val nextFormat = when (settings.timeFormat) {
                            TimeFormat.SYSTEM -> TimeFormat.HOUR_12
                            TimeFormat.HOUR_12 -> TimeFormat.HOUR_24
                            TimeFormat.HOUR_24 -> TimeFormat.SYSTEM
                        }
                        viewModel.setTimeFormat(nextFormat)
                        viewModel.resetAutoHideTimer()
                    },
                    onToggleKeepAwake = {
                        viewModel.toggleKeepScreenAwake()
                        viewModel.resetAutoHideTimer()
                    },
                    onCycleTheme = {
                        val entries = ClockTheme.entries
                        val nextIndex = (entries.indexOf(settings.theme) + 1) % entries.size
                        viewModel.setTheme(entries[nextIndex])
                        viewModel.resetAutoHideTimer()
                    },
                    onOpenSettings = {
                        viewModel.hideControls()
                        showSettingsSheet = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                BannerAdHost(
                    backgroundColor = Color(settings.theme.surfaceColor),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Full Settings Sheet
        if (showSettingsSheet) {
            SettingsSheet(
                settings = settings,
                onDismiss = { showSettingsSheet = false },
                onOpenAppearanceScreen = { showAppearanceScreen = true },
                onClockStyleChange = { viewModel.setClockStyle(it) },
                onTimeFormatChange = { viewModel.setTimeFormat(it) },
                onShowSecondsChange = { viewModel.toggleSeconds() },
                onShowDateChange = { viewModel.toggleDate() },
                onShowTimeZoneChange = { viewModel.toggleTimeZone() },
                onKeepAwakeChange = { viewModel.toggleKeepScreenAwake() },
                onThemeChange = { viewModel.setTheme(it) },
                onBrightnessModeChange = { viewModel.setBrightnessMode(it) },
                onBrightnessLevelChange = { viewModel.setBrightnessLevel(it) },
                onImmersiveModeChange = { viewModel.setImmersiveMode(it) },
                onOledCareChange = { viewModel.setOledCareEnabled(it) }
            )
        }
    }
}
