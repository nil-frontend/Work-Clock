package com.example.workclock.clock

import android.content.Context
import android.text.format.DateFormat
import com.example.workclock.model.TimeFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class FormattedTime(
    val hoursMinutes: String,
    val seconds: String,
    val amPm: String,
    val fullDate: String,
    val timeZone: String
)

object TimeFormatter {

    fun format(
        time: ZonedDateTime,
        timeFormat: TimeFormat,
        showSeconds: Boolean,
        showDate: Boolean,
        showTimeZone: Boolean,
        context: Context,
        locale: Locale = Locale.getDefault()
    ): FormattedTime {
        val use24Hour = when (timeFormat) {
            TimeFormat.HOUR_24 -> true
            TimeFormat.HOUR_12 -> false
            TimeFormat.SYSTEM -> DateFormat.is24HourFormat(context)
        }

        val hoursMinutesPattern = if (use24Hour) "HH:mm" else "h:mm"
        val hoursMinutes = time.format(DateTimeFormatter.ofPattern(hoursMinutesPattern, locale))

        val seconds = if (showSeconds) {
            time.format(DateTimeFormatter.ofPattern(":ss", locale))
        } else {
            ""
        }

        val amPm = if (!use24Hour) {
            time.format(DateTimeFormatter.ofPattern("a", locale))
        } else {
            ""
        }

        val fullDate = if (showDate) {
            // e.g. "Thursday, November 12, 2026"
            time.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", locale))
        } else {
            ""
        }

        val timeZone = if (showTimeZone) {
            val zoneShort = time.format(DateTimeFormatter.ofPattern("z", locale))
            val offset = time.format(DateTimeFormatter.ofPattern("XXX", locale))
            when {
                zoneShort.equals("GMT", ignoreCase = true) || zoneShort.equals("UTC", ignoreCase = true) -> {
                    "GMT"
                }
                zoneShort.startsWith("+") || zoneShort.startsWith("-") -> {
                    "GMT$zoneShort"
                }
                offset == "Z" || offset == "+00:00" -> {
                    zoneShort
                }
                else -> {
                    "$zoneShort (GMT$offset)"
                }
            }
        } else {
            ""
        }

        return FormattedTime(
            hoursMinutes = hoursMinutes,
            seconds = seconds,
            amPm = amPm,
            fullDate = fullDate,
            timeZone = timeZone
        )
    }
}
