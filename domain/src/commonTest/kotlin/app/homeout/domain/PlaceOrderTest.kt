package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PlaceOrderTest {

    private val orders = FakeOrders()
    private val placeOrder = PlaceOrder(FakeEvents(), orders)

    @Test
    fun paidOrderIsAdded() = runTest {
        val order = placeOrder("fado-old-cistern", 2, PaymentMethod.GOOGLE_WALLET)
        assertEquals(Order("fado-old-cistern", 2, PaymentMethod.GOOGLE_WALLET), order)
        assertEquals(listOf(order), orders.orders.value)
    }

    @Test
    fun freeOrderTakesNoPayment() = runTest {
        placeOrder("night-market-river", 8, null)
        assertEquals(listOf(Order("night-market-river", 8, null)), orders.orders.value)
    }

    @Test
    fun quantityMustBeOneToEight() = runTest {
        assertFailsWith<IllegalArgumentException> { placeOrder("fado-old-cistern", 0, PaymentMethod.MB_WAY) }
        assertFailsWith<IllegalArgumentException> { placeOrder("fado-old-cistern", 9, PaymentMethod.MB_WAY) }
        assertEquals(emptyList(), orders.orders.value)
    }

    @Test
    fun paymentMustMatchWhetherTheEventIsFree() = runTest {
        assertFailsWith<IllegalArgumentException> { placeOrder("fado-old-cistern", 1, null) }
        assertFailsWith<IllegalArgumentException> { placeOrder("night-market-river", 1, PaymentMethod.CARD) }
        assertEquals(emptyList(), orders.orders.value)
    }

    @Test
    fun unknownEventIsRejected() = runTest {
        assertFailsWith<IllegalArgumentException> { placeOrder("unknown", 1, PaymentMethod.CARD) }
    }
}
