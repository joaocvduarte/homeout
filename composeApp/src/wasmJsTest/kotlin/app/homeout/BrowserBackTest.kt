package app.homeout

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import app.homeout.domain.PaymentMethod
import app.homeout.domain.PlaceOrder
import kotlinx.browser.window
import kotlinx.datetime.TimeZone
import org.koin.mp.KoinPlatform
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class, ExperimentalBrowserHistoryApi::class)
class BrowserBackTest {

    private val clock = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    /** Hands control back to the browser so it can deliver popstate; waitUntil alone would starve it. */
    private suspend fun browserTick() = suspendCoroutine { cont ->
        window.setTimeout({ cont.resume(Unit); null }, 50)
    }

    private suspend fun ComposeUiTest.eventually(what: String, condition: () -> Boolean) {
        repeat(60) {
            waitForIdle()
            if (condition()) return
            browserTick()
        }
        assertTrue(condition(), "$what did not happen within 3 s")
    }

    /** Each test starts from the bare page URL: the app reads its first route from the fragment. */
    private fun cleanUrl() = window.history.replaceState(null, "", window.location.pathname + window.location.search)

    private fun ComposeUiTest.has(text: String) = onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun browserBackReturnsHome() = runComposeUiTest { withKoin(clock, TimeZone.of("Europe/Lisbon")) { browserBack() } }

    private suspend fun ComposeUiTest.browserBack() {
        cleanUrl()
        setContent {
            App(onNavHostReady = { it.bindToBrowserNavigation() })
        }
        eventually("Home shows") { has("UPCOMING NEAR YOU") }
        val homeHash = window.location.hash

        onNodeWithTag("event-fado-old-cistern").performClick()
        eventually("the event page opens") { has("HOMEOUT PRESENTS") }
        eventually("the URL names the event") { window.location.hash != homeHash }
        val eventHash = window.location.hash

        window.history.back()
        eventually("browser back shows Home") { has("UPCOMING NEAR YOU") && !has("HOMEOUT PRESENTS") }
        assertNotEquals(eventHash, window.location.hash, "the URL leaves the event")
    }

    @Test
    fun browserBackLeavesBuyForTheEvent() = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon")) {
            cleanUrl()
            setContent { App(onNavHostReady = { it.bindToBrowserNavigation() }) }
            eventually("Home shows") { has("UPCOMING NEAR YOU") }
            onNodeWithTag("event-petiscos-tasting").performClick()
            eventually("the event page opens") { has("HOMEOUT PRESENTS") }
            onNodeWithText("Buy tickets").performClick()
            eventually("Buy opens") { has("BUY TICKETS") }

            window.history.back()
            eventually("browser back shows the event page") { has("PETISCOS TASTING NIGHT") && !has("BUY TICKETS") }
        }
    }

    @Test
    fun browserBackLeavesAQrCodeForTickets() = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon")) {
            KoinPlatform.getKoin().get<PlaceOrder>()("fado-old-cistern", 2, PaymentMethod.CARD)
            cleanUrl()
            setContent { App(onNavHostReady = { it.bindToBrowserNavigation() }) }
            eventually("Home shows") { has("UPCOMING NEAR YOU") }
            onNodeWithTag("tab-Tickets").performClick()
            eventually("Tickets shows") { has("YOUR TICKETS") }
            onNodeWithTag("ticket-HO-FADO-002").performClick()
            eventually("the QR code opens") { has("YOUR TICKET") && !has("YOUR TICKETS") }

            window.history.back()
            eventually("browser back shows the Tickets tab") { has("YOUR TICKETS") && !has("YOUR TICKET") }
        }
    }

    private fun addressWith(fragment: String) =
        window.history.replaceState(null, "", window.location.pathname + window.location.search + "#" + fragment)

    @Test
    fun webBrowserBackLeavesGetTheAppForTheEvent() = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon"), signedIn = false) {
            cleanUrl()
            setContent { App(edition = AppEdition.Web, onNavHostReady = { it.bindToBrowserNavigation() }) }
            eventually("Home shows") { has("UPCOMING NEAR YOU") }
            onNodeWithTag("event-petiscos-tasting").performClick()
            eventually("the event page opens") { has("HOMEOUT PRESENTS") }
            onNodeWithText("Buy tickets").performClick()
            eventually("Get the app opens") { has("GET THE APP") }

            window.history.back()
            eventually("browser back shows the event page") { has("PETISCOS TASTING NIGHT") && !has("GET THE APP") }
        }
    }

    @Test
    fun webRestoredBuyAddressOpensGetTheApp() = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon"), signedIn = false) {
            cleanUrl()
            addressWith("app.homeout.BuyDestination/fado-old-cistern")
            setContent { App(edition = AppEdition.Web, onNavHostReady = { it.bindToBrowserNavigation() }) }
            eventually("Get the app opens, not Buy") { has("GET THE APP") && !has("BUY TICKETS") }
            assertTrue(has("Fado at the Old Cistern"))
        }
    }

    @Test
    fun webRestoredLogInAddressShowsHome() = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon"), signedIn = false) {
            cleanUrl()
            addressWith("app.homeout.LoginDestination")
            setContent { App(edition = AppEdition.Web, onNavHostReady = { it.bindToBrowserNavigation() }) }
            eventually("Home shows, not Log in") { has("UPCOMING NEAR YOU") && !has("Continue with Google") }
        }
    }
}
