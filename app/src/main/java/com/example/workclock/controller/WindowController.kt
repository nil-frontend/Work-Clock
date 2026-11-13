package com.example.workclock.controller

import android.app.Activity
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.workclock.model.BrightnessMode
import com.example.workclock.model.ClockSettings

class WindowController(private val activity: Activity) {

    fun applySettings(settings: ClockSettings) {
        applyKeepScreenAwake(settings.keepScreenAwake)
        applyBrightness(settings.brightnessMode, settings.brightnessLevel)
        applyImmersiveMode(settings.immersiveMode)
    }

    fun applyKeepScreenAwake(keepAwake: Boolean) {
        val window = activity.window ?: return
        if (keepAwake) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    fun applyBrightness(mode: BrightnessMode, level: Float) {
        val window = activity.window ?: return
        val layoutParams = window.attributes
        layoutParams.screenBrightness = when (mode) {
            BrightnessMode.CUSTOM -> level.coerceIn(0.05f, 1.0f)
            BrightnessMode.SYSTEM -> WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        }
        window.attributes = layoutParams
    }

    fun applyImmersiveMode(immersive: Boolean) {
        val window = activity.window ?: return
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (immersive) {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}
