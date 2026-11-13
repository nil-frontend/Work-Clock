package com.example.workclock.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.workclock.model.AnimationStyle
import com.example.workclock.model.BrightnessMode
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.FontChoice
import com.example.workclock.model.TimeFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "workclock_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val CLOCK_STYLE = stringPreferencesKey("clock_style")
        val ANIMATION_STYLE = stringPreferencesKey("animation_style")
        val FONT_CHOICE = stringPreferencesKey("font_choice")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val SHOW_SECONDS = booleanPreferencesKey("show_seconds")
        val SHOW_DATE = booleanPreferencesKey("show_date")
        val SHOW_TIME_ZONE = booleanPreferencesKey("show_time_zone")
        val KEEP_SCREEN_AWAKE = booleanPreferencesKey("keep_screen_awake")
        val THEME_ID = stringPreferencesKey("theme_id")
        val BRIGHTNESS_MODE = stringPreferencesKey("brightness_mode")
        val BRIGHTNESS_LEVEL = floatPreferencesKey("brightness_level")
        val ANIMATION_DURATION_MS = intPreferencesKey("animation_duration_ms")
        val DIGIT_SPACING_DP = intPreferencesKey("digit_spacing_dp")
        val CARD_RADIUS_DP = intPreferencesKey("card_radius_dp")
        val BLINK_COLON = booleanPreferencesKey("blink_colon")
        val LED_GLOW_ENABLED = booleanPreferencesKey("led_glow_enabled")
        val IMMERSIVE_MODE = booleanPreferencesKey("immersive_mode")
        val OLED_CARE_ENABLED = booleanPreferencesKey("oled_care_enabled")
    }

    val settingsFlow: Flow<ClockSettings> = context.settingsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val clockStyleStr = preferences[PreferencesKeys.CLOCK_STYLE]
            val clockStyle = try {
                clockStyleStr?.let { ClockStyle.fromId(it) } ?: ClockStyle.SPLIT_FLAP
            } catch (_: Exception) {
                ClockStyle.SPLIT_FLAP
            }

            val animationStyleStr = preferences[PreferencesKeys.ANIMATION_STYLE]
            val animationStyle = try {
                animationStyleStr?.let { AnimationStyle.fromId(it) } ?: AnimationStyle.REALISTIC_FLIP
            } catch (_: Exception) {
                AnimationStyle.REALISTIC_FLIP
            }

            val fontChoiceStr = preferences[PreferencesKeys.FONT_CHOICE]
            val fontChoice = try {
                fontChoiceStr?.let { FontChoice.fromId(it) } ?: FontChoice.ROBOTO_MONO
            } catch (_: Exception) {
                FontChoice.ROBOTO_MONO
            }

            val timeFormatStr = preferences[PreferencesKeys.TIME_FORMAT]
            val timeFormat = try {
                timeFormatStr?.let { TimeFormat.valueOf(it) } ?: TimeFormat.SYSTEM
            } catch (_: Exception) {
                TimeFormat.SYSTEM
            }

            val showSeconds = preferences[PreferencesKeys.SHOW_SECONDS] ?: true
            val showDate = preferences[PreferencesKeys.SHOW_DATE] ?: true
            val showTimeZone = preferences[PreferencesKeys.SHOW_TIME_ZONE] ?: true
            val keepScreenAwake = preferences[PreferencesKeys.KEEP_SCREEN_AWAKE] ?: true
            val themeId = preferences[PreferencesKeys.THEME_ID] ?: ClockTheme.MIDNIGHT.id
            val theme = ClockTheme.fromId(themeId)

            val brightnessModeStr = preferences[PreferencesKeys.BRIGHTNESS_MODE]
            val brightnessMode = try {
                brightnessModeStr?.let { BrightnessMode.valueOf(it) } ?: BrightnessMode.SYSTEM
            } catch (_: Exception) {
                BrightnessMode.SYSTEM
            }

            val brightnessLevel = preferences[PreferencesKeys.BRIGHTNESS_LEVEL] ?: 0.7f
            val animationDurationMs = preferences[PreferencesKeys.ANIMATION_DURATION_MS] ?: 350
            val digitSpacingDp = preferences[PreferencesKeys.DIGIT_SPACING_DP] ?: 8
            val cardRadiusDp = preferences[PreferencesKeys.CARD_RADIUS_DP] ?: 16
            val blinkColon = preferences[PreferencesKeys.BLINK_COLON] ?: true
            val ledGlowEnabled = preferences[PreferencesKeys.LED_GLOW_ENABLED] ?: true
            val immersiveMode = preferences[PreferencesKeys.IMMERSIVE_MODE] ?: false
            val oledCareEnabled = preferences[PreferencesKeys.OLED_CARE_ENABLED] ?: false

            ClockSettings(
                clockStyle = clockStyle,
                animationStyle = animationStyle,
                fontChoice = fontChoice,
                timeFormat = timeFormat,
                showSeconds = showSeconds,
                showDate = showDate,
                showTimeZone = showTimeZone,
                keepScreenAwake = keepScreenAwake,
                theme = theme,
                brightnessMode = brightnessMode,
                brightnessLevel = brightnessLevel.coerceIn(0.05f, 1.0f),
                animationDurationMs = animationDurationMs.coerceIn(150, 800),
                digitSpacingDp = digitSpacingDp.coerceIn(2, 24),
                cardRadiusDp = cardRadiusDp.coerceIn(4, 28),
                blinkColon = blinkColon,
                ledGlowEnabled = ledGlowEnabled,
                immersiveMode = immersiveMode,
                oledCareEnabled = oledCareEnabled
            )
        }

    suspend fun updateClockStyle(style: ClockStyle) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.CLOCK_STYLE] = style.id
        }
    }

    suspend fun updateAnimationStyle(style: AnimationStyle) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.ANIMATION_STYLE] = style.id
        }
    }

    suspend fun updateFontChoice(font: FontChoice) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.FONT_CHOICE] = font.id
        }
    }

    suspend fun updateTimeFormat(format: TimeFormat) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.TIME_FORMAT] = format.name
        }
    }

    suspend fun updateShowSeconds(show: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_SECONDS] = show
        }
    }

    suspend fun updateShowDate(show: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_DATE] = show
        }
    }

    suspend fun updateShowTimeZone(show: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.SHOW_TIME_ZONE] = show
        }
    }

    suspend fun updateKeepScreenAwake(keepAwake: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.KEEP_SCREEN_AWAKE] = keepAwake
        }
    }

    suspend fun updateTheme(theme: ClockTheme) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_ID] = theme.id
        }
    }

    suspend fun updateBrightnessMode(mode: BrightnessMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.BRIGHTNESS_MODE] = mode.name
        }
    }

    suspend fun updateBrightnessLevel(level: Float) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.BRIGHTNESS_LEVEL] = level.coerceIn(0.05f, 1.0f)
        }
    }

    suspend fun updateAnimationDuration(durationMs: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.ANIMATION_DURATION_MS] = durationMs.coerceIn(150, 800)
        }
    }

    suspend fun updateDigitSpacing(spacingDp: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.DIGIT_SPACING_DP] = spacingDp.coerceIn(2, 24)
        }
    }

    suspend fun updateCardRadius(radiusDp: Int) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.CARD_RADIUS_DP] = radiusDp.coerceIn(4, 28)
        }
    }

    suspend fun updateBlinkColon(blink: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.BLINK_COLON] = blink
        }
    }

    suspend fun updateLedGlow(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.LED_GLOW_ENABLED] = enabled
        }
    }

    suspend fun updateImmersiveMode(immersive: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.IMMERSIVE_MODE] = immersive
        }
    }

    suspend fun updateOledCareEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.OLED_CARE_ENABLED] = enabled
        }
    }

    suspend fun updateAllSettings(settings: ClockSettings) {
        context.settingsDataStore.edit { prefs ->
            prefs[PreferencesKeys.CLOCK_STYLE] = settings.clockStyle.id
            prefs[PreferencesKeys.ANIMATION_STYLE] = settings.animationStyle.id
            prefs[PreferencesKeys.FONT_CHOICE] = settings.fontChoice.id
            prefs[PreferencesKeys.TIME_FORMAT] = settings.timeFormat.name
            prefs[PreferencesKeys.SHOW_SECONDS] = settings.showSeconds
            prefs[PreferencesKeys.SHOW_DATE] = settings.showDate
            prefs[PreferencesKeys.SHOW_TIME_ZONE] = settings.showTimeZone
            prefs[PreferencesKeys.KEEP_SCREEN_AWAKE] = settings.keepScreenAwake
            prefs[PreferencesKeys.THEME_ID] = settings.theme.id
            prefs[PreferencesKeys.BRIGHTNESS_MODE] = settings.brightnessMode.name
            prefs[PreferencesKeys.BRIGHTNESS_LEVEL] = settings.brightnessLevel.coerceIn(0.05f, 1.0f)
            prefs[PreferencesKeys.ANIMATION_DURATION_MS] = settings.animationDurationMs.coerceIn(150, 800)
            prefs[PreferencesKeys.DIGIT_SPACING_DP] = settings.digitSpacingDp.coerceIn(2, 24)
            prefs[PreferencesKeys.CARD_RADIUS_DP] = settings.cardRadiusDp.coerceIn(4, 28)
            prefs[PreferencesKeys.BLINK_COLON] = settings.blinkColon
            prefs[PreferencesKeys.LED_GLOW_ENABLED] = settings.ledGlowEnabled
            prefs[PreferencesKeys.IMMERSIVE_MODE] = settings.immersiveMode
            prefs[PreferencesKeys.OLED_CARE_ENABLED] = settings.oledCareEnabled
        }
    }
}
