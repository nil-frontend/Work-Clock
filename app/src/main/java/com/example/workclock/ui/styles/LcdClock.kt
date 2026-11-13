package com.example.workclock.ui.styles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import java.time.ZonedDateTime

@Composable
fun LcdClockDisplay(
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
        settings.fontChoice.fontResId?.let { FontFamily(Font(it, FontWeight.Normal)) } ?: FontFamily.Monospace
    }

    val timeParts = formattedTime.hoursMinutes.split(":")
    val hoursStr = timeParts.getOrElse(0) { "12" }.padStart(2, '0')
    val minutesStr = timeParts.getOrElse(1) { "00" }.padStart(2, '0')
    val secondsStr = formattedTime.seconds.removePrefix(":")

    val lcdBackground = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF9EAF95),
                Color(0xFF8F9F86)
            )
        )
    }
    val lcdSegmentDark = Color(0xFF182015)
    val lcdGhostSegment = Color(0xFF85967D)

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
            .testTag("lcd_clock_surface"),
        contentAlignment = Alignment.Center
    ) {
        val availW = maxWidth.value
        val availH = maxHeight.value
        val isLandscape = availW > availH

        val hasSeconds = settings.showSeconds && secondsStr.length >= 2
        val hasAmPm = formattedTime.amPm.isNotEmpty()
        val hasDate = formattedTime.fullDate.isNotEmpty()
        val hasZone = formattedTime.timeZone.isNotEmpty()

        val outerHorizPad = if (isPreview) 8f else if (isLandscape) 40f else 12f
        val outerBottomPad = if (isPreview) 6f else if (isLandscape) 50f else 56f
        val innerHorizPad = if (isPreview) 14f else 22f
        val innerVertPad = if (isPreview) 10f else 18f

        val headerReserve = if (hasDate) (if (isPreview) 18f else 26f) else 0f
        val footerReserve = if (hasZone) (if (isPreview) 18f else 24f) else 0f

        val digitGap = (settings.digitSpacingDp * 0.5f).coerceIn(3f, 10f)

        val usableW = (availW - outerHorizPad * 2 - innerHorizPad * 2 - 12f).coerceAtLeast(80f)
        val usableH = (availH - outerBottomPad - innerVertPad * 2 - headerReserve - footerReserve - 12f).coerceAtLeast(40f)

        val widthUnits = 4.56f + (if (hasSeconds) 1.20f else 0f)
        val amPmReserve = if (hasAmPm) (if (isPreview) 24f else 38f) else 0f
        val fixedGaps = digitGap * 2f + (if (hasSeconds) digitGap + 8f else 0f) + amPmReserve

        val maxWFromWidth = ((usableW - fixedGaps) / widthUnits).coerceAtLeast(10f)
        val maxWFromHeight = (usableH / 1.85f).coerceAtLeast(10f)

        val digitWidthVal = minOf(maxWFromWidth, maxWFromHeight, if (isPreview) 44f else 80f)
        val digitWidth = digitWidthVal.dp
        val digitHeight = (digitWidthVal * 1.85f).dp

        val outerRadius = settings.cardRadiusDp.coerceIn(10, 26).dp
        val innerRadius = (settings.cardRadiusDp - 4).coerceIn(6, 22).dp

        val talkBackDesc = "LCD Clock " + formattedTime.hoursMinutes

        Surface(
            shape = RoundedCornerShape(outerRadius),
            color = Color(0xFF141716),
            border = BorderStroke(3.dp, Color(0xFF282C29)),
            shadowElevation = if (isPreview) 6.dp else 14.dp,
            modifier = Modifier
                .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                .padding(horizontal = outerHorizPad.dp)
                .padding(bottom = outerBottomPad.dp)
                .semantics { contentDescription = talkBackDesc }
                .testTag("lcd_frame_container")
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(innerRadius))
                    .background(lcdBackground)
                    .border(BorderStroke(2.dp, Color(0xFF75856D)), RoundedCornerShape(innerRadius))
                    .padding(horizontal = innerHorizPad.dp, vertical = innerVertPad.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (hasDate) {
                    Text(
                        text = formattedTime.fullDate.uppercase(),
                        color = lcdSegmentDark.copy(alpha = 0.85f),
                        fontSize = if (isPreview) 9.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                        letterSpacing = 0.8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = if (isPreview) 6.dp else 10.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    SevenSegmentDigit(hoursStr[0], digitWidth, digitHeight, lcdSegmentDark, lcdGhostSegment)
                    Spacer(modifier = Modifier.width(digitGap.dp))
                    SevenSegmentDigit(hoursStr[1], digitWidth, digitHeight, lcdSegmentDark, lcdGhostSegment)

                    LedColon(
                        visible = colonVisible,
                        digitHeight = digitHeight,
                        litColor = lcdSegmentDark,
                        unlitColor = lcdGhostSegment
                    )

                    SevenSegmentDigit(minutesStr[0], digitWidth, digitHeight, lcdSegmentDark, lcdGhostSegment)
                    Spacer(modifier = Modifier.width(digitGap.dp))
                    SevenSegmentDigit(minutesStr[1], digitWidth, digitHeight, lcdSegmentDark, lcdGhostSegment)

                    if (hasSeconds) {
                        Spacer(modifier = Modifier.width((digitGap * 1.1f).coerceAtLeast(5f).dp))
                        val secWidth = (digitWidthVal * 0.60f).dp
                        val secHeight = (digitWidthVal * 1.85f * 0.60f).dp
                        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(bottom = 3.dp)) {
                            SevenSegmentDigit(secondsStr[0], secWidth, secHeight, lcdSegmentDark, lcdGhostSegment)
                            Spacer(modifier = Modifier.width((digitGap * 0.55f).dp))
                            SevenSegmentDigit(secondsStr[1], secWidth, secHeight, lcdSegmentDark, lcdGhostSegment)
                        }
                    }

                    if (hasAmPm) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formattedTime.amPm.uppercase(),
                            color = lcdSegmentDark,
                            fontSize = (digitHeight.value * 0.20f).coerceIn(8f, 20f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                if (hasZone) {
                    Spacer(modifier = Modifier.height(if (isPreview) 6.dp else 10.dp))
                    Text(
                        text = formattedTime.timeZone,
                        color = lcdSegmentDark.copy(alpha = 0.8f),
                        fontSize = if (isPreview) 9.sp else 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = customFontFamily,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
