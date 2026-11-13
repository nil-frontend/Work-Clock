package com.example.workclock.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.workclock.clock.ClockTicker
import com.example.workclock.data.SettingsRepository
import com.example.workclock.model.AnimationStyle
import com.example.workclock.model.BrightnessMode
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.ClockUiState
import com.example.workclock.model.FontChoice
import com.example.workclock.model.TimeFormat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import kotlin.random.Random

@OptIn(ExperimentalCoroutinesApi::class)
class ClockViewModel(
    application: Application,
    private val repository: SettingsRepository = SettingsRepository(application),
    private val ticker: ClockTicker = ClockTicker()
) : AndroidViewModel(application) {

    val settings: StateFlow<ClockSettings> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ClockSettings()
        )

    private val _controlsVisible = MutableStateFlow(false)
    val controlsVisible: StateFlow<Boolean> = _controlsVisible.asStateFlow()

    private val _pixelShiftX = MutableStateFlow(0f)
    val pixelShiftX: StateFlow<Float> = _pixelShiftX.asStateFlow()

    private val _pixelShiftY = MutableStateFlow(0f)
    val pixelShiftY: StateFlow<Float> = _pixelShiftY.asStateFlow()

    private var autoHideControlsJob: Job? = null
    private var pixelShiftJob: Job? = null

    // Emits time ticks aligned with second/minute boundaries based on showSeconds setting
    private val timeFlow = settings
        .map { it.showSeconds }
        .distinctUntilChanged()
        .flatMapLatest { showSeconds ->
            ticker.tickFlow(showSeconds)
        }

    val uiState: StateFlow<ClockUiState> = combine(
        timeFlow,
        settings,
        _controlsVisible,
        _pixelShiftX,
        _pixelShiftY
    ) { time, currentSettings, controlsShown, shiftX, shiftY ->
        ClockUiState(
            currentTime = time,
            settings = currentSettings,
            controlsVisible = controlsShown,
            pixelShiftX = shiftX,
            pixelShiftY = shiftY
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ClockUiState()
    )

    init {
        observeOledCare()
    }

    private fun observeOledCare() {
        viewModelScope.launch {
            settings.map { it.oledCareEnabled }.distinctUntilChanged().collect { enabled ->
                pixelShiftJob?.cancel()
                if (enabled) {
                    pixelShiftJob = launch {
                        while (isActive) {
                            delay(120_000L) // Shift every 2 minutes
                            _pixelShiftX.value = Random.nextInt(-4, 5).toFloat()
                            _pixelShiftY.value = Random.nextInt(-4, 5).toFloat()
                        }
                    }
                } else {
                    _pixelShiftX.value = 0f
                    _pixelShiftY.value = 0f
                }
            }
        }
    }

    fun onClockTap() {
        if (_controlsVisible.value) {
            hideControls()
        } else {
            showControls()
        }
    }

    fun showControls() {
        _controlsVisible.value = true
        resetAutoHideTimer()
    }

    fun hideControls() {
        _controlsVisible.value = false
        autoHideControlsJob?.cancel()
    }

    fun resetAutoHideTimer() {
        autoHideControlsJob?.cancel()
        autoHideControlsJob = viewModelScope.launch {
            delay(5000L)
            _controlsVisible.value = false
        }
    }

    fun setClockStyle(style: ClockStyle) {
        viewModelScope.launch {
            repository.updateClockStyle(style)
        }
    }

    fun toggleClockStyle() {
        val styles = ClockStyle.entries
        val nextIndex = (styles.indexOf(settings.value.clockStyle) + 1) % styles.size
        val nextStyle = styles[nextIndex]
        viewModelScope.launch {
            repository.updateClockStyle(nextStyle)
        }
    }

    fun setTimeFormat(format: TimeFormat) {
        viewModelScope.launch {
            repository.updateTimeFormat(format)
        }
    }

    fun toggleSeconds() {
        viewModelScope.launch {
            repository.updateShowSeconds(!settings.value.showSeconds)
        }
    }

    fun toggleDate() {
        viewModelScope.launch {
            repository.updateShowDate(!settings.value.showDate)
        }
    }

    fun toggleTimeZone() {
        viewModelScope.launch {
            repository.updateShowTimeZone(!settings.value.showTimeZone)
        }
    }

    fun setShowTimeZone(show: Boolean) {
        viewModelScope.launch {
            repository.updateShowTimeZone(show)
        }
    }

    fun toggleKeepScreenAwake() {
        viewModelScope.launch {
            repository.updateKeepScreenAwake(!settings.value.keepScreenAwake)
        }
    }

    fun setTheme(theme: ClockTheme) {
        viewModelScope.launch {
            repository.updateTheme(theme)
        }
    }

    fun setBrightnessMode(mode: BrightnessMode) {
        viewModelScope.launch {
            repository.updateBrightnessMode(mode)
        }
    }

    fun setBrightnessLevel(level: Float) {
        viewModelScope.launch {
            repository.updateBrightnessLevel(level)
        }
    }

    fun toggleImmersiveMode() {
        viewModelScope.launch {
            repository.updateImmersiveMode(!settings.value.immersiveMode)
        }
    }

    fun setImmersiveMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateImmersiveMode(enabled)
        }
    }

    fun setAnimationStyle(style: AnimationStyle) {
        viewModelScope.launch {
            repository.updateAnimationStyle(style)
        }
    }

    fun setFontChoice(font: FontChoice) {
        viewModelScope.launch {
            repository.updateFontChoice(font)
        }
    }

    fun setAnimationDuration(durationMs: Int) {
        viewModelScope.launch {
            repository.updateAnimationDuration(durationMs)
        }
    }

    fun setDigitSpacing(spacingDp: Int) {
        viewModelScope.launch {
            repository.updateDigitSpacing(spacingDp)
        }
    }

    fun setCardRadius(radiusDp: Int) {
        viewModelScope.launch {
            repository.updateCardRadius(radiusDp)
        }
    }

    fun setBlinkColon(blink: Boolean) {
        viewModelScope.launch {
            repository.updateBlinkColon(blink)
        }
    }

    fun setLedGlow(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateLedGlow(enabled)
        }
    }

    fun toggleOledCare() {
        viewModelScope.launch {
            repository.updateOledCareEnabled(!settings.value.oledCareEnabled)
        }
    }

    fun setOledCareEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateOledCareEnabled(enabled)
        }
    }

    fun applyAllSettings(newSettings: ClockSettings) {
        viewModelScope.launch {
            repository.updateAllSettings(newSettings)
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClockViewModel(application) as T
        }
    }
}
