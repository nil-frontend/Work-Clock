package com.example.workclock.model

import com.example.R
import java.time.ZonedDateTime

enum class TimeFormat(val label: String) {
    SYSTEM("System Default"),
    HOUR_12("12-Hour (AM/PM)"),
    HOUR_24("24-Hour")
}

enum class BrightnessMode(val label: String) {
    SYSTEM("Follow System"),
    CUSTOM("App Override")
}

enum class ClockTheme(
    val id: String,
    val displayName: String,
    val bg: Long,
    val timeColor: Long,
    val secColor: Long,
    val accentColor: Long,
    val surfaceColor: Long,
    val isLight: Boolean = false
) {
    MIDNIGHT(
        id = "midnight",
        displayName = "Midnight",
        bg = 0xFF080B12,
        timeColor = 0xFFF5F7FA,
        secColor = 0xFF8D9AA8,
        accentColor = 0xFF79B8FF,
        surfaceColor = 0xFF161B26
    ),
    OLED_BLACK(
        id = "oled_black",
        displayName = "OLED Black",
        bg = 0xFF000000,
        timeColor = 0xFFFFFFFF,
        secColor = 0xFFAAAAAA,
        accentColor = 0xFF60A5FA,
        surfaceColor = 0xFF18181A
    ),
    AMBER_WARM(
        id = "amber_warm",
        displayName = "Amber Warm",
        bg = 0xFF171006,
        timeColor = 0xFFFFE0A3,
        secColor = 0xFFC8AD7B,
        accentColor = 0xFFFFA726,
        surfaceColor = 0xFF251C10
    ),
    FOREST_GREEN(
        id = "forest_green",
        displayName = "Forest Green",
        bg = 0xFF07120D,
        timeColor = 0xFFD9FBE8,
        secColor = 0xFF9AB9A8,
        accentColor = 0xFF81C784,
        surfaceColor = 0xFF122018
    ),
    NAVY_BLUE(
        id = "navy_blue",
        displayName = "Navy Blue",
        bg = 0xFF07111F,
        timeColor = 0xFFD8E8FF,
        secColor = 0xFF9BB4D1,
        accentColor = 0xFF64B5F6,
        surfaceColor = 0xFF101C2E
    ),
    MONOCHROME(
        id = "monochrome",
        displayName = "Monochrome",
        bg = 0xFF171717,
        timeColor = 0xFFF5F5F5,
        secColor = 0xFFBDBDBD,
        accentColor = 0xFFE0E0E0,
        surfaceColor = 0xFF282828
    ),
    VINTAGE_BROWN(
        id = "vintage_brown",
        displayName = "Vintage Brown",
        bg = 0xFF24100C,
        timeColor = 0xFFF4C9A6,
        secColor = 0xFFD59D79,
        accentColor = 0xFFE69F73,
        surfaceColor = 0xFF381F1A
    ),
    PAPER_LIGHT(
        id = "paper_light",
        displayName = "Paper Light",
        bg = 0xFFF1F0EB,
        timeColor = 0xFF171717,
        secColor = 0xFF666666,
        accentColor = 0xFF2C2C2C,
        surfaceColor = 0xFFE2E0D8,
        isLight = true
    );

    companion object {
        // Aliases for backwards compatibility with earlier versions and tests
        val AMBER = AMBER_WARM
        val FOREST = FOREST_GREEN
        val NAVY = NAVY_BLUE

        fun fromId(id: String): ClockTheme = when (id.lowercase()) {
            "midnight" -> MIDNIGHT
            "oled_black", "oled" -> OLED_BLACK
            "amber_warm", "amber" -> AMBER_WARM
            "forest_green", "forest" -> FOREST_GREEN
            "navy_blue", "navy" -> NAVY_BLUE
            "monochrome" -> MONOCHROME
            "vintage_brown", "vintage" -> VINTAGE_BROWN
            "paper_light", "paper" -> PAPER_LIGHT
            else -> MIDNIGHT
        }
    }
}

enum class ClockStyle(
    val id: String,
    val displayName: String,
    val description: String
) {
    SPLIT_FLAP("split_flap", "Classic Split-Flap", "Mechanical flap cards with 3D folding"),
    MINIMAL_DIGITAL("minimal_digital", "Minimal Digital", "Clean bold digital typography"),
    LED_SEVEN_SEGMENT("led_seven_segment", "LED Seven-Segment", "Classic electronic digital clock with glow"),
    LCD("lcd", "Classic LCD", "Retro grey-green liquid crystal display"),
    TYPOGRAPHY("typography", "Typography", "Curated font styling with adjustable weight"),
    ODOMETER("odometer", "Rolling Odometer", "Mechanical counter rolling tumblers");

    companion object {
        // Aliases for backward compatibility
        val FLIP_CLOCK = SPLIT_FLAP
        val DIGITAL_MINIMAL = MINIMAL_DIGITAL

        fun fromId(id: String): ClockStyle = when (id.lowercase()) {
            "split_flap", "flip_clock", "flip" -> SPLIT_FLAP
            "minimal_digital", "digital_minimal", "digital" -> MINIMAL_DIGITAL
            "led_seven_segment", "led" -> LED_SEVEN_SEGMENT
            "lcd" -> LCD
            "typography" -> TYPOGRAPHY
            "odometer" -> ODOMETER
            else -> SPLIT_FLAP
        }
    }
}

enum class AnimationStyle(
    val id: String,
    val displayName: String,
    val description: String
) {
    REALISTIC_FLIP("realistic_flip", "Realistic Flip", "Physical 350ms split-flap gravity fold"),
    VERTICAL_ROLL("vertical_roll", "Vertical Roll", "Continuous rolling mechanical counter"),
    FADE("fade", "Cross Fade", "Smooth alpha transition between digits"),
    SLIDE("slide", "Vertical Slide", "Snappy vertical slide transition"),
    INSTANT("instant", "Instant (No Motion)", "Immediate zero-delay tick");

    companion object {
        fun fromId(id: String): AnimationStyle =
            entries.firstOrNull { it.id == id.lowercase() } ?: REALISTIC_FLIP
    }
}

enum class FontChoice(
    val id: String,
    val displayName: String,
    val fontResId: Int?
) {
    ROBOTO_MONO("roboto_mono", "Roboto Mono", R.font.roboto_mono),
    JETBRAINS_MONO("jetbrains_mono", "JetBrains Mono", R.font.jetbrains_mono),
    ORBITRON("orbitron", "Orbitron", R.font.orbitron),
    IBM_PLEX_MONO("ibm_plex_mono", "IBM Plex Mono", R.font.ibm_plex_mono),
    INTER("inter", "Inter", R.font.inter),
    SHARE_TECH_MONO("share_tech_mono", "Share Tech Mono", R.font.share_tech_mono),
    SYSTEM_DEFAULT("system_default", "System Default", null);

    companion object {
        fun fromId(id: String): FontChoice =
            entries.firstOrNull { it.id == id.lowercase() } ?: ROBOTO_MONO
    }
}

data class ClockSettings(
    val clockStyle: ClockStyle = ClockStyle.SPLIT_FLAP,
    val animationStyle: AnimationStyle = AnimationStyle.REALISTIC_FLIP,
    val fontChoice: FontChoice = FontChoice.ROBOTO_MONO,
    val timeFormat: TimeFormat = TimeFormat.SYSTEM,
    val showSeconds: Boolean = true,
    val showDate: Boolean = true,
    val showTimeZone: Boolean = true,
    val keepScreenAwake: Boolean = true,
    val theme: ClockTheme = ClockTheme.MIDNIGHT,
    val brightnessMode: BrightnessMode = BrightnessMode.SYSTEM,
    val brightnessLevel: Float = 0.7f,
    val animationDurationMs: Int = 350,
    val digitSpacingDp: Int = 8,
    val cardRadiusDp: Int = 16,
    val blinkColon: Boolean = true,
    val ledGlowEnabled: Boolean = true,
    val immersiveMode: Boolean = false,
    val oledCareEnabled: Boolean = false
)

data class ClockUiState(
    val currentTime: ZonedDateTime = ZonedDateTime.now(),
    val settings: ClockSettings = ClockSettings(),
    val controlsVisible: Boolean = false,
    val pixelShiftX: Float = 0f,
    val pixelShiftY: Float = 0f
)
