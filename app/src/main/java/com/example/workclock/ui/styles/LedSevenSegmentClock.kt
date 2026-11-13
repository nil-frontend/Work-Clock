package com.example.workclock.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.clock.TimeFormatter
import com.example.workclock.model.ClockSettings
import java.time.ZonedDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LedSevenSegmentClockDisplay(
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

    val litColor = Color(settings.theme.accentColor)
    val unlitColor = litColor.copy(alpha = if (settings.theme.isLight) 0.12f else 0.08f)
    val secColor = Color(settings.theme.secColor)
    val surfaceColor = Color(settings.theme.surfaceColor)
    val timeColor = Color(settings.theme.timeColor)

    val customFontFamily = remember(settings.fontChoice) {
        settings.fontChoice.fontResId?.let { FontFamily(Font(it, FontWeight.Normal)) } ?: FontFamily.Monospace
    }

    val timeParts = formattedTime.hoursMinutes.split(":")
    val hoursStr = timeParts.getOrElse(0) { "12" }.padStart(2, '0')
    val minutesStr = timeParts.getOrElse(1) { "00" }.padStart(2, '0')
    val secondsStr = formattedTime.seconds.removePrefix(":")

    val colonVisible = if (settings.blinkColon) {
        currentTime.second % 2 == 0
    } else true

    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClockTap
            )
            .testTag("led_clock_surface"),
        contentAlignment = Alignment.Center
    ) {
        val availW = maxWidth.value
        val availH = maxHeight.value
        val isLandscape = availW > availH

        val hasSeconds = settings.showSeconds && secondsStr.length >= 2
        val hasAmPm = formattedTime.amPm.isNotEmpty()
        val hasFooter = formattedTime.fullDate.isNotEmpty() || formattedTime.timeZone.isNotEmpty()

        val horizPad = if (isPreview) 12f else if (isLandscape) 48f else 16f
        val bottomPad = if (isPreview) 6f else if (isLandscape) 52f else 58f
        val footerReserve = if (hasFooter) (if (isPreview) 30f else if (isLandscape) 40f else 64f) else 0f

        val digitGap = (settings.digitSpacingDp * 0.6f).coerceIn(3f, 12f)
        val usableW = (availW - horizPad * 2).coerceAtLeast(100f)
        val usableH = (availH - bottomPad - footerReserve - 8f).coerceAtLeast(50f)

        val widthUnits = 4.83f + (if (hasSeconds) 1.30f else 0f)
        val amPmReserve = if (hasAmPm) (if (isPreview) 26f else 44f) else 0f
        val fixedGaps = digitGap * 2f + (if (hasSeconds) digitGap + 10f else 0f) + amPmReserve

        val maxWFromWidth = ((usableW - fixedGaps) / widthUnits).coerceAtLeast(12f)
        val maxWFromHeight = (usableH / 1.85f).coerceAtLeast(12f)

        val digitWidthVal = minOf(maxWFromWidth, maxWFromHeight, if (isPreview) 52f else 92f)
        val digitWidth = digitWidthVal.dp
        val digitHeight = (digitWidthVal * 1.85f).dp

        val talkBackDesc = "LED Clock " + formattedTime.hoursMinutes

        Column(
            modifier = Modifier
                .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                .padding(horizontal = horizPad.dp)
                .padding(bottom = bottomPad.dp)
                .semantics { contentDescription = talkBackDesc }
                .testTag("led_clock_container"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SevenSegmentDigit(hoursStr[0], digitWidth, digitHeight, litColor, unlitColor, glowEnabled = settings.ledGlowEnabled)
                Spacer(modifier = Modifier.width(digitGap.dp))
                SevenSegmentDigit(hoursStr[1], digitWidth, digitHeight, litColor, unlitColor, glowEnabled = settings.ledGlowEnabled)

                LedColon(
                    visible = colonVisible,
                    digitHeight = digitHeight,
                    litColor = litColor,
                    unlitColor = unlitColor
                )

                SevenSegmentDigit(minutesStr[0], digitWidth, digitHeight, litColor, unlitColor, glowEnabled = settings.ledGlowEnabled)
                Spacer(modifier = Modifier.width(digitGap.dp))
                SevenSegmentDigit(minutesStr[1], digitWidth, digitHeight, litColor, unlitColor, glowEnabled = settings.ledGlowEnabled)

                if (hasSeconds) {
                    Spacer(modifier = Modifier.width((digitGap * 1.2f).coerceAtLeast(6f).dp))
                    val secWidth = (digitWidthVal * 0.65f).dp
                    val secHeight = (digitWidthVal * 1.85f * 0.65f).dp
                    Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 4.dp)) {
                        SevenSegmentDigit(secondsStr[0], secWidth, secHeight, litColor, unlitColor, glowEnabled = settings.ledGlowEnabled)
                        Spacer(modifier = Modifier.width((digitGap * 0.6f).dp))
                        SevenSegmentDigit(secondsStr[1], secWidth, secHeight, litColor, unlitColor, glowEnabled = settings.ledGlowEnabled)
                    }
                }

                if (hasAmPm) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedTime.amPm.uppercase(),
                        color = litColor,
                        fontSize = (digitHeight.value * 0.20f).coerceIn(9f, 22f).sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            if (hasFooter) {
                Spacer(modifier = Modifier.height(if (isPreview) 8.dp else 16.dp))
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.testTag("led_date_row")
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
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    if (formattedTime.timeZone.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = surfaceColor,
                            border = BorderStroke(1.dp, litColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = formattedTime.timeZone,
                                color = timeColor,
                                fontSize = if (isPreview) 10.sp else 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.padding(
                                    horizontal = if (isPreview) 6.dp else 8.dp,
                                    vertical = if (isPreview) 2.dp else 3.dp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SevenSegmentDigit(
    char: Char,
    width: Dp,
    height: Dp,
    litColor: Color,
    unlitColor: Color,
    modifier: Modifier = Modifier,
    glowEnabled: Boolean = false
) {
    val digit = char.digitToIntOrNull() ?: -1

    val segA = digit in listOf(0, 2, 3, 5, 6, 7, 8, 9)
    val segB = digit in listOf(0, 1, 2, 3, 4, 7, 8, 9)
    val segC = digit in listOf(0, 1, 3, 4, 5, 6, 7, 8, 9)
    val segD = digit in listOf(0, 2, 3, 5, 6, 8, 9)
    val segE = digit in listOf(0, 2, 6, 8)
    val segF = digit in listOf(0, 4, 5, 6, 8, 9)
    val segG = digit in listOf(2, 3, 4, 5, 6, 8, 9)

    Canvas(
        modifier = modifier
            .size(width, height)
            .testTag("seven_segment_$char")
    ) {
        val w = size.width
        val h = size.height
        val t = w * 0.17f
        val gap = t * 0.15f

        fun drawSegment(path: Path, active: Boolean) {
            if (active && glowEnabled) {
                drawPath(
                    path = path,
                    color = litColor.copy(alpha = 0.25f),
                    style = Stroke(width = t * 0.55f)
                )
            }
            drawPath(path, if (active) litColor else unlitColor)
        }

        fun drawHorizSeg(yCenter: Float, active: Boolean) {
            val path = Path().apply {
                moveTo(t + gap, yCenter)
                lineTo(t * 1.5f + gap, yCenter - t / 2)
                lineTo(w - t * 1.5f - gap, yCenter - t / 2)
                lineTo(w - t - gap, yCenter)
                lineTo(w - t * 1.5f - gap, yCenter + t / 2)
                lineTo(t * 1.5f + gap, yCenter + t / 2)
                close()
            }
            drawSegment(path, active)
        }

        fun drawVertSeg(xCenter: Float, yTop: Float, yBottom: Float, active: Boolean) {
            val path = Path().apply {
                moveTo(xCenter, yTop + gap)
                lineTo(xCenter + t / 2, yTop + t * 0.7f + gap)
                lineTo(xCenter + t / 2, yBottom - t * 0.7f - gap)
                lineTo(xCenter, yBottom - gap)
                lineTo(xCenter - t / 2, yBottom - t * 0.7f - gap)
                lineTo(xCenter - t / 2, yTop + t * 0.7f + gap)
                close()
            }
            drawSegment(path, active)
        }

        drawHorizSeg(t / 2, segA)
        drawVertSeg(w - t / 2, t / 2, h / 2, segB)
        drawVertSeg(w - t / 2, h / 2, h - t / 2, segC)
        drawHorizSeg(h - t / 2, segD)
        drawVertSeg(t / 2, h / 2, h - t / 2, segE)
        drawVertSeg(t / 2, t / 2, h / 2, segF)
        drawHorizSeg(h / 2, segG)
    }
}

@Composable
fun LedColon(
    visible: Boolean,
    digitHeight: Dp,
    litColor: Color,
    unlitColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .size(digitHeight * 0.30f, digitHeight)
            .padding(horizontal = 2.dp)
    ) {
        val r = (size.width * 0.22f).coerceAtLeast(1.5f)
        val color = if (visible) litColor else unlitColor
        drawCircle(color, r, Offset(size.width / 2f, size.height * 0.35f))
        drawCircle(color, r, Offset(size.width / 2f, size.height * 0.65f))
    }
}
