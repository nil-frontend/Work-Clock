package com.example.workclock.ui.styles

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.clock.TimeFormatter
import com.example.workclock.model.AnimationStyle
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.FontChoice
import java.time.ZonedDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OdometerClockDisplay(
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

    val timeParts = formattedTime.hoursMinutes.split(":")
    val hoursStr = timeParts.getOrElse(0) { "12" }.padStart(2, '0')
    val minutesStr = timeParts.getOrElse(1) { "00" }.padStart(2, '0')
    val secondsStr = formattedTime.seconds.removePrefix(":")

    val drumTextColor = Color(settings.theme.timeColor)
    val accentColor = Color(settings.theme.accentColor)
    val secColor = Color(settings.theme.secColor)
    val surfaceColor = Color(settings.theme.surfaceColor)
    val isLight = settings.theme.isLight

    val customFontFamily = remember(settings.fontChoice) {
        settings.fontChoice.fontResId?.let { FontFamily(Font(it, FontWeight.Normal)) } ?: FontFamily.Monospace
    }
    val fontScale = if (settings.fontChoice == FontChoice.ORBITRON) 0.78f else 1f

    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClockTap
            )
            .testTag("odometer_clock_surface"),
        contentAlignment = Alignment.Center
    ) {
        val availW = maxWidth.value
        val availH = maxHeight.value
        val isLandscape = availW > availH

        val hasSeconds = settings.showSeconds && secondsStr.length >= 2
        val hasAmPm = formattedTime.amPm.isNotEmpty()
        val hasFooter = formattedTime.fullDate.isNotEmpty() || formattedTime.timeZone.isNotEmpty()

        val outerHorizPad = if (isPreview) 8f else if (isLandscape) 40f else 12f
        val outerBottomPad = if (isPreview) 6f else if (isLandscape) 50f else 56f
        val frameInnerPad = if (isPreview) 10f else 14f
        val footerReserve = if (hasFooter) (if (isPreview) 28f else if (isLandscape) 40f else 62f) else 0f

        val drumGap = (settings.digitSpacingDp * 0.45f).coerceIn(2f, 8f)
        val usableW = (availW - outerHorizPad * 2 - frameInnerPad * 2 - 12f).coerceAtLeast(100f)
        val usableH = (availH - outerBottomPad - frameInnerPad * 2 - footerReserve - 12f).coerceAtLeast(45f)

        val widthUnits = 4.0f + (if (hasSeconds) 1.44f else 0f)
        val colonReserve = if (isPreview) 14f else 22f
        val amPmReserve = if (hasAmPm) (if (isPreview) 26f else 40f) else 0f
        val gapsReserve = drumGap * (if (hasSeconds) 6f else 4f) + colonReserve + amPmReserve + (if (hasSeconds) 6f else 0f)

        val maxWFromWidth = ((usableW - gapsReserve) / widthUnits).coerceAtLeast(14f)
        val maxWFromHeight = (usableH / 1.55f).coerceAtLeast(14f)

        val drumWidthVal = minOf(maxWFromWidth, maxWFromHeight, if (isPreview) 48f else 84f)
        val drumHeightVal = drumWidthVal * 1.55f
        val drumWidth = drumWidthVal.dp
        val drumHeight = drumHeightVal.dp
        val fontSize = (drumHeightVal * 0.62f * fontScale).coerceIn(14f, 80f)

        val frameBg = if (isLight) Color(0xFFDAD7CE) else Color(0xFF15171C)
        val frameBorder = if (isLight) Color(0xFFBDB9AE) else Color(0xFF2C2F38)
        val frameRadius = settings.cardRadiusDp.coerceIn(8, 24).dp

        val talkBackDesc = "Odometer Clock " + formattedTime.hoursMinutes

        Column(
            modifier = Modifier
                .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                .padding(horizontal = outerHorizPad.dp)
                .padding(bottom = outerBottomPad.dp)
                .semantics { contentDescription = talkBackDesc }
                .testTag("odometer_container"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(frameRadius),
                color = frameBg,
                border = BorderStroke(2.dp, frameBorder),
                shadowElevation = if (isPreview) 6.dp else 12.dp,
                modifier = Modifier.padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = frameInnerPad.dp, vertical = frameInnerPad.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(drumGap.dp)
                ) {
                    OdometerDrum(
                        digitChar = hoursStr[0],
                        width = drumWidth,
                        height = drumHeight,
                        fontSize = fontSize,
                        textColor = drumTextColor,
                        fontFamily = customFontFamily,
                        animDurationMs = settings.animationDurationMs,
                        animationStyle = settings.animationStyle,
                        isLight = isLight
                    )
                    OdometerDrum(
                        digitChar = hoursStr[1],
                        width = drumWidth,
                        height = drumHeight,
                        fontSize = fontSize,
                        textColor = drumTextColor,
                        fontFamily = customFontFamily,
                        animDurationMs = settings.animationDurationMs,
                        animationStyle = settings.animationStyle,
                        isLight = isLight
                    )

                    Box(
                        modifier = Modifier
                            .height(drumHeight)
                            .padding(horizontal = if (isPreview) 2.dp else 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ":",
                            color = accentColor,
                            fontSize = (fontSize * 0.85f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = customFontFamily
                        )
                    }

                    OdometerDrum(
                        digitChar = minutesStr[0],
                        width = drumWidth,
                        height = drumHeight,
                        fontSize = fontSize,
                        textColor = drumTextColor,
                        fontFamily = customFontFamily,
                        animDurationMs = settings.animationDurationMs,
                        animationStyle = settings.animationStyle,
                        isLight = isLight
                    )
                    OdometerDrum(
                        digitChar = minutesStr[1],
                        width = drumWidth,
                        height = drumHeight,
                        fontSize = fontSize,
                        textColor = drumTextColor,
                        fontFamily = customFontFamily,
                        animDurationMs = settings.animationDurationMs,
                        animationStyle = settings.animationStyle,
                        isLight = isLight
                    )

                    if (hasSeconds) {
                        Spacer(modifier = Modifier.width(4.dp))
                        val secDrumWidth = (drumWidthVal * 0.72f).dp
                        val secDrumHeight = (drumHeightVal * 0.72f).dp
                        val secFontSize = fontSize * 0.72f
                        val secTextColor = if (isLight) Color(0xFF2C2C2C) else accentColor
                        OdometerDrum(
                            digitChar = secondsStr[0],
                            width = secDrumWidth,
                            height = secDrumHeight,
                            fontSize = secFontSize,
                            textColor = secTextColor,
                            fontFamily = customFontFamily,
                            animDurationMs = settings.animationDurationMs,
                            animationStyle = settings.animationStyle,
                            isLight = isLight,
                            isAccent = true
                        )
                        OdometerDrum(
                            digitChar = secondsStr[1],
                            width = secDrumWidth,
                            height = secDrumHeight,
                            fontSize = secFontSize,
                            textColor = secTextColor,
                            fontFamily = customFontFamily,
                            animDurationMs = settings.animationDurationMs,
                            animationStyle = settings.animationStyle,
                            isLight = isLight,
                            isAccent = true
                        )
                    }

                    if (hasAmPm) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedTime.amPm.uppercase(),
                            color = accentColor,
                            fontSize = (drumHeightVal * 0.20f).coerceIn(9f, 16f).sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }

            if (hasFooter) {
                Spacer(modifier = Modifier.height(if (isPreview) 6.dp else 14.dp))
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
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
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = formattedTime.timeZone,
                                color = drumTextColor,
                                fontSize = if (isPreview) 10.sp else 11.sp,
                                fontWeight = FontWeight.SemiBold,
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
fun OdometerDrum(
    digitChar: Char,
    width: Dp,
    height: Dp,
    fontSize: Float,
    textColor: Color,
    fontFamily: FontFamily,
    animDurationMs: Int,
    animationStyle: AnimationStyle,
    isLight: Boolean,
    modifier: Modifier = Modifier,
    isAccent: Boolean = false
) {
    val targetDigit = digitChar.digitToIntOrNull() ?: 0
    var previousDigit by remember { mutableStateOf(targetDigit) }
    val rollAnim = remember { Animatable(0f) }

    LaunchedEffect(targetDigit, animationStyle) {
        if (targetDigit != previousDigit) {
            if (animationStyle == AnimationStyle.INSTANT || animDurationMs <= 0) {
                previousDigit = targetDigit
                rollAnim.snapTo(0f)
                return@LaunchedEffect
            }
            rollAnim.snapTo(0f)
            try {
                rollAnim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = animDurationMs, easing = FastOutSlowInEasing)
                )
            } finally {
                previousDigit = targetDigit
                rollAnim.snapTo(0f)
            }
        }
    }

    val drumBg = when {
        isLight && isAccent -> Color(0xFFE6DFD3)
        isLight -> Color(0xFFF8F7F3)
        isAccent -> Color(0xFF261D15)
        else -> Color(0xFF1E2026)
    }
    val drumBorder = if (isLight) Color(0xFFB5B0A4) else Color(0xFF333640)
    val shadeColor = if (isLight) Color.Black.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.75f)

    Box(
        modifier = modifier
            .size(width, height)
            .clip(RoundedCornerShape(6.dp))
            .background(drumBg)
            .border(BorderStroke(1.dp, drumBorder), RoundedCornerShape(6.dp))
            .testTag("odometer_drum_$digitChar"),
        contentAlignment = Alignment.Center
    ) {
        val animOffset = rollAnim.value * height.value

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(width, height)
                    .offset(y = animOffset.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = previousDigit.toString(),
                    color = textColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = fontFamily,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
            }

            Box(
                modifier = Modifier
                    .size(width, height)
                    .offset(y = (animOffset - height.value).dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = targetDigit.toString(),
                    color = textColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = fontFamily,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height * 0.28f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(shadeColor, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height * 0.28f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, shadeColor)
                    )
                )
        )
    }
}
