package com.example.workclock.ui.flip

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.clock.TimeFormatter
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.FontChoice
import java.time.ZonedDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RetroFlipClockDisplay(
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

    val hoursMinutesParts = formattedTime.hoursMinutes.split(":")
    val hoursStr = hoursMinutesParts.getOrElse(0) { "12" }.padStart(2, '0')
    val minutesStr = hoursMinutesParts.getOrElse(1) { "00" }.padStart(2, '0')
    val secondsStr = formattedTime.seconds.removePrefix(":")

    val timeColor = Color(settings.theme.timeColor)
    val secColor = Color(settings.theme.secColor)
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)

    val customFontFamily = remember(settings.fontChoice) {
        settings.fontChoice.fontResId?.let { FontFamily(Font(it, FontWeight.Normal)) } ?: FontFamily.SansSerif
    }
    val fontScale = if (settings.fontChoice == FontChoice.ORBITRON) 0.78f else 0.92f

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

        val talkBackDescription = buildString {
            append("Current time ")
            append(hoursStr)
            append(" ")
            append(minutesStr)
            if (secondsStr.isNotEmpty()) {
                append(" and ")
                append(secondsStr)
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

        val hasFooter = formattedTime.fullDate.isNotEmpty() || formattedTime.timeZone.isNotEmpty()

        if (isLandscape) {
            val hasSeconds = settings.showSeconds && secondsStr.isNotEmpty()
            val horizPad = if (isPreview) 12f else 56f
            val bottomPad = if (isPreview) 6f else 54f
            val footerReserve = if (hasFooter) (if (isPreview) 28f else 38f) else 0f
            val cardGap = settings.digitSpacingDp.coerceIn(4, 24).toFloat()
            val gapCount = if (hasSeconds) 2 else 1

            val usableW = (availableWidth - horizPad * 2 - cardGap * gapCount).coerceAtLeast(100f)
            val usableH = (availableHeight - bottomPad - footerReserve - 8f).coerceAtLeast(60f)

            val widthUnits = 2f + if (hasSeconds) 0.65f else 0f
            val maxCardWFromW = usableW / widthUnits
            val maxCardWFromH = usableH / 1.12f

            val cardWidthVal = minOf(maxCardWFromW, maxCardWFromH * 0.88f, if (isPreview) 130f else 210f)
                .coerceAtLeast(44f)
            val cardHeightVal = (cardWidthVal / 0.88f).coerceAtMost(usableH)
            val cardWidth = cardWidthVal.dp
            val cardHeight = cardHeightVal.dp
            val fontSize = cardHeightVal * 0.68f * fontScale
            val cornerRadiusDp = (settings.cardRadiusDp.toFloat()).coerceAtMost(cardHeightVal * 0.16f).dp

            Column(
                modifier = Modifier
                    .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                    .padding(horizontal = horizPad.dp)
                    .padding(bottom = bottomPad.dp)
                    .semantics { contentDescription = talkBackDescription }
                    .testTag("flip_clock_container_landscape"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(cardGap.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlipCardTile(
                        currentValue = hoursStr,
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        fontSize = fontSize,
                        theme = settings.theme,
                        amPm = formattedTime.amPm,
                        animDurationMs = settings.animationDurationMs,
                        cornerRadius = cornerRadiusDp,
                        fontChoice = settings.fontChoice,
                        animationStyle = settings.animationStyle,
                        testTag = "flip_hours_card"
                    )

                    FlipCardTile(
                        currentValue = minutesStr,
                        cardWidth = cardWidth,
                        cardHeight = cardHeight,
                        fontSize = fontSize,
                        theme = settings.theme,
                        animDurationMs = settings.animationDurationMs,
                        cornerRadius = cornerRadiusDp,
                        fontChoice = settings.fontChoice,
                        animationStyle = settings.animationStyle,
                        testTag = "flip_minutes_card"
                    )

                    if (hasSeconds) {
                        val secCardWidth = (cardWidthVal * 0.65f).dp
                        val secCardHeight = (cardHeightVal * 0.75f).dp
                        val secFontSize = fontSize * 0.62f

                        FlipCardTile(
                            currentValue = secondsStr,
                            cardWidth = secCardWidth,
                            cardHeight = secCardHeight,
                            fontSize = secFontSize,
                            theme = settings.theme,
                            animDurationMs = settings.animationDurationMs,
                            cornerRadius = (cornerRadiusDp.value * 0.8f).dp,
                            fontChoice = settings.fontChoice,
                            animationStyle = settings.animationStyle,
                            testTag = "flip_seconds_card"
                        )
                    }
                }

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
                                fontSize = if (isPreview) 11.sp else 14.sp,
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
                            TimeZonePill(
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
        } else {
            val hasSeconds = settings.showSeconds && secondsStr.isNotEmpty()
            val horizPad = if (isPreview) 10f else 16f
            val bottomPad = if (isPreview) 6f else 60f
            val topHeaderReserve = if (hasFooter) (if (isPreview) 36f else 64f) else 0f
            val cardGap = settings.digitSpacingDp.coerceIn(4, 20).toFloat()
            val secRatio = 0.44f

            val usableW = (availableWidth - horizPad * 2).coerceAtLeast(100f)
            val usableH = (availableHeight - bottomPad - topHeaderReserve - cardGap * (if (hasSeconds) 2 else 1)).coerceAtLeast(120f)

            val heightUnits = 2f + (if (hasSeconds) secRatio else 0f)
            val maxCardHFromH = usableH / heightUnits
            val maxCardWFromW = (usableW * 0.68f).coerceAtMost(240f)

            val cardWidthVal = minOf(maxCardWFromW, maxCardHFromH / 0.90f).coerceAtLeast(72f)
            val cardHeightVal = (cardWidthVal * 0.90f).coerceAtMost(maxCardHFromH)
            val cardWidth = cardWidthVal.dp
            val cardHeight = cardHeightVal.dp
            val fontSize = cardHeightVal * 0.68f * fontScale
            val cornerRadiusDp = (settings.cardRadiusDp.toFloat()).coerceAtMost(cardHeightVal * 0.16f).dp

            Column(
                modifier = Modifier
                    .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                    .padding(horizontal = horizPad.dp)
                    .padding(bottom = bottomPad.dp)
                    .semantics { contentDescription = talkBackDescription }
                    .testTag("flip_clock_container_portrait"),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (hasFooter) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(bottom = if (isPreview) 6.dp else 14.dp)
                            .testTag("date_timezone_row_portrait")
                    ) {
                        if (formattedTime.fullDate.isNotEmpty()) {
                            Text(
                                text = formattedTime.fullDate,
                                color = secColor,
                                fontSize = if (isPreview) 11.sp else 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = customFontFamily,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("time_date")
                            )
                        }

                        if (formattedTime.timeZone.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            TimeZonePill(
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

                FlipCardTile(
                    currentValue = hoursStr,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    fontSize = fontSize,
                    theme = settings.theme,
                    amPm = formattedTime.amPm,
                    animDurationMs = settings.animationDurationMs,
                    cornerRadius = cornerRadiusDp,
                    fontChoice = settings.fontChoice,
                    animationStyle = settings.animationStyle,
                    testTag = "flip_hours_card"
                )

                Spacer(modifier = Modifier.height(cardGap.dp))

                FlipCardTile(
                    currentValue = minutesStr,
                    cardWidth = cardWidth,
                    cardHeight = cardHeight,
                    fontSize = fontSize,
                    theme = settings.theme,
                    animDurationMs = settings.animationDurationMs,
                    cornerRadius = cornerRadiusDp,
                    fontChoice = settings.fontChoice,
                    animationStyle = settings.animationStyle,
                    testTag = "flip_minutes_card"
                )

                if (hasSeconds) {
                    Spacer(modifier = Modifier.height(cardGap.dp))
                    val secWidth = (cardWidthVal * 0.54f).coerceIn(56f, 120f).dp
                    val secHeight = (cardHeightVal * secRatio).coerceIn(36f, 72f).dp
                    val secFontSize = fontSize * 0.44f

                    FlipCardTile(
                        currentValue = secondsStr,
                        cardWidth = secWidth,
                        cardHeight = secHeight,
                        fontSize = secFontSize,
                        theme = settings.theme,
                        animDurationMs = settings.animationDurationMs,
                        cornerRadius = (cornerRadiusDp.value * 0.8f).dp,
                        fontChoice = settings.fontChoice,
                        animationStyle = settings.animationStyle,
                        testTag = "flip_seconds_card_bottom"
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeZonePill(
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
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = modifier.testTag("time_zone_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = if (isCompact) 6.dp else 8.dp,
                vertical = if (isCompact) 2.dp else 3.dp
            )
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(if (isCompact) 10.dp else 12.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = timeZone,
                color = timeColor,
                fontSize = if (isCompact) 10.sp else 11.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.testTag("time_zone_text")
            )
        }
    }
}
