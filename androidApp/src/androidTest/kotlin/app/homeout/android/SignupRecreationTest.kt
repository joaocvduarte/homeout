package app.homeout.android

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

/** The forms are rebuilt with the activity and keep what was typed, including the password, which is never saved to disk. */
@RunWith(AndroidJUnit4::class)
class SignupRecreationTest {

    @get:Rule(order = 0)
    val signedOut = object : ExternalResource() {
        override fun before() = clearAccounts()
    }

    @get:Rule(order = 1)
    val rule = createAndroidComposeRule<MainActivity>()

    @Test
    fun theCreateAccountFormKeepsEverythingTypedWhenTheActivityIsRecreated() {
        rule.onNodeWithTag("login-create").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("signup-name").performTextInput("Ana")
        rule.onNodeWithTag("signup-email").performTextInput("ana@x.pt")
        rule.onNodeWithTag("signup-password").performTextInput("12345678")
        rule.onNodeWithTag("signup-terms").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("signup-submit").assertIsEnabled()

        rule.activityRule.scenario.recreate()
        rule.waitForIdle()

        rule.onNodeWithTag("signup-page").assertExists()
        rule.onNodeWithTag("signup-name").assertTextContains("Ana")
        rule.onNodeWithTag("signup-email").assertTextContains("ana@x.pt")
        // Enabled again means the name, the email, the password and the terms all came back.
        rule.onNodeWithTag("signup-submit").assertIsEnabled()
    }

    @Test
    fun theLogInFormKeepsTheEmailAndPasswordWhenTheActivityIsRecreated() {
        rule.onNodeWithTag("login-email").performTextInput("ana@x.pt")
        rule.onNodeWithTag("login-password").performTextInput("12345678")
        rule.waitForIdle()
        rule.onNodeWithTag("login-submit").assertIsEnabled()

        rule.activityRule.scenario.recreate()
        rule.waitForIdle()

        rule.onNodeWithTag("login-email").assertTextContains("ana@x.pt")
        rule.onNodeWithTag("login-submit").assertIsEnabled()
    }

    @Test
    fun anEmptyFormStaysDisabledAfterRecreation() {
        rule.activityRule.scenario.recreate()
        rule.waitForIdle()
        rule.onNodeWithTag("login-submit").assertIsNotEnabled()
    }
}
