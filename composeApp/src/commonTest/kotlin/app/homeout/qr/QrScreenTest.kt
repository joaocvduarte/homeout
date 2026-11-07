package app.homeout.qr

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
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
class QrScreenTest {

    private suspend fun group(id: String, count: Int, payment: PaymentMethod?): TicketGroup {
        val event = SampleEventRepository(friday, TimeZone.UTC).events().first { it.id == id }
        val prefix = "HO-${id.take(4).uppercase()}-"
        return TicketGroup(event, List(count) { Ticket(prefix + (it + 1).toString().padStart(3, '0'), it + 1, count, payment) })
    }

    @Test
    fun opensOnTheTappedTicketAndMovesWithArrowsAndSwipes() = runComposeUiTest {
        val fado = group("fado-old-cistern", 3, PaymentMethod.MB_WAY)
        var backs = 0
        setContent {
            HoTheme { QrScreen(fado, initialIndex = 1, timeZone = TimeZone.UTC, onBack = { backs++ }, modifier = Modifier.width(390.dp).height(760.dp)) }
        }
        onNodeWithText("YOUR TICKET").assertExists()
        onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 2 of 3")
        onNodeWithContentDescription("QR code for Ticket 2 of 3, HO-FADO-002").assertExists()
        onNodeWithText("Swipe sideways for your other tickets. Show each code at the entrance.").assertExists()

        onNodeWithContentDescription("Next ticket").performClick()
        waitForIdle()
        onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 3 of 3")
        onNodeWithContentDescription("Next ticket").assertIsNotEnabled()

        onNodeWithTag("qr-pager").performTouchInput { swipeRight() }
        waitForIdle()
        onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 2 of 3")

        onNodeWithContentDescription("Previous ticket").performClick()
        waitForIdle()
        onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 1 of 3")
        onNodeWithContentDescription("Previous ticket").assertIsNotEnabled()
        onNodeWithContentDescription("Next ticket").assertIsEnabled()

        onNodeWithTag("qr-pager").performTouchInput { swipeLeft() }
        waitForIdle()
        onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 2 of 3")

        onNodeWithContentDescription("Back to tickets").performClick()
        assertEquals(1, backs)
    }

    @Test
    fun aSingleTicketHasNoArrows() = runComposeUiTest {
        val market = group("night-market-river", 1, null)
        setContent { HoTheme(darkTheme = false) { QrScreen(market, 0, TimeZone.UTC, {}, Modifier.width(390.dp).height(760.dp)) } }
        onNodeWithTag("qr-position").assertTextEquals("Night Market by the River · Ticket 1 of 1")
        onNodeWithText("Show this code at the entrance.").assertExists()
        onNodeWithText("General admission · Free").assertExists()
        assertEquals(0, onAllNodes(hasContentDescription("Next ticket")).fetchSemanticsNodes().size)
        assertEquals(0, onAllNodes(hasContentDescription("Previous ticket")).fetchSemanticsNodes().size)
    }
}
