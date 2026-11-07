package app.homeout.data

import app.homeout.domain.Order
import app.homeout.domain.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

/** Orders kept only while the app runs, until a backend stores them. */
class InMemoryOrderRepository : OrderRepository {
    private val placed = MutableStateFlow<List<Order>>(emptyList())

    override val orders: StateFlow<List<Order>> = placed

    override suspend fun add(order: Order) = placed.update { it + order }
}
