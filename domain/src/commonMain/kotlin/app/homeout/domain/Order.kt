package app.homeout.domain

/** [quantity] tickets for one event. [payment] is null for a free event. */
data class Order(
    val eventId: String,
    val quantity: Int,
    val payment: PaymentMethod?,
) {
    companion object {
        const val MAX_TICKETS = 8
    }
}
