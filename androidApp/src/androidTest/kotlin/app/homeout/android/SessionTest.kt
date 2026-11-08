package app.homeout.android

import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith

/** Whoever signs in stays signed in: a new launch of the app opens on Home. */
@RunWith(AndroidJUnit4::class)
class SessionTest {

    @get:Rule(order = 0)
    val signedOut = object : ExternalResource() {
        override fun before() = clearAccounts()
    }

    @get:Rule(order = 1)
    val compose = createEmptyComposeRule()

    private fun logInPages() = compose.onAllNodes(androidx.compose.ui.test.hasTestTag("login-page")).fetchSemanticsNodes().size

    @Test
    fun aNewLaunchOpensOnHomeForSomeoneWhoSignedIn() {
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithTag("login-google").performClick()
            compose.waitForIdle()
            compose.onNodeWithText("UPCOMING NEAR YOU").assertExists()
        }

        ActivityScenario.launch(MainActivity::class.java).use {
            compose.waitForIdle()
            compose.onNodeWithText("UPCOMING NEAR YOU").assertExists()
            assertEquals(0, logInPages())
        }
    }

    @Test
    fun aNewLaunchOpensOnLogInWhenNobodySignedIn() {
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.waitForIdle()
            assertEquals(1, logInPages())
        }
    }
}
