package com.example.workclock.ui.flip

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.model.AnimationStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.FontChoice
import kotlin.math.PI
import kotlin.math.sin

/**
 * Authentic mechanical split-flap flip clock card (Fliqlo style).
 */
@Composable
fun FlipCardTile(
    currentValue: String,
    cardWidth: Dp,
    cardHeight: Dp,
    fontSize: Float,
    theme: ClockTheme,
    modifier: Modifier = Modifier,
    amPm: String = "",
    animDurationMs: Int = 350,
    cornerRadius: Dp? = null,
    fontChoice: FontChoice = FontChoice.SYSTEM_DEFAULT,
    animationStyle: AnimationStyle = AnimationStyle.REALISTIC_FLIP,
    testTag: String = "flip_card_tile"
) {
    var displayedOldValue by remember { mutableStateOf(currentValue) }
    var displayedNewValue by remember { mutableStateOf(currentValue) }
    var isFlipping by remember { mutableStateOf(false) }

    val flipAnimation = remember { Animatable(0f) }

    LaunchedEffect(currentValue, animationStyle) {
        if (currentValue != displayedNewValue) {
            if (animationStyle == AnimationStyle.INSTANT || animDurationMs <= 0) {
                displayedOldValue = currentValue
                displayedNewValue = currentValue
                isFlipping = false
                flipAnimation.snapTo(0f)
                return@LaunchedEffect
            }
            val previousTarget = displayedNewValue
            displayedOldValue = previousTarget
            displayedNewValue = currentValue
            isFlipping = true
            flipAnimation.snapTo(0f)
            try {
                flipAnimation.animateTo(
                    targetValue = 180f,
                    animationSpec = tween(
                        durationMillis = animDurationMs,
                        easing = FastOutSlowInEasing
                    )
                )
            } finally {
                displayedOldValue = currentValue
                displayedNewValue = currentValue
                isFlipping = false
                flipAnimation.snapTo(0f)
            }
        }
    }

    val halfHeight = cardHeight / 2
    val actualCornerRadius = cornerRadius ?: (cardHeight.value * 0.08f).coerceIn(8f, 20f).dp

    val isLight = theme.isLight
    val cardGradient = remember(isLight) {
        if (isLight) {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFEBE8E0)
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF27292F),
                    Color(0xFF1D1E22)
                )
            )
        }
    }
    val cardBorder = remember(isLight) {
        BorderStroke(1.dp, if (isLight) Color(0xFFD0CCC2) else Color(0xFF383B44))
    }
    val creaseColor = if (isLight) Color(0xFFC8C3B8) else Color(0xFF0C0D10)
    val creaseHighlightColor = if (isLight) Color(0xFFFFFFFF).copy(alpha = 0.6f) else Color(0xFF3A3D46)
    val notchColor = if (isLight) Color(0xFFB5B0A4) else Color(0xFF090A0D)
    val badgeBgColor = if (isLight) Color(0xFFE2E0D8).copy(alpha = 0.95f) else Color(0xFF141518).copy(alpha = 0.9f)
    val badgeBorderColor = if (isLight) Color(0xFFBDB8AC) else Color(0xFF383A42)

    val textColor = Color(theme.timeColor)
    val accentColor = Color(theme.accentColor)
    val customFontFamily = remember(fontChoice) {
        fontChoice.fontResId?.let { FontFamily(Font(it, FontWeight.Normal)) } ?: FontFamily.SansSerif
    }

    val density = LocalDensity.current

    Box(
        modifier = modifier
            .size(cardWidth, cardHeight)
            .shadow(
                elevation = if (isLight) 6.dp else 10.dp,
                shape = RoundedCornerShape(actualCornerRadius),
                ambientColor = Color.Black,
                spotColor = Color.Black
            )
            .testTag(testTag)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CardHalf(
                text = displayedNewValue,
                isTop = true,
                cardWidth = cardWidth,
                halfHeight = halfHeight,
                fontSize = fontSize,
                textColor = textColor,
                fontFamily = customFontFamily,
                gradient = cardGradient,
                border = cardBorder,
                cornerRadius = actualCornerRadius,
                badgeBgColor = badgeBgColor,
                badgeBorderColor = badgeBorderColor
            )

            Box(modifier = Modifier.size(cardWidth, halfHeight)) {
                CardHalf(
                    text = displayedOldValue,
                    isTop = false,
                    cardWidth = cardWidth,
                    halfHeight = halfHeight,
                    fontSize = fontSize,
                    textColor = textColor,
                    fontFamily = customFontFamily,
                    gradient = cardGradient,
                    border = cardBorder,
                    cornerRadius = actualCornerRadius,
                    amPm = amPm,
                    accentColor = accentColor,
                    badgeBgColor = badgeBgColor,
                    badgeBorderColor = badgeBorderColor,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (flipAnimation.value < 90f) 1f else 0f
                    }
                )

                CardHalf(
                    text = displayedNewValue,
                    isTop = false,
                    cardWidth = cardWidth,
                    halfHeight = halfHeight,
                    fontSize = fontSize,
                    textColor = textColor,
                    fontFamily = customFontFamily,
                    gradient = cardGradient,
                    border = cardBorder,
                    cornerRadius = actualCornerRadius,
                    amPm = amPm,
                    accentColor = accentColor,
                    badgeBgColor = badgeBgColor,
                    badgeBorderColor = badgeBorderColor,
                    modifier = Modifier.graphicsLayer {
                        alpha = if (flipAnimation.value >= 90f) 1f else 0f
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .size(cardWidth, halfHeight)
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    val rot = flipAnimation.value
                    rotationX = -rot
                    alpha = if (isFlipping && rot in 0.01f..89.99f) 1f else 0f
                    transformOrigin = TransformOrigin(0.5f, 1.0f)
                    cameraDistance = 18f * density.density
                }
        ) {
            CardHalf(
                text = displayedOldValue,
                isTop = true,
                cardWidth = cardWidth,
                halfHeight = halfHeight,
                fontSize = fontSize,
                textColor = textColor,
                fontFamily = customFontFamily,
                gradient = cardGradient,
                border = cardBorder,
                cornerRadius = actualCornerRadius,
                badgeBgColor = badgeBgColor,
                badgeBorderColor = badgeBorderColor
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val rot = flipAnimation.value
                        val rad = (rot * PI / 180f).toFloat()
                        alpha = if (isFlipping && rot in 0.01f..89.99f) (sin(rad) * 0.35f).coerceIn(0f, 0.5f) else 0f
                    }
                    .background(Color.Black)
            )
        }

        Box(
            modifier = Modifier
                .size(cardWidth, halfHeight)
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    val rot = flipAnimation.value
                    rotationX = 180f - rot
                    alpha = if (isFlipping && rot in 90f..180f) 1f else 0f
                    transformOrigin = TransformOrigin(0.5f, 0.0f)
                    cameraDistance = 18f * density.density
                }
        ) {
            CardHalf(
                text = displayedNewValue,
                isTop = false,
                cardWidth = cardWidth,
                halfHeight = halfHeight,
                fontSize = fontSize,
                textColor = textColor,
                fontFamily = customFontFamily,
                gradient = cardGradient,
                border = cardBorder,
                cornerRadius = actualCornerRadius,
                amPm = amPm,
                accentColor = accentColor,
                badgeBgColor = badgeBgColor,
                badgeBorderColor = badgeBorderColor
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val rot = flipAnimation.value
                        val rad = (rot * PI / 180f).toFloat()
                        alpha = if (isFlipping && rot in 90f..180f) (sin(rad) * 0.35f).coerceIn(0f, 0.5f) else 0f
                    }
                    .background(Color.Black)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.Center)
                .background(creaseColor)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .offset(y = 1.dp)
                .align(Alignment.Center)
                .background(creaseHighlightColor)
        )

        val notchW = (cardWidth.value * 0.04f).coerceIn(4f, 7f).dp
        val notchH = (cardHeight.value * 0.07f).coerceIn(7f, 14f).dp
        Box(
            modifier = Modifier
                .size(notchW, notchH)
                .align(Alignment.CenterStart)
                .offset(x = -(notchW / 2))
                .clip(CircleShape)
                .background(notchColor)
        )
        Box(
            modifier = Modifier
                .size(notchW, notchH)
                .align(Alignment.CenterEnd)
                .offset(x = (notchW / 2))
                .clip(CircleShape)
                .background(notchColor)
        )
    }
}

@Composable
private fun CardHalf(
    text: String,
    isTop: Boolean,
    cardWidth: Dp,
    halfHeight: Dp,
    fontSize: Float,
    textColor: Color,
    fontFamily: FontFamily,
    gradient: Brush,
    border: BorderStroke,
    cornerRadius: Dp,
    badgeBgColor: Color,
    badgeBorderColor: Color,
    modifier: Modifier = Modifier,
    amPm: String = "",
    accentColor: Color = Color.White
) {
    val shape = remember(isTop, cornerRadius) {
        if (isTop) {
            RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
        } else {
            RoundedCornerShape(bottomStart = cornerRadius, bottomEnd = cornerRadius)
        }
    }

    val textMeasurer = rememberTextMeasurer()

    val textLayoutResult = remember(text, fontSize, textColor, fontFamily) {
        textMeasurer.measure(
            text = text,
            style = TextStyle(
                color = textColor,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Black,
                fontFamily = fontFamily,
                fontFeatureSettings = "tnum"
            )
        )
    }

    Box(
        modifier = modifier
            .size(cardWidth, halfHeight)
            .clip(shape)
            .background(gradient)
            .border(border, shape)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val textX = (size.width - textLayoutResult.size.width) / 2f
            val textCenterY = if (isTop) size.height else 0f
            val textY = textCenterY - (textLayoutResult.size.height / 2f)

            clipRect(0f, 0f, size.width, size.height) {
                drawText(textLayoutResult, topLeft = Offset(textX, textY))
            }
        }

        if (!isTop && amPm.isNotEmpty()) {
            val padStart = (cardWidth.value * 0.06f).coerceIn(4f, 10f).dp
            val padBottom = (halfHeight.value * 0.08f).coerceIn(3f, 8f).dp
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = badgeBgColor,
                border = BorderStroke(0.5.dp, badgeBorderColor),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = padStart, bottom = padBottom)
            ) {
                Text(
                    text = amPm.uppercase(),
                    color = accentColor,
                    fontSize = (fontSize * 0.16f).coerceIn(8f, 18f).sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }
    }
}
