package app.homeout.buy

import androidx.lifecycle.SavedStateHandle
import app.homeout.data.InMemoryOrderRepository
import app.homeout.data.SampleEventRepository
import app.homeout.domain.GetEvent
import app.homeout.domain.Order
import app.homeout.domain.PaymentMethod
import app.homeout.domain.PlaceOrder
import app.homeout.event.friday
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BuyViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private val events = SampleEventRepository(friday, TimeZone.UTC)
    private val orders = InMemoryOrderRepository()

    private fun buy(id: String, handle: SavedStateHandle = SavedStateHandle(mapOf("id" to id))) =
        BuyViewModel(handle, GetEvent(events, friday, TimeZone.UTC), PlaceOrder(events, orders))

    @Test
    fun startsAtOneTicketWithMbWay() {
        val state = buy("fado-old-cistern").state.value!!
        assertEquals("fado-old-cistern", state.detail.event.id)
        assertEquals(1, state.quantity)
        assertEquals(PaymentMethod.MB_WAY, state.method)
        assertFalse(state.canRemove)
        assertTrue(state.canAdd)
    }

    @Test
    fun countStaysBetweenOneAndEight() {
        val vm = buy("fado-old-cistern")
        vm.remove()
        assertEquals(1, vm.state.value!!.quantity)
        repeat(10) { vm.add() }
        assertEquals(8, vm.state.value!!.quantity)
        assertFalse(vm.state.value!!.canAdd)
    }

    @Test
    fun payPlacesTheOrderWithTheChosenMethod() = runTest {
        val vm = buy("fado-old-cistern")
        vm.add()
        vm.select(PaymentMethod.GOOGLE_WALLET)
        assertEquals("Payment confirmed. 2 tickets for Fado at the Old Cistern added.", vm.pay())
        assertEquals(listOf(Order("fado-old-cistern", 2, PaymentMethod.GOOGLE_WALLET)), orders.orders.value)
    }

    @Test
    fun freeEventReservesWithoutPayment() = runTest {
        val vm = buy("rooftop-vinyl")
        assertTrue(vm.state.value!!.free)
        assertEquals("Reserved. 1 ticket for Rooftop Vinyl Session added.", vm.pay())
        assertEquals(listOf(Order("rooftop-vinyl", 1, null)), orders.orders.value)
    }

    @Test
    fun countAndMethodLiveInTheSavedState() {
        val handle = SavedStateHandle(mapOf("id" to "fado-old-cistern"))
        buy("fado-old-cistern", handle).apply {
            add()
            add()
            select(PaymentMethod.CARD)
        }
        val restored = buy("fado-old-cistern", handle).state.value!!
        assertEquals(3, restored.quantity)
        assertEquals(PaymentMethod.CARD, restored.method)
    }
}
