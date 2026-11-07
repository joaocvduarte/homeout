package app.homeout.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.test.Test
import kotlin.test.assertEquals

class HoColorsTest {

    private fun HoColors.tokens() = mapOf(
        "background" to background,
        "surface" to surface,
        "onSurface" to onSurface,
        "onSurfaceVariant" to onSurfaceVariant,
        "outline" to outline,
        "primary" to primary,
        "onPrimaryText" to onPrimaryText,
        "secondaryContainer" to secondaryContainer,
        "onSecondaryContainer" to onSecondaryContainer,
        "brand" to brand,
        "tertiary" to tertiary,
    ).mapValues { (_, color) -> color.hex() }

    @Test
    fun darkTokensMatchTheDesign() {
        assertEquals(
            mapOf(
                "background" to "#283432",
                "surface" to "#334340",
                "onSurface" to "#e7eceb",
                "onSurfaceVariant" to "#b3bdbb",
                "outline" to "#4f6864",
                "primary" to "#70a89f",
                "onPrimaryText" to "#aee0d8",
                "secondaryContainer" to "#3e514e",
                "onSecondaryContainer" to "#c9f2eb",
                "brand" to "#64837e",
                "tertiary" to "#f69dab",
            ),
            HoColors.Dark.tokens(),
        )
    }

    @Test
    fun lightTokensMatchTheDesign() {
        assertEquals(
            mapOf(
                "background" to "#e0ebe9",
                "surface" to "#d1e0de",
                "onSurface" to "#0f1817",
                "onSurfaceVariant" to "#4a5654",
                "outline" to "#b4cbc7",
                "primary" to "#4f817a",
                "onPrimaryText" to "#335c57",
                "secondaryContainer" to "#c9f2eb",
                "onSecondaryContainer" to "#284d48",
                "brand" to "#64837e",
                "tertiary" to "#8e4754",
            ),
            HoColors.Light.tokens(),
        )
    }

    @Test
    fun onTertiaryIsTheBadgeText() {
        assertEquals("#283432", HoColors.Dark.onTertiary.hex())
        assertEquals("#ffffff", HoColors.Light.onTertiary.hex())
    }

    @Test
    fun glassIsTheBackgroundSeeThrough() {
        assertEquals(Color(0xFF283432).copy(alpha = 0.72f), HoColors.Dark.glass)
        assertEquals(Color(0xFFE0EBE9).copy(alpha = 0.8f), HoColors.Light.glass)
    }

    private fun Color.hex(): String = "#" + (toArgb() and 0xFFFFFF).toString(16).padStart(6, '0')
}
