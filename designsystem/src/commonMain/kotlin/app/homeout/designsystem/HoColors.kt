package app.homeout.designsystem

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * The design's color tokens (homeout UI Library page), plus the interaction and
 * texture shades its Button Set and Home Screen pages use.
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
    val divider: Color,
    val primaryPressed: Color,
    val secondaryPressed: Color,
    val neutralPressed: Color,
    val stripe: Color,
) {
    companion object {
        val Dark = HoColors(
            background = Color(0xFF0F1817),
            surface = Color(0xFF192422),
            onSurface = Color(0xFFE7ECEB),
            onSurfaceVariant = Color(0xFFB3BDBB),
            outline = Color(0xFF3E4746),
            primary = Color(0xFF70A89F),
            onPrimaryText = Color(0xFFAEE0D8),
            secondaryContainer = Color(0xFF19302D),
            onSecondaryContainer = Color(0xFFC9F2EB),
            brand = Color(0xFF64837E),
            tertiary = Color(0xFFF69DAB),
            divider = Color(0xFFE7ECEB).copy(alpha = 0.16f),
            primaryPressed = Color(0xFF70A89F).copy(alpha = 0.14f),
            secondaryPressed = Color(0xFF284D48),
            neutralPressed = Color(0xFFE7ECEB).copy(alpha = 0.08f),
            stripe = Color(0xFF252D2C),
        )

        val Light = HoColors(
            background = Color(0xFFF3F8F7),
            surface = Color(0xFFE4EDEC),
            onSurface = Color(0xFF0F1817),
            onSurfaceVariant = Color(0xFF596361),
            outline = Color(0xFFCCD7D5),
            primary = Color(0xFF548981),
            onPrimaryText = Color(0xFF3B6A64),
            secondaryContainer = Color(0xFFC9F2EB),
            onSecondaryContainer = Color(0xFF284D48),
            brand = Color(0xFF64837E),
            tertiary = Color(0xFF8E4754),
            divider = Color(0xFF0F1817).copy(alpha = 0.18f),
            primaryPressed = Color(0xFF548981).copy(alpha = 0.12f),
            secondaryPressed = Color(0xFFAEE0D8),
            neutralPressed = Color(0xFF0F1817).copy(alpha = 0.07f),
            stripe = Color(0xFFCCD7D5),
        )
    }
}
