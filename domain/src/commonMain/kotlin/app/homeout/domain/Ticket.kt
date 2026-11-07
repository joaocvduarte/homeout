package app.homeout.domain

/** Ticket [number] of [of] for one event. [payment] is null for a free ticket. */
data class Ticket(
    val code: String,
    val number: Int,
    val of: Int,
    val payment: PaymentMethod?,
)

/** Every ticket bought for [event], numbered in purchase order. */
data class TicketGroup(
    val event: Event,
    val tickets: List<Ticket>,
)
