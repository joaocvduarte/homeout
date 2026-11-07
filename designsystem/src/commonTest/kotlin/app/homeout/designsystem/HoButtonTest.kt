package app.homeout.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class HoButtonTest {

    @Test
    fun everyVariantShowsItsLabelAndClicks() = runComposeUiTest {
        var clicks = 0
        setContent {
            HoTheme(darkTheme = true) {
                Column {
                    HoButtonVariant.entries.forEach { variant ->
                        HoButton(variant.name, onClick = { clicks++ }, variant = variant, icon = HoIcons.Ticket)
                    }
                }
            }
        }
        HoButtonVariant.entries.forEach {
            onNodeWithText(it.name).assertHasClickAction().assertIsEnabled().performClick()
        }
        assertEquals(3, clicks)
    }

    @Test
    fun tintedButtonShowsItsLabelAndClicks() = runComposeUiTest {
        var clicks = 0
        setContent {
            HoTheme(darkTheme = false) {
                HoButton("Buy tickets", onClick = { clicks++ }, size = HoButtonSize.Large, icon = HoIcons.Ticket, block = true, tinted = true)
            }
        }
        onNodeWithText("Buy tickets").assertHasClickAction().assertHeightIsEqualTo(52.dp).performClick()
        assertEquals(1, clicks)
    }

    @Test
    fun sizesHaveTheDesignHeights() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) {
                Column {
                    HoButton("Large", {}, Modifier.testTag("large"), size = HoButtonSize.Large)
                    HoButton("Medium", {}, Modifier.testTag("medium"), size = HoButtonSize.Medium)
                }
            }
        }
        onNodeWithTag("large").assertHeightIsEqualTo(52.dp)
        onNodeWithTag("medium").assertHeightIsEqualTo(44.dp)
    }

    @Test
    fun blockButtonFillsTheWidth() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) {
                Box(Modifier.width(300.dp)) {
                    HoButton("Get tickets", {}, Modifier.testTag("block"), size = HoButtonSize.Large, block = true)
                }
            }
        }
        onNodeWithTag("block").assertWidthIsEqualTo(300.dp).assertHeightIsEqualTo(52.dp)
    }

    @Test
    fun disabledButtonIgnoresClicks() = runComposeUiTest {
        var clicks = 0
        setContent {
            HoTheme(darkTheme = true) {
                HoButton("Disabled", onClick = { clicks++ }, enabled = false)
            }
        }
        onNodeWithText("Disabled").assertIsNotEnabled().performClick()
        assertEquals(0, clicks)
    }

    @Test
    fun iconButtonIsSquareAndNamed() = runComposeUiTest {
        var clicks = 0
        setContent {
            HoTheme(darkTheme = true) {
                HoIconButton(HoIcons.Plus, "Add", onClick = { clicks++ }, modifier = Modifier.testTag("icon"))
            }
        }
        onNodeWithTag("icon").assertWidthIsEqualTo(44.dp).assertHeightIsEqualTo(44.dp).performClick()
        onNodeWithContentDescription("Add").assertExists()
        assertEquals(1, clicks)
    }
}
