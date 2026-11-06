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
                "background" to "#0f1817",
                "surface" to "#192422",
                "onSurface" to "#e7eceb",
                "onSurfaceVariant" to "#b3bdbb",
                "outline" to "#3e4746",
                "primary" to "#70a89f",
                "onPrimaryText" to "#aee0d8",
                "secondaryContainer" to "#19302d",
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
                "background" to "#f3f8f7",
                "surface" to "#e4edec",
                "onSurface" to "#0f1817",
                "onSurfaceVariant" to "#596361",
                "outline" to "#ccd7d5",
                "primary" to "#548981",
                "onPrimaryText" to "#3b6a64",
                "secondaryContainer" to "#c9f2eb",
                "onSecondaryContainer" to "#284d48",
                "brand" to "#64837e",
                "tertiary" to "#8e4754",
            ),
            HoColors.Light.tokens(),
        )
    }

    private fun Color.hex(): String = "#" + (toArgb() and 0xFFFFFF).toString(16).padStart(6, '0')
}
