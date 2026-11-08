package app.homeout.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class HoTextFieldTest {

    private fun hasKey(key: androidx.compose.ui.semantics.SemanticsPropertyKey<*>) = SemanticsMatcher.keyIsDefined(key)

    @Test
    fun showsItsLabelAndPlaceholderUntilYouType() = runComposeUiTest {
        val state = TextFieldState()
        setContent {
            HoTheme(darkTheme = false) {
                HoTextField("Email", state, Modifier.testTag("email"), placeholder = "you@example.com")
            }
        }
        onNodeWithText("Email").assertExists()
        onNodeWithText("you@example.com").assertExists()

        onNodeWithTag("email").performTextInput("ana@x.pt")

        assertEquals("ana@x.pt", state.text.toString())
        assertEquals(0, onAllNodes(hasText("you@example.com")).fetchSemanticsNodes().size)
        onNodeWithTag("email").assertTextContains("ana@x.pt")
    }

    @Test
    fun anErrorShowsItsMessageAndReportsTheErrorState() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) {
                Column {
                    HoTextField("Email", TextFieldState("maya@example"), Modifier.testTag("bad"), error = "Enter an email like you@example.com.")
                    HoTextField("Name", TextFieldState(), Modifier.testTag("ok"))
                }
            }
        }
        onNodeWithText("Enter an email like you@example.com.").assertExists()
        onNodeWithTag("bad").assert(hasKey(SemanticsProperties.Error))
        onNodeWithTag("ok").assert(!hasKey(SemanticsProperties.Error))
    }

    @Test
    fun theHintShowsAndAnErrorReplacesIt() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = false) {
                Column {
                    HoTextField("Password", TextFieldState(), supporting = "At least 8 characters.", password = true)
                    HoTextField("Email", TextFieldState(), error = "Enter an email like you@example.com.", supporting = "ignored")
                }
            }
        }
        onNodeWithText("At least 8 characters.").assertExists()
        onNodeWithText("ignored").assertDoesNotExist()
    }

    @Test
    fun aPasswordFieldIsMarkedAsAPassword() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = false) {
                Column {
                    HoTextField("Password", TextFieldState(), Modifier.testTag("secret"), password = true)
                    HoTextField("Name", TextFieldState(), Modifier.testTag("plain"))
                }
            }
        }
        onNodeWithTag("secret").assert(hasKey(SemanticsProperties.Password))
        onNodeWithTag("plain").assert(!hasKey(SemanticsProperties.Password))
    }

    @Test
    fun losingFocusFiresOnFocusLostOnce() = runComposeUiTest {
        var lost = 0
        setContent {
            HoTheme(darkTheme = true) {
                Column {
                    HoTextField("Email", TextFieldState(), Modifier.testTag("first"), onFocusLost = { lost++ })
                    HoTextField("Name", TextFieldState(), Modifier.testTag("second"))
                }
            }
        }
        onNodeWithTag("first").performClick()
        waitForIdle()
        assertEquals(0, lost)
        onNodeWithTag("second").performClick()
        waitForIdle()
        assertEquals(1, lost)
    }
}
