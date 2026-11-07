package app.homeout.designsystem

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/** WCAG 2.1 contrast: text needs 4.5:1; large text and UI borders 3:1. */
class HoContrastTest {

    @Test
    fun darkThemeMeetsAA() = assertContrast(HoColors.Dark, "dark")

    @Test
    fun lightThemeMeetsAA() = assertContrast(HoColors.Light, "light")

    private fun assertContrast(c: HoColors, theme: String) {
        val text = mapOf(
            "onSurface" to c.onSurface,
            "onSurfaceVariant" to c.onSurfaceVariant,
            "onPrimaryText" to c.onPrimaryText,
            "tertiary" to c.tertiary,
        )
        val failures = buildList {
            text.forEach { (name, color) ->
                check(name, color, "background", c.background, 4.5)
                check(name, color, "surface", c.surface, 4.5)
            }
            check("onSecondaryContainer", c.onSecondaryContainer, "secondaryContainer", c.secondaryContainer, 4.5)
            check("onSecondaryContainer", c.onSecondaryContainer, "secondaryPressed", c.secondaryPressed, 4.5)
            check("onTertiary", c.onTertiary, "tertiary", c.tertiary, 4.5)
            check("primary", c.primary, "background", c.background, 3.0)
            // The wordmark is 24sp at weight 800, large text under WCAG, so 3:1 applies.
            check("wordmark letters", HoColors.Dark.onSurface, "brand", c.brand, 3.0)
        }
        assertTrue(failures.isEmpty(), "$theme theme below WCAG AA: $failures")
    }

    private fun MutableList<String>.check(fg: String, a: Color, bg: String, b: Color, minimum: Double) {
        val ratio = ratio(a, b)
        if (ratio < minimum) add("$fg on $bg = ${(ratio * 100).toInt() / 100.0} (needs $minimum)")
    }

    private fun ratio(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun luminance(c: Color): Double {
        fun channel(v: Float): Double = if (v <= 0.04045f) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
    }
}
