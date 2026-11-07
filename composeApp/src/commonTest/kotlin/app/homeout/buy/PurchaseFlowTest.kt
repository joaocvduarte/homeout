package app.homeout.buy

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import app.homeout.App
import app.homeout.withKoin
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/** From an event page through Buy tickets to the Tickets tab, as a person does it. */
@OptIn(ExperimentalTestApi::class)
class PurchaseFlowTest {

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private fun flowTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(friday, TimeZone.of("Europe/Lisbon")) { block() }
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

    private fun ComposeUiTest.openBuy(eventId: String, cta: String) {
        onNodeWithTag("event-$eventId").performClick()
        waitForIdle()
        onNodeWithText(cta).performClick()
        waitForIdle()
    }

    @Test
    fun payingLandsOnTicketsWithTheConfirmation() = flowTest {
        val nav = showApp()
        openBuy("fado-old-cistern", "Buy tickets")
        onNodeWithText("BUY TICKETS").assertExists()
        onNodeWithText("Pay €18").assertExists()

        onNodeWithContentDescription("Add a ticket").performClick()
        onNodeWithTag("method-GOOGLE_WALLET").performClick()
        onNodeWithText("Pay €36").performClick()
        waitForIdle()

        onNodeWithTag("tab-Tickets").assertIsSelected()
        onNodeWithText("Payment confirmed. 2 tickets for Fado at the Old Cistern added.", useUnmergedTree = true).assertExists()
        onNodeWithText("Ticket 1 of 2").assertExists()
        onNodeWithText("Ticket 2 of 2").assertExists()
        assertEquals(2, onAllNodes(hasText("General admission · Paid with Google Wallet")).fetchSemanticsNodes().size)
        onNodeWithContentDescription("2 tickets").assertExists()
        assertEquals(null, nav().previousBackStackEntry, "Buy and the event page leave the back stack")
    }

    @Test
    fun reservingAFreeEventSkipsPayment() = flowTest {
        showApp()
        openBuy("rooftop-vinyl", "Get free tickets")
        assertTrue(!has("PAYMENT METHOD"))
        onNodeWithText("Reserve 1 ticket").performClick()
        waitForIdle()
        onNodeWithText("Reserved. 1 ticket for Rooftop Vinyl Session added.", useUnmergedTree = true).assertExists()
        onNodeWithText("General admission · Free").assertExists()
        onNodeWithText("HO-ROOF-001").assertExists()
    }

    @Test
    fun moreTicketsJoinTheSameCardAndTheBadgeGrows() = flowTest {
        showApp()
        openBuy("fado-old-cistern", "Buy tickets")
        onNodeWithText("Pay €18").performClick()
        waitForIdle()
        onNodeWithTag("tab-Home").performClick()
        waitForIdle()
        openBuy("fado-old-cistern", "Buy tickets")
        onNodeWithContentDescription("Add a ticket").performClick()
        onNodeWithText("Pay €36").performClick()
        waitForIdle()

        onNodeWithContentDescription("3 tickets").assertExists()
        onNodeWithText("3 tickets · 1 event").assertExists()
        onNodeWithText("Ticket 3 of 3").assertExists()
        onNodeWithText("HO-FADO-003").assertExists()
    }

    @Test
    fun backFromBuyReturnsToTheEvent() = flowTest {
        showApp()
        openBuy("petiscos-tasting", "Buy tickets")
        onNodeWithContentDescription("Back to event").performClick()
        waitForIdle()
        onNodeWithText("PETISCOS TASTING NIGHT").assertExists()
        assertTrue(!has("BUY TICKETS"))
    }

    @Test
    fun buyPushesIn360Milliseconds() = flowTest {
        showApp()
        onNodeWithTag("event-fado-old-cistern").performClick()
        waitForIdle()
        mainClock.autoAdvance = false
        onNodeWithText("Buy tickets").performClick()

        mainClock.advanceTimeBy(300)
        assertTrue(has("HOMEOUT PRESENTS") && has("BUY TICKETS"), "both screens show while Buy slides in")
        mainClock.advanceTimeBy(150)
        assertTrue(!has("HOMEOUT PRESENTS") && has("BUY TICKETS"), "the event page has left once 360 ms are over")

        onNodeWithContentDescription("Back to event").performClick()
        mainClock.advanceTimeBy(300)
        assertTrue(has("HOMEOUT PRESENTS") && has("BUY TICKETS"), "both screens show while going back")
        mainClock.advanceTimeBy(150)
        assertTrue(has("HOMEOUT PRESENTS") && !has("BUY TICKETS"), "Buy has left once 360 ms are over")
        mainClock.autoAdvance = true
    }
}
