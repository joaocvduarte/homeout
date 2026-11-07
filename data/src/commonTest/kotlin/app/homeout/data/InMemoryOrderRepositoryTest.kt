package app.homeout.data

import app.homeout.domain.Order
import app.homeout.domain.PaymentMethod
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryOrderRepositoryTest {

    @Test
    fun startsEmptyAndKeepsOrdersInPlacementOrder() = runTest {
        val repository = InMemoryOrderRepository()
        assertEquals(emptyList(), repository.orders.first())

        val first = Order("fado-old-cistern", 2, PaymentMethod.MB_WAY)
        val second = Order("night-market-river", 1, null)
        repository.add(first)
        repository.add(second)

        assertEquals(listOf(first, second), repository.orders.first())
    }
}
