package app.homeout.qr

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import app.homeout.App
import app.homeout.domain.PaymentMethod
import app.homeout.domain.PlaceOrder
import app.homeout.withKoin
import kotlinx.datetime.TimeZone
import org.koin.mp.KoinPlatform
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

/** From the Tickets tab to a ticket's QR code and back. */
@OptIn(ExperimentalTestApi::class)
class TicketQrFlowTest {

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private fun flowTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(friday, TimeZone.of("Europe/Lisbon")) { block() }
    }

    private fun ComposeUiTest.has(text: String) = onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()

    /** Three Fado tickets, then the Tickets tab. */
    private suspend fun ComposeUiTest.ticketsTab() {
        KoinPlatform.getKoin().get<PlaceOrder>()("fado-old-cistern", 3, PaymentMethod.MB_WAY)
        setContent { App() }
        waitForIdle()
        onNodeWithTag("tab-Tickets").performClick()
        waitForIdle()
    }

    @Test
    fun aTicketOpensItsQrCodeAndBackReturnsToTickets() = flowTest {
        ticketsTab()
        onNodeWithTag("ticket-HO-FADO-002").performClick()
        waitForIdle()
        onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 2 of 3")
        onNodeWithContentDescription("QR code for Ticket 2 of 3, HO-FADO-002").assertExists()

        onNodeWithContentDescription("Back to tickets").performClick()
        waitForIdle()
        onNodeWithTag("tab-Tickets").assertIsSelected()
        onNodeWithText("3 tickets · 1 event").assertExists()
    }

    @Test
    fun openingATicketClearsThePurchaseConfirmation() = flowTest {
        setContent { App() }
        waitForIdle()
        onNodeWithTag("event-fado-old-cistern").performClick()
        waitForIdle()
        onNodeWithText("Buy tickets").performClick()
        waitForIdle()
        onNodeWithText("Pay €18").performClick()
        waitForIdle()
        assertTrue(has("Payment confirmed. 1 ticket for Fado at the Old Cistern added."))

        onNodeWithTag("ticket-HO-FADO-001").performClick()
        waitForIdle()
        onNodeWithContentDescription("Back to tickets").performClick()
        waitForIdle()
        assertTrue(!has("Payment confirmed. 1 ticket for Fado at the Old Cistern added."))
    }

    @Test
    fun qrPushesIn360Milliseconds() = flowTest {
        ticketsTab()
        mainClock.autoAdvance = false
        onNodeWithTag("ticket-HO-FADO-001").performClick()

        mainClock.advanceTimeBy(300)
        assertTrue(has("YOUR TICKETS") && has("YOUR TICKET"), "both screens show while the QR code slides in")
        mainClock.advanceTimeBy(150)
        assertTrue(!has("YOUR TICKETS") && has("YOUR TICKET"), "Tickets has left once 360 ms are over")

        onNodeWithContentDescription("Back to tickets").performClick()
        mainClock.advanceTimeBy(300)
        assertTrue(has("YOUR TICKETS") && has("YOUR TICKET"), "both screens show while going back")
        mainClock.advanceTimeBy(150)
        assertTrue(has("YOUR TICKETS") && !has("YOUR TICKET"), "the QR code has left once 360 ms are over")
        mainClock.autoAdvance = true
    }
}
