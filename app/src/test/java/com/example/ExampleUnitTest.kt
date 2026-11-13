package com.example

import com.example.workclock.clock.ClockTicker
import com.example.workclock.clock.TimeProvider
import com.example.workclock.model.ClockTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ExampleUnitTest {

    @Test
    fun testClockThemeFromId() {
        assertEquals(ClockTheme.MIDNIGHT, ClockTheme.fromId("midnight"))
        assertEquals(ClockTheme.OLED_BLACK, ClockTheme.fromId("oled_black"))
        assertEquals(ClockTheme.AMBER, ClockTheme.fromId("amber"))
        assertEquals(ClockTheme.FOREST, ClockTheme.fromId("forest"))
        assertEquals(ClockTheme.NAVY, ClockTheme.fromId("navy"))
        assertEquals(ClockTheme.MIDNIGHT, ClockTheme.fromId("invalid_id"))
    }

    @Test
    fun testClockTickerEmitsCurrentTime() = runBlocking {
        val fixedTime = ZonedDateTime.of(2026, 11, 12, 10, 30, 0, 0, ZoneId.of("UTC"))
        val fakeProvider = object : TimeProvider {
            override fun now(): ZonedDateTime = fixedTime
        }
        val ticker = ClockTicker(fakeProvider)
        val emittedTime = ticker.tickFlow(showSeconds = true).first()
        assertEquals(fixedTime, emittedTime)
    }
}
