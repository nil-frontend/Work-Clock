package com.example.workclock.clock

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.time.ZonedDateTime

interface TimeProvider {
    fun now(): ZonedDateTime
}

class DefaultTimeProvider : TimeProvider {
    override fun now(): ZonedDateTime = ZonedDateTime.now()
}

class ClockTicker(
    private val timeProvider: TimeProvider = DefaultTimeProvider()
) {
    fun tickFlow(showSeconds: Boolean): Flow<ZonedDateTime> = flow {
        while (currentCoroutineContext().isActive) {
            val now = timeProvider.now()
            emit(now)

            val currentMillis = now.toInstant().toEpochMilli()
            val delayMs = if (showSeconds) {
                1000L - (currentMillis % 1000L)
            } else {
                60000L - (currentMillis % 60000L)
            }
            // Ensure minimum delay of 1ms to avoid busy loop
            delay(delayMs.coerceAtLeast(1L))
        }
    }
}
