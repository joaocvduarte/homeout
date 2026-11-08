package app.homeout.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The design's color tokens (homeout UI Library page): logo-teal backgrounds, a pale
 * tint in light and a deep teal in dark, plus the interaction and texture shades the
 * Button Set and Home Screen pages use.
 */
@Immutable
data class HoColors(
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val primary: Color,
    val onPrimaryText: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val brand: Color,
    val tertiary: Color,
    /** Text on [tertiary], such as the Tickets tab badge. */
    val onTertiary: Color,
    val divider: Color,
    val primaryPressed: Color,
    val secondaryPressed: Color,
    val neutralPressed: Color,
    /** Translucent fill for buttons floating over imagery (the event page's Back and Share). */
    val glass: Color,
    /** Opacity of decorative imagery such as the flower border. */
    val decorationAlpha: Float,
    /** Error text, borders and icons, on the background, a field or [errorContainer]. */
    val error: Color,
    /** The translucent fill behind an error alert. */
    val errorContainer: Color,
    /** The fill of a text field. */
    val field: Color,
    val fieldBorder: Color,
    /** A surface-colored control under the pointer. */
    val surfaceHover: Color,
) {
    companion object {
        val Dark = HoColors(
            background = Color(0xFF283432),
            surface = Color(0xFF334340),
            onSurface = Color(0xFFE7ECEB),
            onSurfaceVariant = Color(0xFFB3BDBB),
            outline = Color(0xFF4F6864),
            primary = Color(0xFF70A89F),
            onPrimaryText = Color(0xFFAEE0D8),
            secondaryContainer = Color(0xFF3E514E),
            onSecondaryContainer = Color(0xFFC9F2EB),
            brand = Color(0xFF64837E),
            tertiary = Color(0xFFF69DAB),
            onTertiary = Color(0xFF283432),
            divider = Color(0xFFE7ECEB).copy(alpha = 0.16f),
            primaryPressed = Color(0xFF70A89F).copy(alpha = 0.14f),
            secondaryPressed = Color(0xFF4B625E),
            neutralPressed = Color(0xFFE7ECEB).copy(alpha = 0.08f),
            glass = Color(0xFF283432).copy(alpha = 0.72f),
            decorationAlpha = 0.92f,
            error = Color(0xFFFFB3B8),
            errorContainer = Color(0xFFFFB3B8).copy(alpha = 0.14f),
            field = Color(0xFF334340).copy(alpha = 0.85f),
            fieldBorder = Color(0xFF6F8A86),
            surfaceHover = Color(0xFF3C4E4B),
        )

        val Light = HoColors(
            background = Color(0xFFE0EBE9),
            surface = Color(0xFFD1E0DE),
            onSurface = Color(0xFF0F1817),
            onSurfaceVariant = Color(0xFF4A5654),
            outline = Color(0xFFB4CBC7),
            primary = Color(0xFF4F817A),
            onPrimaryText = Color(0xFF335C57),
            secondaryContainer = Color(0xFFC9F2EB),
            onSecondaryContainer = Color(0xFF284D48),
            brand = Color(0xFF64837E),
            tertiary = Color(0xFF8E4754),
            onTertiary = Color(0xFFFFFFFF),
            divider = Color(0xFF0F1817).copy(alpha = 0.18f),
            primaryPressed = Color(0xFF4F817A).copy(alpha = 0.12f),
            secondaryPressed = Color(0xFFAEE0D8),
            neutralPressed = Color(0xFF0F1817).copy(alpha = 0.07f),
            glass = Color(0xFFE0EBE9).copy(alpha = 0.8f),
            decorationAlpha = 1f,
            error = Color(0xFFA12D3A),
            errorContainer = Color(0xFFA12D3A).copy(alpha = 0.10f),
            field = Color(0xFFFFFFFF).copy(alpha = 0.55f),
            fieldBorder = Color(0xFF7D9894),
            surfaceHover = Color(0xFFC4D6D3),
        )
    }
}
