package app.homeout.root

import androidx.lifecycle.SavedStateHandle
import app.homeout.data.InMemoryOrderRepository
import app.homeout.data.SampleEventRepository
import app.homeout.domain.GetTicketGroups
import app.homeout.domain.PaymentMethod
import app.homeout.domain.PlaceOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class RootViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }
    private val events = SampleEventRepository(friday, TimeZone.UTC)
    private val orders = InMemoryOrderRepository()
    private val handle = SavedStateHandle()
    /** Built on first use, after the test Main dispatcher is in place. */
    private val root by lazy { RootViewModel(handle, GetTicketGroups(events, orders)) }

    @Test
    fun startsOnHomeWithNothingToConfirm() {
        assertEquals(Tab.HOME, root.tab.value)
        assertNull(root.confirmation.value)
        assertEquals(0, root.ticketCount.value)
    }

    @Test
    fun showTicketsSelectsTicketsWithTheMessageUntilTheTabChanges() {
        root.showTickets("Reserved. 1 ticket for Rooftop Vinyl Session added.")
        assertEquals(Tab.TICKETS, root.tab.value)
        assertEquals("Reserved. 1 ticket for Rooftop Vinyl Session added.", root.confirmation.value)

        root.select(Tab.HOME)
        assertEquals(Tab.HOME, root.tab.value)
        assertNull(root.confirmation.value)
    }

    @Test
    fun clearConfirmationKeepsTheTab() {
        root.showTickets("Payment confirmed. 1 ticket for Fado at the Old Cistern added.")
        root.clearConfirmation()
        assertEquals(Tab.TICKETS, root.tab.value)
        assertNull(root.confirmation.value)
    }

    @Test
    fun tabAndConfirmationLiveInTheSavedState() {
        root.showTickets("Payment confirmed. 2 tickets for Fado at the Old Cistern added.")
        val restored = RootViewModel(handle, GetTicketGroups(events, orders))
        assertEquals(Tab.TICKETS, restored.tab.value)
        assertEquals("Payment confirmed. 2 tickets for Fado at the Old Cistern added.", restored.confirmation.value)
    }

    @Test
    fun ticketCountFollowsEveryOrder() = runTest {
        root.ticketCount.value
        val place = PlaceOrder(events, orders)
        place("fado-old-cistern", 3, PaymentMethod.MB_WAY)
        place("night-market-river", 1, null)
        assertEquals(4, root.ticketCount.value)
    }
}
