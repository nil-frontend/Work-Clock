package com.example.workclock.ui.appearance

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.workclock.model.AnimationStyle
import com.example.workclock.model.ClockSettings
import com.example.workclock.model.ClockStyle
import com.example.workclock.model.ClockTheme
import com.example.workclock.model.FontChoice
import com.example.workclock.model.TimeFormat
import com.example.workclock.ui.ClockDisplay
import java.time.ZonedDateTime

/**
 * Dedicated Clock Appearance & Customization Screen with Live Interactive Preview.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ClockAppearanceScreen(
    currentSettings: ClockSettings,
    currentTime: ZonedDateTime,
    onApplySettings: (ClockSettings) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    // Transient preview state - updates live preview immediately before applying
    var previewStyle by remember { mutableStateOf(currentSettings.clockStyle) }
    var previewAnimStyle by remember { mutableStateOf(currentSettings.animationStyle) }
    var previewFont by remember { mutableStateOf(currentSettings.fontChoice) }
    var previewTheme by remember { mutableStateOf(currentSettings.theme) }
    var previewShowSeconds by remember { mutableStateOf(currentSettings.showSeconds) }
    var previewShowDate by remember { mutableStateOf(currentSettings.showDate) }
    var previewShowTimeZone by remember { mutableStateOf(currentSettings.showTimeZone) }
    var previewTimeFormat by remember { mutableStateOf(currentSettings.timeFormat) }
    var previewAnimDuration by remember { mutableIntStateOf(currentSettings.animationDurationMs) }
    var previewDigitSpacing by remember { mutableIntStateOf(currentSettings.digitSpacingDp) }
    var previewCardRadius by remember { mutableIntStateOf(currentSettings.cardRadiusDp) }
    var previewBlinkColon by remember { mutableStateOf(currentSettings.blinkColon) }
    var previewLedGlow by remember { mutableStateOf(currentSettings.ledGlowEnabled) }

    // Allows user to tap "Test Anim" to advance preview time by +1 min & trigger digit animations on demand
    var previewOffsetMinutes by remember { mutableLongStateOf(0L) }
    val previewTime = remember(currentTime, previewOffsetMinutes) {
        currentTime.plusMinutes(previewOffsetMinutes)
    }

    val previewSettings = remember(
        previewStyle,
        previewAnimStyle,
        previewFont,
        previewTheme,
        previewShowSeconds,
        previewShowDate,
        previewShowTimeZone,
        previewTimeFormat,
        previewAnimDuration,
        previewDigitSpacing,
        previewCardRadius,
        previewBlinkColon,
        previewLedGlow
    ) {
        currentSettings.copy(
            clockStyle = previewStyle,
            animationStyle = previewAnimStyle,
            fontChoice = previewFont,
            theme = previewTheme,
            showSeconds = previewShowSeconds,
            showDate = previewShowDate,
            showTimeZone = previewShowTimeZone,
            timeFormat = previewTimeFormat,
            animationDurationMs = previewAnimDuration,
            digitSpacingDp = previewDigitSpacing,
            cardRadiusDp = previewCardRadius,
            blinkColon = previewBlinkColon,
            ledGlowEnabled = previewLedGlow
        )
    }

    val themeAccent = Color(previewTheme.accentColor)
    val onAccentColor = if (previewTheme.isLight) Color.White else Color.Black
    val themeBg = Color(previewTheme.bg)
    val themeSurface = Color(previewTheme.surfaceColor)
    val themeText = Color(previewTheme.timeColor)
    val themeSec = Color(previewTheme.secColor)
    val cardBorderColor = if (previewTheme.isLight) Color(0xFFCBC7BD) else Color(0xFF2E323D)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Clock Appearance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = themeText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("appearance_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = themeText
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            onApplySettings(previewSettings)
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeAccent,
                            contentColor = onAccentColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("appearance_apply_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Apply", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = themeSurface
                )
            )
        },
        containerColor = themeBg,
        modifier = modifier.testTag("clock_appearance_screen")
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isLandscape = maxWidth > maxHeight
            val portraitPreviewHeight = if (maxHeight < 650.dp) 168.dp else 192.dp

            val settingsScrollContent: @Composable () -> Unit = {
                // =====================================================================
                // 2. Clock Visual Styles (6 Styles)
                // =====================================================================
                AppearanceSectionHeader(
                    title = "Clock Visual Style",
                    icon = Icons.Default.Layers,
                    accentColor = themeAccent,
                    textColor = themeText
                )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ClockStyle.entries.forEach { style ->
                    val isSelected = previewStyle == style
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) themeAccent.copy(alpha = 0.14f) else themeSurface,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) themeAccent else cardBorderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { previewStyle = style }
                            .testTag("style_item_${style.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (isSelected) themeAccent else themeSec,
                                        CircleShape
                                    )
                                    .background(if (isSelected) themeAccent else Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = onAccentColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = style.displayName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) themeAccent else themeText
                                )
                                Text(
                                    text = style.description,
                                    fontSize = 12.sp,
                                    color = themeSec
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 3. Animation Engine
            // =========================================================================
            AppearanceSectionHeader(
                title = "Clock Animation Style",
                icon = Icons.Default.Animation,
                accentColor = themeAccent,
                textColor = themeText
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = themeSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimationStyle.entries.forEach { anim ->
                            val selected = previewAnimStyle == anim
                            FilterChip(
                                selected = selected,
                                onClick = { previewAnimStyle = anim },
                                label = { Text(anim.displayName, fontSize = 12.sp) },
                                leadingIcon = {
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeAccent.copy(alpha = 0.22f),
                                    selectedLabelColor = themeAccent,
                                    labelColor = themeText
                                ),
                                modifier = Modifier.testTag("anim_chip_${anim.id}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Transition Speed: ${previewAnimDuration}ms",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeText
                    )
                    Slider(
                        value = previewAnimDuration.toFloat(),
                        onValueChange = { previewAnimDuration = it.toInt() },
                        valueRange = 150f..800f,
                        steps = 12,
                        colors = SliderDefaults.colors(
                            thumbColor = themeAccent,
                            activeTrackColor = themeAccent,
                            inactiveTrackColor = themeSec.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("anim_duration_slider")
                    )
                }
            }

            // =========================================================================
            // 4. Bundled Fonts Library
            // =========================================================================
            AppearanceSectionHeader(
                title = "Font Family & Weight",
                icon = Icons.Default.FontDownload,
                accentColor = themeAccent,
                textColor = themeText
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FontChoice.entries.forEach { fontChoice ->
                    val isSelected = previewFont == fontChoice
                    val customFamily = remember(fontChoice) {
                        fontChoice.fontResId?.let { FontFamily(Font(it)) } ?: FontFamily.SansSerif
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) themeAccent.copy(alpha = 0.12f) else themeSurface,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) themeAccent else cardBorderColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { previewFont = fontChoice }
                            .testTag("font_item_${fontChoice.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = fontChoice.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) themeAccent else themeText
                                )
                                Text(
                                    text = "0123456789",
                                    fontSize = 16.sp,
                                    fontFamily = customFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = themeSec
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = themeAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 5. Colour Themes (8 Preset Themes - Responsive 2-column grid, never overflows!)
            // =========================================================================
            AppearanceSectionHeader(
                title = "Colour Themes",
                icon = Icons.Default.ColorLens,
                accentColor = themeAccent,
                textColor = themeText
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ClockTheme.entries.chunked(2).forEach { rowThemes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowThemes.forEach { themeItem ->
                            val isSelected = previewTheme == themeItem
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(themeItem.surfaceColor),
                                border = BorderStroke(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) themeAccent else cardBorderColor
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { previewTheme = themeItem }
                                    .testTag("theme_card_${themeItem.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = themeItem.displayName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(themeItem.timeColor),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color(themeItem.accentColor),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color(themeItem.bg))
                                                .border(1.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color(themeItem.timeColor))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color(themeItem.accentColor))
                                        )
                                    }
                                }
                            }
                        }
                        if (rowThemes.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // =========================================================================
            // 6. Display Preferences & Fine-Tuning
            // =========================================================================
            AppearanceSectionHeader(
                title = "Display Preferences & Fine-Tuning",
                icon = Icons.Default.Tune,
                accentColor = themeAccent,
                textColor = themeText
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = themeSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppearanceToggleRow(
                        title = "Show Seconds",
                        subtitle = "Display real-time seconds group",
                        checked = previewShowSeconds,
                        onCheckedChange = { previewShowSeconds = it },
                        accentColor = themeAccent,
                        onAccentColor = onAccentColor,
                        textColor = themeText,
                        subTextColor = themeSec,
                        testTag = "pref_toggle_seconds"
                    )

                    HorizontalDivider(color = cardBorderColor)

                    AppearanceToggleRow(
                        title = "Show Date",
                        subtitle = "Display full date and weekday",
                        checked = previewShowDate,
                        onCheckedChange = { previewShowDate = it },
                        accentColor = themeAccent,
                        onAccentColor = onAccentColor,
                        textColor = themeText,
                        subTextColor = themeSec,
                        testTag = "pref_toggle_date"
                    )

                    HorizontalDivider(color = cardBorderColor)

                    AppearanceToggleRow(
                        title = "Show Time Zone Badge",
                        subtitle = "Display current time zone identifier",
                        checked = previewShowTimeZone,
                        onCheckedChange = { previewShowTimeZone = it },
                        accentColor = themeAccent,
                        onAccentColor = onAccentColor,
                        textColor = themeText,
                        subTextColor = themeSec,
                        testTag = "pref_toggle_timezone"
                    )

                    HorizontalDivider(color = cardBorderColor)

                    AppearanceToggleRow(
                        title = "Blink Colon",
                        subtitle = "Blink separator colon every second (LED/LCD/Digital)",
                        checked = previewBlinkColon,
                        onCheckedChange = { previewBlinkColon = it },
                        accentColor = themeAccent,
                        onAccentColor = onAccentColor,
                        textColor = themeText,
                        subTextColor = themeSec,
                        testTag = "pref_toggle_blink_colon"
                    )

                    HorizontalDivider(color = cardBorderColor)

                    AppearanceToggleRow(
                        title = "LED Segment Glow",
                        subtitle = "Add subtle ambient glow on Seven-Segment LED digits",
                        checked = previewLedGlow,
                        onCheckedChange = { previewLedGlow = it },
                        accentColor = themeAccent,
                        onAccentColor = onAccentColor,
                        textColor = themeText,
                        subTextColor = themeSec,
                        testTag = "pref_toggle_led_glow"
                    )

                    HorizontalDivider(color = cardBorderColor)

                    // Time Format (FlowRow prevents overflow on portrait screens!)
                    Text(
                        text = "Time Format",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeText
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TimeFormat.entries.forEach { format ->
                            val isSelected = previewTimeFormat == format
                            FilterChip(
                                selected = isSelected,
                                onClick = { previewTimeFormat = format },
                                label = { Text(format.label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = themeAccent.copy(alpha = 0.22f),
                                    selectedLabelColor = themeAccent,
                                    labelColor = themeText
                                ),
                                modifier = Modifier.testTag("time_format_${format.name.lowercase()}")
                            )
                        }
                    }

                    HorizontalDivider(color = cardBorderColor)

                    Text(
                        text = "Digit Spacing: ${previewDigitSpacing}dp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeText
                    )
                    Slider(
                        value = previewDigitSpacing.toFloat(),
                        onValueChange = { previewDigitSpacing = it.toInt() },
                        valueRange = 2f..24f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = themeAccent,
                            activeTrackColor = themeAccent,
                            inactiveTrackColor = themeSec.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("spacing_slider")
                    )

                    HorizontalDivider(color = cardBorderColor)

                    Text(
                        text = "Card Corner Radius: ${previewCardRadius}dp",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeText
                    )
                    Slider(
                        value = previewCardRadius.toFloat(),
                        onValueChange = { previewCardRadius = it.toInt() },
                        valueRange = 4f..28f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = themeAccent,
                            activeTrackColor = themeAccent,
                            inactiveTrackColor = themeSec.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("radius_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onApplySettings(previewSettings)
                    onNavigateBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeAccent,
                    contentColor = onAccentColor
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("bottom_apply_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply & Save Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LivePreviewCard(
                        previewTime = previewTime,
                        previewSettings = previewSettings,
                        themeAccent = themeAccent,
                        themeSurface = themeSurface,
                        themeBg = themeBg,
                        cardBorderColor = cardBorderColor,
                        onTestAnim = { previewOffsetMinutes += 1L },
                        fillPreviewHeight = true,
                        modifier = Modifier
                            .weight(0.45f)
                            .fillMaxHeight()
                    )

                    Column(
                        modifier = Modifier
                            .weight(0.55f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(end = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        settingsScrollContent()
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(themeBg)
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        LivePreviewCard(
                            previewTime = previewTime,
                            previewSettings = previewSettings,
                            themeAccent = themeAccent,
                            themeSurface = themeSurface,
                            themeBg = themeBg,
                            cardBorderColor = cardBorderColor,
                            onTestAnim = { previewOffsetMinutes += 1L },
                            fillPreviewHeight = false,
                            portraitPreviewHeight = portraitPreviewHeight,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = cardBorderColor.copy(alpha = 0.7f)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        settingsScrollContent()
                    }
                }
            }
        }
    }
}

@Composable
private fun LivePreviewCard(
    previewTime: ZonedDateTime,
    previewSettings: ClockSettings,
    themeAccent: Color,
    themeSurface: Color,
    themeBg: Color,
    cardBorderColor: Color,
    onTestAnim: () -> Unit,
    fillPreviewHeight: Boolean,
    portraitPreviewHeight: androidx.compose.ui.unit.Dp = 192.dp,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = themeSurface),
        border = BorderStroke(1.5.dp, themeAccent.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier.testTag("live_preview_card")
    ) {
        Column(
            modifier = (if (fillPreviewHeight) Modifier.fillMaxSize() else Modifier.fillMaxWidth())
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Preview,
                        contentDescription = null,
                        tint = themeAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Preview",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = themeAccent,
                        maxLines = 1
                    )
                }

                TextButton(
                    onClick = onTestAnim,
                    modifier = Modifier.testTag("preview_test_anim_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Test clock animation",
                        tint = themeAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Test Anim",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            val previewBoxModifier = if (fillPreviewHeight) {
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(portraitPreviewHeight)
            }

            Box(
                modifier = previewBoxModifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(themeBg)
                    .border(BorderStroke(1.dp, cardBorderColor), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                ClockDisplay(
                    currentTime = previewTime,
                    settings = previewSettings,
                    pixelShiftX = 0f,
                    pixelShiftY = 0f,
                    onClockTap = onTestAnim,
                    isPreview = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun AppearanceSectionHeader(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun AppearanceToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
    onAccentColor: Color,
    textColor: Color,
    subTextColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = subTextColor
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = onAccentColor,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = subTextColor,
                uncheckedTrackColor = subTextColor.copy(alpha = 0.25f)
            )
        )
    }
}
