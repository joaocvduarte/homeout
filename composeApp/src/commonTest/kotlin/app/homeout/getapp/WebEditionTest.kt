package app.homeout.getapp

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.homeout.App
import app.homeout.AppEdition
import app.homeout.BuyDestination
import app.homeout.LoginDestination
import app.homeout.QrDestination
import app.homeout.SignupDestination
import app.homeout.withKoin
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/** The website: it only shows events, with no accounts, no tabs and no buying. Buy tickets sends people to the app. */
@OptIn(ExperimentalTestApi::class)
class WebEditionTest {

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private fun webTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(friday, TimeZone.of("Europe/Lisbon"), signedIn = false) { block() }
    }

    private fun ComposeUiTest.showWeb(): () -> NavHostController {
        lateinit var nav: NavHostController
        setContent {
            nav = rememberNavController()
            App(navController = nav, edition = AppEdition.Web)
        }
        waitForIdle()
        return { nav }
    }

    private fun ComposeUiTest.has(text: String) = onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()
    private fun ComposeUiTest.hasTag(tag: String) = onAllNodes(hasTestTag(tag)).fetchSemanticsNodes().isNotEmpty()

    private fun ComposeUiTest.openEvent(id: String, cta: String) {
        onNodeWithTag("event-$id").performClick()
        waitForIdle()
        onNodeWithText(cta).performClick()
        waitForIdle()
    }

    @Test
    fun opensOnHomeWithNoLogIn() = webTest {
        showWeb()
        assertTrue(has("UPCOMING NEAR YOU"))
        assertTrue(!hasTag("login-page"))
    }

    @Test
    fun theHeaderShowsOnlyTheLogo() = webTest {
        showWeb()
        assertTrue(has("HOMEOUT"))
        assertTrue(onAllNodes(hasContentDescription("Notifications")).fetchSemanticsNodes().isEmpty())
        assertTrue(!has("MA"))
    }

    @Test
    fun thereIsNoTabBar() = webTest {
        showWeb()
        listOf("Home", "Tickets", "Profile").forEach { assertTrue(!hasTag("tab-$it"), "tab-$it") }
    }

    @Test
    fun buyTicketsOpensGetTheAppWithTheEventAndBothBadges() = webTest {
        showWeb()
        openEvent("fado-old-cistern", "Buy tickets")

        assertTrue(hasTag("getapp-page"))
        assertTrue(!has("BUY TICKETS"))
        assertTrue(has("GET THE APP"))
        assertTrue(has("Fado at the Old Cistern"))
        assertTrue(has("Tickets live in the homeout app"))
        onNodeWithTag("badge-app-store").assertExists()
        onNodeWithTag("badge-google-play").assertExists()
    }

    @Test
    fun aFreeEventsGetFreeTicketsOpensGetTheAppToo() = webTest {
        showWeb()
        openEvent("rooftop-vinyl", "Get free tickets")
        assertTrue(hasTag("getapp-page"))
        assertTrue(has("Rooftop Vinyl Session"))
    }

    @Test
    fun backReturnsToTheEventAndABadgeGoesNowhere() = webTest {
        showWeb()
        openEvent("fado-old-cistern", "Buy tickets")
        onNodeWithTag("badge-app-store").performClick()
        waitForIdle()
        assertTrue(hasTag("getapp-page"), "a badge doesn't navigate")

        onNodeWithContentDescription("Back to event").performClick()
        waitForIdle()
        assertTrue(has("HOMEOUT PRESENTS") && !hasTag("getapp-page"))
    }

    @Test
    fun getTheAppPushesIn360Milliseconds() = webTest {
        showWeb()
        onNodeWithTag("event-fado-old-cistern").performClick()
        waitForIdle()
        mainClock.autoAdvance = false
        onNodeWithText("Buy tickets").performClick()
        mainClock.advanceTimeBy(300)
        assertTrue(has("HOMEOUT PRESENTS") && hasTag("getapp-page"), "both screens show while Get the app slides in")
        mainClock.advanceTimeBy(150)
        assertTrue(!has("HOMEOUT PRESENTS") && hasTag("getapp-page"), "the event page has left once 360 ms are over")
        mainClock.autoAdvance = true
    }

    @Test
    fun aRestoredBuyAddressOpensGetTheAppInsteadOfBuy() = webTest {
        val nav = showWeb()
        runOnUiThread { nav().navigate(BuyDestination("fado-old-cistern")) }
        waitForIdle()
        assertTrue(hasTag("getapp-page"))
        assertTrue(!has("BUY TICKETS"))
        assertTrue(has("Fado at the Old Cistern"))
    }

    @Test
    fun restoredQrLogInAndSignUpAddressesFallBackToHome() = webTest {
        val nav = showWeb()
        listOf(QrDestination("fado-old-cistern", 0), LoginDestination, SignupDestination).forEach { destination ->
            runOnUiThread { nav().navigate(destination) }
            waitForIdle()
            assertTrue(has("UPCOMING NEAR YOU"), "$destination falls back to Home")
            assertTrue(!hasTag("login-page") && !hasTag("signup-page"))
        }
    }
}
