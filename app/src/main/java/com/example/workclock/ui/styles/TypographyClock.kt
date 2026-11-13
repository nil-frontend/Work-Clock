package com.example.workclock.ui.styles

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
import com.example.workclock.model.FontChoice
import java.time.ZonedDateTime

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypographyClockDisplay(
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
        settings.fontChoice.fontResId?.let { resId ->
            FontFamily(Font(resId, FontWeight.Normal))
        } ?: FontFamily.SansSerif
    }

    val timeColor = Color(settings.theme.timeColor)
    val secColor = Color(settings.theme.secColor)
    val accentColor = Color(settings.theme.accentColor)
    val surfaceColor = Color(settings.theme.surfaceColor)

    val interactionSource = remember { MutableInteractionSource() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClockTap
            )
            .testTag("typography_clock_surface"),
        contentAlignment = Alignment.Center
    ) {
        val availW = maxWidth.value
        val availH = maxHeight.value
        val isLandscape = availW > availH

        val hasSeconds = formattedTime.seconds.isNotEmpty()
        val hasAmPm = formattedTime.amPm.isNotEmpty()
        val hasFooter = formattedTime.fullDate.isNotEmpty() || formattedTime.timeZone.isNotEmpty()

        val horizPad = if (isPreview) 12f else if (isLandscape) 48f else 16f
        val bottomPad = if (isPreview) 6f else if (isLandscape) 48f else 56f
        val footerReserve = if (hasFooter) (if (isPreview) 28f else if (isLandscape) 40f else 56f) else 0f

        val usableW = (availW - horizPad * 2).coerceAtLeast(100f)
        val usableH = (availH - bottomPad - footerReserve - 10f).coerceAtLeast(50f)

        val charWidthUnits = 3.3f + (if (hasSeconds) 1.35f else 0f) + (if (hasAmPm) 0.85f else 0f)
        val wideFontFactor = if (settings.fontChoice == FontChoice.ORBITRON) 1.25f else 1.0f

        val maxFontFromW = (usableW / (charWidthUnits * wideFontFactor)).coerceAtLeast(16f)
        val maxFontFromH = (usableH * 0.58f).coerceAtLeast(16f)
        val timeFontSizeVal = minOf(maxFontFromW, maxFontFromH, if (isPreview) 64f else 120f)

        val timeFontSize = timeFontSizeVal.sp
        val secondsFontSize = (timeFontSizeVal * 0.44f).coerceIn(12f, 44f).sp
        val amPmFontSize = (timeFontSizeVal * 0.28f).coerceIn(10f, 26f).sp
        val dateFontSize = (if (isPreview) 11f else (timeFontSizeVal * 0.22f).coerceIn(12f, 20f)).sp

        val talkBackDesc = "Typography Clock " + formattedTime.hoursMinutes

        Column(
            modifier = Modifier
                .offset(x = pixelShiftX.dp, y = pixelShiftY.dp)
                .padding(horizontal = horizPad.dp)
                .padding(bottom = bottomPad.dp)
                .semantics { contentDescription = talkBackDesc }
                .testTag("typography_clock_container"),
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
                label = "typography_hours_minutes"
            ) { hoursMinutes ->
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = hoursMinutes,
                        color = timeColor,
                        fontSize = timeFontSize,
                        fontWeight = FontWeight.Bold,
                        fontFamily = customFontFamily,
                        maxLines = 1,
                        softWrap = false,
                        style = TextStyle(fontFeatureSettings = "tnum")
                    )

                    if (hasSeconds) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = formattedTime.seconds,
                            color = secColor,
                            fontSize = secondsFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            style = TextStyle(fontFeatureSettings = "tnum"),
                            modifier = Modifier.padding(bottom = (timeFontSizeVal * 0.08f).dp)
                        )
                    }

                    if (hasAmPm) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formattedTime.amPm.uppercase(),
                            color = accentColor,
                            fontSize = amPmFontSize,
                            fontWeight = FontWeight.Bold,
                            fontFamily = customFontFamily,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(bottom = (timeFontSizeVal * 0.12f).dp)
                        )
                    }
                }
            }

            if (hasFooter) {
                Spacer(modifier = Modifier.height(if (isPreview) 8.dp else 14.dp))
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
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
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                    if (formattedTime.timeZone.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = surfaceColor,
                            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(
                                    horizontal = if (isPreview) 6.dp else 8.dp,
                                    vertical = if (isPreview) 2.dp else 3.dp
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(if (isPreview) 10.dp else 12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formattedTime.timeZone,
                                    color = timeColor,
                                    fontSize = if (isPreview) 10.sp else 11.sp,
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
        }
    }
}
