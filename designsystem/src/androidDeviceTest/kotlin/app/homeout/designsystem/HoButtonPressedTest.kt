package app.homeout.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class HoButtonPressedTest {

    @Test
    fun darkPressedColorsMatchTheDesign() = assertPressedColors(darkTheme = true)

    @Test
    fun lightPressedColorsMatchTheDesign() = assertPressedColors(darkTheme = false)

    private fun assertPressedColors(darkTheme: Boolean) = runComposeUiTest {
        val colors = if (darkTheme) HoColors.Dark else HoColors.Light
        val expected = mapOf(
            HoButtonVariant.Primary to (Color.Transparent to colors.primaryPressed),
            HoButtonVariant.Secondary to (colors.secondaryContainer to colors.secondaryPressed),
            HoButtonVariant.Outline to (Color.Transparent to colors.neutralPressed),
        )
        var variant by mutableStateOf(HoButtonVariant.Primary)
        setContent {
            HoTheme(darkTheme = darkTheme) {
                Box(Modifier.background(colors.background).padding(8.dp)) {
                    HoButton("Get tickets", {}, Modifier.testTag("button"), variant = variant)
                }
            }
        }

        expected.forEach { (current, pair) ->
            variant = current
            waitForIdle()
            val (idle, pressed) = pair
            val button = onNodeWithTag("button")

            button.assertBackground(idle.compositeOver(colors.background), "$current idle")
            button.performTouchInput { down(center) }
            waitForIdle()
            button.assertBackground(pressed.compositeOver(colors.background), "$current pressed")
            button.performTouchInput { up() }
            waitForIdle()
            button.assertBackground(idle.compositeOver(colors.background), "$current released")
        }
    }

    /** Samples a pixel inside the padding, clear of the 1dp border, the rounded corners and the label. */
    private fun SemanticsNodeInteraction.assertBackground(expected: Color, label: String) {
        val pixels = captureToImage().toPixelMap()
        val actual = pixels[pixels.width / 24, pixels.height / 2]
        val close = listOf(
            actual.red to expected.red,
            actual.green to expected.green,
            actual.blue to expected.blue,
        ).all { (a, e) -> abs(a - e) < 0.02f }
        assertTrue(close, "$label: expected $expected, got $actual")
    }
}
