package app.homeout.domain

/** Places an order: 1 to 8 tickets, with a payment method exactly when the event isn't free. */
class PlaceOrder(
    private val events: EventRepository,
    private val orders: OrderRepository,
) {
    suspend operator fun invoke(eventId: String, quantity: Int, payment: PaymentMethod?): Order {
        require(quantity in 1..Order.MAX_TICKETS) { "An order holds 1 to ${Order.MAX_TICKETS} tickets, not $quantity." }
        val event = requireNotNull(events.events().firstOrNull { it.id == eventId }) { "No event $eventId." }
        val free = event.price == Price.Free
        require(free == (payment == null)) {
            if (free) "${event.title} is free; it takes no payment." else "${event.title} needs a payment method."
        }
        return Order(eventId, quantity, payment).also { orders.add(it) }
    }
}
