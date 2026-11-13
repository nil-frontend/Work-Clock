package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.workclock.clock.TimeFormatter
import com.example.workclock.model.BrightnessMode
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.TimeFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZoneId
import java.time.ZonedDateTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("WorkClock", appName)
    }

    @Test
    fun `test time formatting with 24 hour and seconds and time zone`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 2026-11-12 14:05:09 in Asia/Kolkata (IST)
        val fixedTime = ZonedDateTime.of(2026, 11, 12, 14, 5, 9, 0, ZoneId.of("Asia/Kolkata"))

        val formatted = TimeFormatter.format(
            time = fixedTime,
            timeFormat = TimeFormat.HOUR_24,
            showSeconds = true,
            showDate = true,
            showTimeZone = true,
            context = context
        )

        assertEquals("14:05", formatted.hoursMinutes)
        assertEquals(":09", formatted.seconds)
        assertEquals("", formatted.amPm)
        assertTrue(formatted.fullDate.contains("November"))
        assertTrue(formatted.timeZone.contains("IST"))
    }

    @Test
    fun `test time formatting with GMT time zone`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 2026-11-12 14:05:09 in GMT
        val fixedTime = ZonedDateTime.of(2026, 11, 12, 14, 5, 9, 0, ZoneId.of("GMT"))

        val formatted = TimeFormatter.format(
            time = fixedTime,
            timeFormat = TimeFormat.HOUR_12,
            showSeconds = false,
            showDate = false,
            showTimeZone = true,
            context = context
        )

        assertEquals("2:05", formatted.hoursMinutes)
        assertEquals("", formatted.seconds)
        assertEquals("PM", formatted.amPm)
        assertEquals("", formatted.fullDate)
        assertEquals("GMT", formatted.timeZone)
    }

    @Test
    fun `test default clock settings values`() {
        val settings = ClockSettings()
        assertEquals(ClockStyle.FLIP_CLOCK, settings.clockStyle)
        assertTrue(settings.keepScreenAwake)
        assertTrue(settings.showSeconds)
        assertTrue(settings.showDate)
        assertTrue(settings.showTimeZone)
        assertEquals(TimeFormat.SYSTEM, settings.timeFormat)
        assertEquals(ClockTheme.MIDNIGHT, settings.theme)
        assertEquals(BrightnessMode.SYSTEM, settings.brightnessMode)
        assertFalse(settings.oledCareEnabled)
        assertFalse(settings.immersiveMode)
    }
}
