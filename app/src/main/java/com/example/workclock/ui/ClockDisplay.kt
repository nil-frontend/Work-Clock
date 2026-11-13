package com.example.workclock.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.clock.TimeFormatter
import com.example.workclock.model.AnimationStyle
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.FontChoice
import com.example.workclock.ui.flip.RetroFlipClockDisplay
import com.example.workclock.ui.styles.LcdClockDisplay
import com.example.workclock.ui.styles.LedSevenSegmentClockDisplay
import com.example.workclock.ui.styles.OdometerClockDisplay
import com.example.workclock.ui.styles.TypographyClockDisplay
import java.time.ZonedDateTime

@Composable
fun ClockDisplay(
    currentTime: ZonedDateTime,
    settings: ClockSettings,
    pixelShiftX: Float,
    pixelShiftY: Float,
    onClockTap: () -> Unit,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false
) {
    when (settings.clockStyle) {
        ClockStyle.SPLIT_FLAP -> {
            RetroFlipClockDisplay(
                currentTime = currentTime,
                settings = settings,
                pixelShiftX = pixelShiftX,
                pixelShiftY = pixelShiftY,
                onClockTap = onClockTap,
                modifier = modifier,
                isPreview = isPreview
            )
        }
        ClockStyle.MINIMAL_DIGITAL -> {
            DigitalMinimalClockDisplay(
                currentTime = currentTime,
                settings = settings,
                pixelShiftX = pixelShiftX,
                pixelShiftY = pixelShiftY,
                onClockTap = onClockTap,
                modifier = modifier,
                isPreview = isPreview
            )
        }
        ClockStyle.LED_SEVEN_SEGMENT -> {
            LedSevenSegmentClockDisplay(
                currentTime = currentTime,
                settings = settings,
                pixelShiftX = pixelShiftX,
                pixelShiftY = pixelShiftY,
                onClockTap = onClockTap,
                modifier = modifier,
                isPreview = isPreview
            )
        }
        ClockStyle.LCD -> {
            LcdClockDisplay(
                currentTime = currentTime,
                settings = settings,
                pixelShiftX = pixelShiftX,
                pixelShiftY = pixelShiftY,
                onClockTap = onClockTap,
                modifier = modifier,
                isPreview = isPreview
            )
        }
        ClockStyle.ODOMETER -> {
            OdometerClockDisplay(
                currentTime = currentTime,
                settings = settings,
                pixelShiftX = pixelShiftX,
                pixelShiftY = pixelShiftY,
                onClockTap = onClockTap,
                modifier = modifier,
                isPreview = isPreview
            )
        }
        ClockStyle.TYPOGRAPHY -> {
            TypographyClockDisplay(
                currentTime = currentTime,
                settings = settings,
                pixelShiftX = pixelShiftX,
                pixelShiftY = pixelShiftY,
                onClockTap = onClockTap,
                modifier = modifier,
                isPreview = isPreview
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DigitalMinimalClockDisplay(
    currentTime: ZonedDateTime,
    settings: ClockSettings,
    pixelShiftX: Float,
    pixelShiftY: Float,
    onClockTap: () -> Unit,
    modifier: Modifier = Modifier,
    isPreview: Boolean = false
) {
    val context = LocalContext.current
    val formattedTime = remember(
        currentTime,
        settings.timeFormat,
        settings.showSeconds,
        settings.showDate,
        settings.showTimeZone
    ) {
        TimeFormatter.format(
            time = currentTime,
            timeFormat = settings.timeFormat,
            showSeconds = settings.showSeconds,
            showDate = settings.showDate,
            showTimeZone = settings.showTimeZone,
            context = context
        )
    }

    val customFontFamily = remember(settings.fontChoice) {
        settings.fontChoice.fontResId?.let { FontFamily(Font(it, FontWeight.Normal)) } ?: FontFamily.SansSerif
    }

    val timeColor = Color(settings.theme.timeColor)
    val secColor = Color(settings.theme.secColor)
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)

    val timeParts = formattedTime.hoursMinutes.split(":")
    val hoursText = timeParts.getOrElse(0) { formattedTime.hoursMinutes }
    val minutesText = timeParts.getOrElse(1) { "" }
    val colonAlpha = if (settings.blinkColon && currentTime.second % 2 != 0) 0.2f else 1f

    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClockTap
            )
            .testTag("clock_surface"),
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth.value
        val availableHeight = maxHeight.value
        val isLandscape = availableWidth > availableHeight

        val hasSeconds = formattedTime.seconds.isNotEmpty()
        val hasAmPm = formattedTime.amPm.isNotEmpty()
        val hasFooter = formattedTime.fullDate.isNotEmpty() || formattedTime.timeZone.isNotEmpty()

        val horizPad = if (isPreview) 12f else if (isLandscape) 64f else 16f
        val bottomPad = if (isPreview) 6f else if (isLandscape) 48f else 56f
        val footerReserve = if (hasFooter) (if (isPreview) 28f else if (isLandscape) 42f else 66f) else 0f

        val usableW = (availableWidth - horizPad * 2).coerceAtLeast(100f)
        val usableH = (availableHeight - bottomPad - footerReserve - 10f).coerceAtLeast(50f)

        val charWidthUnits = 3.25f + (if (hasSeconds) 1.35f else 0f) + (if (hasAmPm) 0.85f else 0f)
        val wideFontFactor = if (settings.fontChoice == FontChoice.ORBITRON) 1.25f else 1.0f

        val maxFontFromW = (usableW / (charWidthUnits * wideFontFactor)).coerceAtLeast(16f)
        val maxFontFromH = (usableH * 0.62f).coerceAtLeast(16f)
        val timeFontSizeVal = minOf(maxFontFromW, maxFontFromH, if (isPreview) 66f else 130f)

        val timeFontSize = timeFontSizeVal.sp
        val secondsFontSize = (timeFontSizeVal * 0.45f).coerceIn(12f, 48f).sp
        val amPmFontSize = (timeFontSizeVal * 0.28f).coerceIn(10f, 28f).sp
        val dateFontSize = (if (isPreview) 11f else (timeFontSizeVal * 0.22f).coerceIn(13f, 22f)).sp

        val talkBackDescription = buildString {
            append("Current time ")
            append(formattedTime.hoursMinutes)
            if (formattedTime.seconds.isNotEmpty()) {
                append(" and ")
                append(formattedTime.seconds.removePrefix(":"))
                append(" seconds")
            }
            if (formattedTime.amPm.isNotEmpty()) {
                append(" ")
                append(formattedTime.amPm)
            }
            if (formattedTime.fullDate.isNotEmpty()) {
                append(", ")
                append(formattedTime.fullDate)
            }
            if (formattedTime.timeZone.isNotEmpty()) {
                append(", Time zone: ")
                append(formattedTime.timeZone)
            }
        }

        Column(
            modifier = Modifier
                .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                .padding(horizontal = horizPad.dp)
                .padding(bottom = bottomPad.dp)
                .semantics { contentDescription = talkBackDescription }
                .testTag("clock_digits_container"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedContent(
                targetState = formattedTime.hoursMinutes,
                transitionSpec = {
                    val dur = settings.animationDurationMs.coerceIn(100, 800)
                    when (settings.animationStyle) {
                        AnimationStyle.FADE -> {
                            fadeIn(animationSpec = tween(dur)) togetherWith
                                    fadeOut(animationSpec = tween(dur))
                        }
                        AnimationStyle.SLIDE, AnimationStyle.VERTICAL_ROLL, AnimationStyle.REALISTIC_FLIP -> {
                            slideInVertically(animationSpec = tween(dur)) { it / 2 } +
                                    fadeIn(animationSpec = tween(dur)) togetherWith
                                    slideOutVertically(animationSpec = tween(dur)) { -it / 2 } +
                                    fadeOut(animationSpec = tween(dur))
                        }
                        AnimationStyle.INSTANT -> {
                            fadeIn(animationSpec = tween(1)) togetherWith fadeOut(animationSpec = tween(1))
                        }
                    }
                },
                label = "digital_minimal_hours_minutes"
            ) { targetHoursMinutes ->
                val targetParts = targetHoursMinutes.split(":")
                val dispH = targetParts.getOrElse(0) { hoursText }
                val dispM = targetParts.getOrElse(1) { minutesText }

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.testTag("time_hours_minutes")
                    ) {
                        Text(
                            text = dispH,
                            color = timeColor,
                            fontSize = timeFontSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            style = TextStyle(fontFeatureSettings = "tnum")
                        )
                        if (dispM.isNotEmpty()) {
                            Text(
                                text = ":",
                                color = timeColor,
                                fontSize = timeFontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.alpha(colonAlpha)
                            )
                            Text(
                                text = dispM,
                                color = timeColor,
                                fontSize = timeFontSize,
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
                                maxLines = 1,
                                softWrap = false,
                                style = TextStyle(fontFeatureSettings = "tnum")
                            )
                        }
                    }

                    if (hasSeconds) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = formattedTime.seconds,
                            color = secColor,
                            fontSize = secondsFontSize,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            style = TextStyle(fontFeatureSettings = "tnum"),
                            modifier = Modifier
                                .padding(bottom = (timeFontSizeVal * 0.08f).dp)
                                .testTag("time_seconds")
                        )
                    }

                    if (hasAmPm) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedTime.amPm.uppercase(),
                            color = accentColor,
                            fontSize = amPmFontSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier
                                .padding(bottom = (timeFontSizeVal * 0.12f).dp)
                                .testTag("time_ampm")
                        )
                    }
                }
            }

            if (isLandscape) {
                if (hasFooter) {
                    Spacer(modifier = Modifier.height(if (isPreview) 8.dp else 14.dp))
                    FlowRow(
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.testTag("date_timezone_row_landscape")
                    ) {
                        if (formattedTime.fullDate.isNotEmpty()) {
                            Text(
                                text = formattedTime.fullDate,
                                color = secColor,
                                fontSize = dateFontSize,
                                fontWeight = FontWeight.Medium,
                                fontFamily = customFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .testTag("time_date")
                            )
                        }
                        if (formattedTime.timeZone.isNotEmpty()) {
                            DigitalTimeZoneBadge(
                                timeZone = formattedTime.timeZone,
                                surfaceColor = surfaceColor,
                                accentColor = accentColor,
                                timeColor = timeColor,
                                fontFamily = customFontFamily,
                                isCompact = isPreview
                            )
                        }
                    }
                }
            } else {
                if (formattedTime.fullDate.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(if (isPreview) 8.dp else 14.dp))
                    Text(
                        text = formattedTime.fullDate,
                        color = secColor,
                        fontSize = dateFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = customFontFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("time_date")
                    )
                }
                if (formattedTime.timeZone.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(if (isPreview) 4.dp else 8.dp))
                    DigitalTimeZoneBadge(
                        timeZone = formattedTime.timeZone,
                        surfaceColor = surfaceColor,
                        accentColor = accentColor,
                        timeColor = timeColor,
                        fontFamily = customFontFamily,
                        isCompact = isPreview
                    )
                }
            }
        }
    }
}

@Composable
private fun DigitalTimeZoneBadge(
    timeZone: String,
    surfaceColor: Color,
    accentColor: Color,
    timeColor: Color,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = surfaceColor,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f)),
        modifier = modifier.testTag("time_zone_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (isCompact) 7.dp else 10.dp,
                vertical = if (isCompact) 2.dp else 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(if (isCompact) 11.dp else 13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = timeZone,
                color = timeColor,
                fontSize = if (isCompact) 10.sp else 12.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.testTag("time_zone_text")
            )
        }
    }
}
