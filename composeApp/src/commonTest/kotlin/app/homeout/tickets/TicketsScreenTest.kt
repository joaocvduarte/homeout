package app.homeout.tickets

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import app.homeout.data.SampleEventRepository
import app.homeout.designsystem.HoTheme
import app.homeout.domain.PaymentMethod
import app.homeout.domain.Ticket
import app.homeout.domain.TicketGroup
import app.homeout.event.friday
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class TicketsScreenTest {

    private suspend fun fadoGroup(): TicketGroup {
        val fado = SampleEventRepository(friday, TimeZone.UTC).events().first { it.id == "fado-old-cistern" }
        return TicketGroup(
            fado,
            listOf(Ticket("HO-FADO-001", 1, 2, PaymentMethod.GOOGLE_WALLET), Ticket("HO-FADO-002", 2, 2, PaymentMethod.GOOGLE_WALLET)),
        )
    }

    @Test
    fun confirmationAndCardShow() = runComposeUiTest {
        val group = fadoGroup()
        setContent {
            HoTheme(darkTheme = false) {
                TicketsScreen(listOf(group), "Payment confirmed. 2 tickets for Fado at the Old Cistern added.", TimeZone.UTC, { _, _ -> }, {})
            }
        }
        onNodeWithTag("confirmation").assertExists()
        onNodeWithText("Payment confirmed. 2 tickets for Fado at the Old Cistern added.", useUnmergedTree = true).assertExists()
        onNodeWithText("2 tickets · 1 event").assertExists()
        onNodeWithText("FRI 13 NOV · 21:30 · ALFAMA").assertExists()
    }

    @Test
    fun tappingATicketReportsItsEventAndIndex() = runComposeUiTest {
        val group = fadoGroup()
        val tapped = mutableListOf<Pair<String, Int>>()
        setContent {
            HoTheme { TicketsScreen(listOf(group), null, TimeZone.UTC, { id, i -> tapped += id to i }, {}) }
        }
        onNodeWithTag("ticket-HO-FADO-002").assertHasClickAction().performClick()
        assertEquals(listOf("fado-old-cistern" to 1), tapped)
        assertEquals(0, onAllNodes(hasTestTag("confirmation")).fetchSemanticsNodes().size)
    }

    @Test
    fun emptyStateOffersFindEvents() = runComposeUiTest {
        var found = 0
        setContent { HoTheme { TicketsScreen(emptyList(), null, TimeZone.UTC, { _, _ -> }, { found++ }) } }
        onNodeWithText("Tickets you buy show up here, grouped by event.").assertExists()
        onNodeWithText("Find events").performClick()
        assertEquals(1, found)
    }
}
