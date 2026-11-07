package app.homeout.domain

import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    /** Every order placed, oldest first. */
    val orders: Flow<List<Order>>

    suspend fun add(order: Order)
}
