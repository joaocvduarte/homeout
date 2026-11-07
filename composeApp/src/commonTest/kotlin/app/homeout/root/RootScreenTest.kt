package app.homeout.root

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
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
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class RootScreenTest {

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private fun rootTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(friday, TimeZone.of("Europe/Lisbon")) { block() }
    }

    private fun ComposeUiTest.showApp() {
        setContent { App() }
        waitForIdle()
    }

    private fun ComposeUiTest.has(text: String) = onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()

    private suspend fun place(eventId: String, quantity: Int, payment: PaymentMethod?) {
        KoinPlatform.getKoin().get<PlaceOrder>()(eventId, quantity, payment)
    }

    @Test
    fun homeIsSelectedWithThreeTabsAndNoBadge() = rootTest {
        showApp()
        onNodeWithTag("tab-Home").assertIsSelected()
        onNodeWithTag("tab-Tickets").assertIsNotSelected()
        onNodeWithText("Profile").assertExists()
        assertEquals(0, onAllNodes(hasText("Explore")).fetchSemanticsNodes().size)
        assertEquals(0, onAllNodes(hasTestTag("tickets-badge")).fetchSemanticsNodes().size)
    }

    @Test
    fun ticketsTabStartsEmptyAndFindEventsGoesHome() = rootTest {
        showApp()
        onNodeWithTag("tab-Tickets").performClick()
        waitForIdle()
        onNodeWithTag("tab-Tickets").assertIsSelected()
        onNodeWithText("YOUR TICKETS").assertExists()
        onNodeWithText("Nothing booked yet").assertExists()
        onNodeWithText("No tickets yet").assertExists()

        onNodeWithText("Find events").performClick()
        waitForIdle()
        onNodeWithTag("tab-Home").assertIsSelected()
        onNodeWithText("UPCOMING NEAR YOU").assertExists()
    }

    @Test
    fun ticketsAreGroupedByEventWithABadge() = rootTest {
        place("fado-old-cistern", 2, PaymentMethod.MB_WAY)
        place("night-market-river", 1, null)
        showApp()
        onNodeWithContentDescription("3 tickets").assertExists()

        onNodeWithTag("tab-Tickets").performClick()
        waitForIdle()
        onNodeWithText("3 tickets · 2 events").assertExists()
        onNodeWithText("FADO AT THE OLD CISTERN").assertExists()
        onNodeWithText("2 tickets").assertExists()
        assertEquals(1, onAllNodes(hasText("Ticket 1 of 2")).fetchSemanticsNodes().size)
        assertEquals(2, onAllNodes(hasText("General admission · Paid with MB WAY")).fetchSemanticsNodes().size)
        onNodeWithText("HO-FADO-001").assertExists()
        onNodeWithText("HO-FADO-002").assertExists()
        onNodeWithText("General admission · Free").assertExists()
        onNodeWithText("HO-NIGH-001").assertExists()
        assertEquals(0, onAllNodes(hasTestTag("tickets-empty")).fetchSemanticsNodes().size)
    }

    @Test
    fun ticketsFollowTheAgendaOrder() = rootTest {
        place("petiscos-tasting", 1, PaymentMethod.CARD)
        place("fado-old-cistern", 1, PaymentMethod.APPLE_PAY)
        showApp()
        onNodeWithTag("tab-Tickets").performClick()
        waitForIdle()
        val fado = onNodeWithTag("tickets-fado-old-cistern").fetchSemanticsNode().boundsInRoot.top
        val petiscos = onNodeWithTag("tickets-petiscos-tasting").fetchSemanticsNode().boundsInRoot.top
        assertEquals(true, fado < petiscos)
    }

    @Test
    fun profileTakesNoTaps() = rootTest {
        showApp()
        assertEquals(0, onAllNodes(hasTestTag("tab-Profile") and hasClickAction()).fetchSemanticsNodes().size)
    }
}
