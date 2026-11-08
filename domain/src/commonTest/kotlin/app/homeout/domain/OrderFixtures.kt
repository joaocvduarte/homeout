package app.homeout.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

/** Fado (paid, tonight), Market (free, tomorrow) and Petiscos (paid, tomorrow), deliberately out of start order. */
internal val ticketEvents = listOf(
    testEvent("petiscos-tasting", "Petiscos Tasting Night", Category.FOOD, listOf("Joana"), "Campo de Ourique", Instant.parse("2026-11-14T20:00:00Z"), Price.Euros(2500)),
    testEvent("fado-old-cistern", "Fado at the Old Cistern", Category.MUSIC, listOf("Inês"), "Alfama", Instant.parse("2026-11-13T21:30:00Z"), Price.Euros(1800)),
    testEvent("night-market-river", "Night Market by the River", Category.FOOD, listOf("Rui"), "Cais do Sodré", Instant.parse("2026-11-14T18:00:00Z")),
)

internal class FakeEvents(private val all: List<Event> = ticketEvents) : EventRepository {
    override suspend fun events() = all
}

internal class FakeOrders : OrderRepository {
    override val orders = MutableStateFlow<List<Order>>(emptyList())
    override suspend fun add(order: Order) = orders.update { it + order }
}
