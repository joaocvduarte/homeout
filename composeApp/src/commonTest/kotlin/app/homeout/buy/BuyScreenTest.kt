package app.homeout.buy

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoTheme
import app.homeout.domain.PaymentMethod
import app.homeout.event.sampleDetail
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class BuyScreenTest {

    @Test
    fun paidEventShowsCounterMethodsAndTotal() = runComposeUiTest {
        val fado = sampleDetail("fado-old-cistern")
        var state by mutableStateOf(BuyState(fado, 1, PaymentMethod.MB_WAY))
        var backs = 0
        var pays = 0
        setContent {
            HoTheme {
                BuyScreen(
                    state, TimeZone.UTC,
                    onBack = { backs++ },
                    onAdd = { state = state.copy(quantity = state.quantity + 1) },
                    onRemove = { state = state.copy(quantity = state.quantity - 1) },
                    onSelect = { state = state.copy(method = it) },
                    onPay = { pays++ },
                    modifier = Modifier.width(390.dp).height(1400.dp),
                )
            }
        }
        onNodeWithText("BUY TICKETS").assertExists()
        onNodeWithText("Fri 13 Nov · 21:30").assertExists()
        onNodeWithText("Cisterna do Largo, Alfama").assertExists()
        onNodeWithText("€18 each").assertExists()
        onNodeWithText("Up to 8 tickets per order.").assertExists()
        onNodeWithContentDescription("Remove a ticket").assertIsNotEnabled()
        onNodeWithText("1 ticket · Total").assertIsDisplayed()
        onNodeWithText("Pay €18").assertIsDisplayed()
        onNodeWithTag("method-MB_WAY").assertIsSelected()
        listOf("CARD", "APPLE_PAY", "GOOGLE_WALLET").forEach { onNodeWithTag("method-$it").assertIsNotSelected() }

        onNodeWithContentDescription("Add a ticket").performClick()
        onNodeWithTag("method-GOOGLE_WALLET").performClick()
        waitForIdle()
        onNodeWithTag("quantity").assertExists()
        onNodeWithText("2", useUnmergedTree = true).assertExists()
        onNodeWithContentDescription("Remove a ticket").assertIsEnabled()
        onNodeWithText("€36").assertExists()
        onNodeWithText("Pay €36").performClick()
        onNodeWithTag("method-GOOGLE_WALLET").assertIsSelected()
        onNodeWithTag("method-MB_WAY").assertIsNotSelected()
        assertEquals(1, pays)

        onNodeWithContentDescription("Back to event").performClick()
        assertEquals(1, backs)
    }

    @Test
    fun counterStopsAtEight() = runComposeUiTest {
        val fado = sampleDetail("fado-old-cistern")
        setContent { HoTheme { BuyScreen(BuyState(fado, 8, PaymentMethod.CARD), TimeZone.UTC, {}, {}, {}, {}, {}) } }
        onNodeWithContentDescription("Add a ticket").assertIsNotEnabled()
        onNodeWithText("Pay €144").assertExists()
    }

    @Test
    fun freeEventSkipsPayment() = runComposeUiTest {
        val vinyl = sampleDetail("rooftop-vinyl")
        setContent { HoTheme(darkTheme = false) { BuyScreen(BuyState(vinyl, 1, PaymentMethod.MB_WAY), TimeZone.UTC, {}, {}, {}, {}, {}) } }
        onNodeWithText("Free entry").assertExists()
        onNodeWithText("This event is free. No payment needed: your tickets go straight to the Tickets tab.").assertExists()
        assertEquals(0, onAllNodes(hasText("PAYMENT METHOD")).fetchSemanticsNodes().size)
        onNodeWithText("Free").assertExists()
        onNodeWithText("Reserve 1 ticket").assertExists()
    }
}
