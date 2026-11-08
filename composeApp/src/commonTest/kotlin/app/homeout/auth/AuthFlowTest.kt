package app.homeout.auth

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.homeout.App
import app.homeout.TestPasswordHasher
import app.homeout.domain.Account
import app.homeout.domain.AccountRepository
import app.homeout.withKoin
import kotlinx.datetime.TimeZone
import org.koin.mp.KoinPlatform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class AuthFlowTest {

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private fun flowTest(signedIn: Boolean = false, block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(friday, TimeZone.of("Europe/Lisbon"), signedIn = signedIn) { block() }
    }

    private fun ComposeUiTest.showApp(): () -> NavHostController {
        lateinit var nav: NavHostController
        setContent {
            nav = rememberNavController()
            App(navController = nav)
        }
        waitForIdle()
        return { nav }
    }

    private fun ComposeUiTest.has(text: String) = onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()

    private fun ComposeUiTest.hasTag(tag: String) =
        onAllNodes(androidx.compose.ui.test.hasTestTag(tag)).fetchSemanticsNodes().isNotEmpty()

    private fun ComposeUiTest.hasDescription(text: String) =
        onAllNodes(androidx.compose.ui.test.hasContentDescription(text)).fetchSemanticsNodes().isNotEmpty()

    private suspend fun addAccount() {
        KoinPlatform.getKoin().get<AccountRepository>().add(Account("Ana", "ana@x.pt"), TestPasswordHasher.hash("12345678"))
    }

    private fun ComposeUiTest.type(tag: String, text: String) {
        onNodeWithTag(tag).performTextInput(text)
    }

    private fun ComposeUiTest.fillSignup(name: String = "Ana", email: String = "ana@x.pt", password: String = "12345678", terms: Boolean = true) {
        type("signup-name", name)
        type("signup-email", email)
        type("signup-password", password)
        if (terms) onNodeWithTag("signup-terms").performClick()
        waitForIdle()
    }

    @Test
    fun opensOnLogInWithoutTheAppHeader() = flowTest {
        showApp()
        assertTrue(hasTag("login-page"))
        assertTrue(!has("UPCOMING NEAR YOU"))
        assertTrue(!hasDescription("Notifications"), "the header's bell is hidden from accessibility")
        assertEquals(1, onAllNodes(hasText("HOMEOUT")).fetchSemanticsNodes().size, "only the large logo shows")
    }

    @Test
    fun signedInOpensOnHomeAndNeverComposesLogIn() = flowTest(signedIn = true) {
        showApp()
        assertTrue(has("UPCOMING NEAR YOU"))
        assertTrue(!hasTag("login-page"))
        assertTrue(hasDescription("Notifications"), "the header shows on Home")
    }

    @Test
    fun logInStaysDisabledUntilTheEmailIsValidAndThePasswordHasEightCharacters() = flowTest {
        showApp()
        onNodeWithTag("login-submit").assertIsNotEnabled()
        type("login-email", "ana@x")
        type("login-password", "12345678")
        waitForIdle()
        onNodeWithTag("login-submit").assertIsNotEnabled()
        type("login-email", ".pt")
        waitForIdle()
        onNodeWithTag("login-submit").assertIsEnabled()
    }

    @Test
    fun anInvalidEmailAndAShortPasswordShowTheirErrorsAfterFocusMoves() = flowTest {
        showApp()
        type("login-email", "maya@example")
        waitForIdle()
        assertTrue(!has("Enter an email like you@example.com."), "no error while the field still has focus")
        type("login-password", "lisbon")
        waitForIdle()
        assertTrue(has("Enter an email like you@example.com."), "the email lost focus")
        onNodeWithTag("login-email").performClick()
        waitForIdle()
        assertTrue(has("At least 8 characters."), "the password lost focus")
    }

    @Test
    fun createAnAccountOpensTheFormAndCreatingItLandsOnHomeWithTheHeader() = flowTest {
        val nav = showApp()
        onNodeWithTag("login-create").performClick()
        waitForIdle()
        assertTrue(hasTag("signup-page"))
        onNodeWithTag("signup-submit").assertIsNotEnabled()

        fillSignup()
        onNodeWithTag("signup-submit").assertIsEnabled().performClick()
        waitForIdle()

        assertTrue(has("UPCOMING NEAR YOU"))
        assertTrue(hasDescription("Notifications"))
        assertNull(nav().previousBackStackEntry, "Log in and Create account leave the back stack")
        assertEquals("ana@x.pt", KoinPlatform.getKoin().get<AccountRepository>().session())
    }

    @Test
    fun createAccountStaysDisabledWithoutTheTerms() = flowTest {
        showApp()
        onNodeWithTag("login-create").performClick()
        waitForIdle()
        fillSignup(terms = false)
        onNodeWithTag("signup-submit").assertIsNotEnabled()
        onNodeWithTag("signup-terms").performClick()
        waitForIdle()
        onNodeWithTag("signup-submit").assertIsEnabled()
    }

    @Test
    fun logInWithAnAccountOnThePhoneReachesHome() = flowTest {
        addAccount()
        val nav = showApp()
        type("login-email", "ana@x.pt")
        type("login-password", "12345678")
        waitForIdle()
        onNodeWithTag("login-submit").performClick()
        waitForIdle()
        assertTrue(has("UPCOMING NEAR YOU"))
        assertNull(nav().previousBackStackEntry)
    }

    @Test
    fun aWrongPasswordShowsTheMismatchAndEditingRemovesIt() = flowTest {
        addAccount()
        showApp()
        type("login-email", "ana@x.pt")
        type("login-password", "00000000")
        waitForIdle()
        onNodeWithTag("login-submit").performClick()
        waitForIdle()

        assertTrue(has("That email and password don't match an account on this phone. Check them, or create an account."))
        assertTrue(hasTag("login-page"))

        type("login-password", "1")
        waitForIdle()
        assertTrue(!hasTag("login-mismatch"))
    }

    @Test
    fun anUnknownEmailShowsTheSameMismatch() = flowTest {
        showApp()
        type("login-email", "nobody@x.pt")
        type("login-password", "12345678")
        waitForIdle()
        onNodeWithTag("login-submit").performClick()
        waitForIdle()
        assertTrue(hasTag("login-mismatch"))
    }

    @Test
    fun anEmailThatHasAnAccountShowsTheTakenAlertAndLogInInsteadFillsIt() = flowTest {
        addAccount()
        val nav = showApp()
        onNodeWithTag("login-create").performClick()
        waitForIdle()
        fillSignup()
        onNodeWithTag("signup-submit").performClick()
        waitForIdle()

        assertTrue(has("There's already an account for this email on this phone."))
        assertTrue(hasTag("signup-page"))
        assertNull(KoinPlatform.getKoin().get<AccountRepository>().session())

        onNodeWithTag("signup-login-instead").performClick()
        waitForIdle()

        assertTrue(hasTag("login-page") && !hasTag("signup-page"))
        onNodeWithTag("login-email").assertTextContains("ana@x.pt")
        assertEquals(null, nav().previousBackStackEntry)
    }

    @Test
    fun bothGoogleButtonsReachHome() = flowTest {
        showApp()
        onNodeWithTag("login-google").performClick()
        waitForIdle()
        assertTrue(has("UPCOMING NEAR YOU"))
    }

    @Test
    fun signUpWithGoogleReachesHome() = flowTest {
        showApp()
        onNodeWithTag("login-create").performClick()
        waitForIdle()
        onNodeWithTag("signup-google").performClick()
        waitForIdle()
        assertTrue(has("UPCOMING NEAR YOU"))
    }

    @Test
    fun theBackButtonAndTheFooterLogInReturnToLogIn() = flowTest {
        showApp()
        onNodeWithTag("login-create").performClick()
        waitForIdle()
        onNodeWithContentDescription("Back to log in").performClick()
        waitForIdle()
        assertTrue(hasTag("login-page") && !hasTag("signup-page"))

        onNodeWithTag("login-create").performClick()
        waitForIdle()
        onNodeWithTag("signup-login").performClick()
        waitForIdle()
        assertTrue(hasTag("login-page") && !hasTag("signup-page"))
    }

    @Test
    fun forgotPasswordDoesNothing() = flowTest {
        showApp()
        onNodeWithTag("login-forgot").performClick()
        waitForIdle()
        assertTrue(hasTag("login-page"))
        assertTrue(!hasTag("login-mismatch"))
    }

    @Test
    fun createAccountPushesIn360MillisecondsAndSigningInSlidesBothAway() = flowTest {
        showApp()
        mainClock.autoAdvance = false
        onNodeWithTag("login-create").performClick()
        mainClock.advanceTimeBy(300)
        assertTrue(hasTag("login-page") && hasTag("signup-page"), "both screens show while Create account slides in")
        mainClock.advanceTimeBy(150)
        assertTrue(!hasTag("login-page") && hasTag("signup-page"), "Log in has left once 360 ms are over")

        onNodeWithTag("signup-google").performClick()
        mainClock.advanceTimeBy(300)
        assertTrue(hasTag("signup-page") && has("UPCOMING NEAR YOU"), "both screens show while Home slides in")
        mainClock.advanceTimeBy(150)
        assertTrue(!hasTag("signup-page") && has("UPCOMING NEAR YOU"), "Create account has left once 360 ms are over")
        mainClock.autoAdvance = true
    }
}
