package app.homeout.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Tickets grouped by event, in the events' start order; events without tickets are left out. */
class GetTicketGroups(
    private val events: EventRepository,
    private val orders: OrderRepository,
) {
    operator fun invoke(): Flow<List<TicketGroup>> = orders.orders.map { placed ->
        events.events().sortedBy { it.startsAt }.mapNotNull { event ->
            val payments = placed.filter { it.eventId == event.id }.flatMap { order -> List(order.quantity) { order.payment } }
            if (payments.isEmpty()) return@mapNotNull null
            val prefix = "HO-${event.id.take(4).uppercase()}-"
            TicketGroup(
                event = event,
                tickets = payments.mapIndexed { i, payment ->
                    Ticket(prefix + (i + 1).toString().padStart(3, '0'), number = i + 1, of = payments.size, payment = payment)
                },
            )
        }
    }
}
