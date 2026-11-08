package app.homeout.tickets

import app.homeout.domain.PaymentMethod
import app.homeout.domain.Ticket

/** "1 ticket", "3 tickets". */
internal fun ticketCountLabel(count: Int): String = if (count == 1) "1 ticket" else "$count tickets"

/** "3 tickets · 2 events", or "Nothing booked yet". */
internal fun ticketsSummary(tickets: Int, events: Int): String =
    if (tickets == 0) "Nothing booked yet" else "${ticketCountLabel(tickets)} · $events ${if (events == 1) "event" else "events"}"

internal fun PaymentMethod.label(): String = when (this) {
    PaymentMethod.MB_WAY -> "MB WAY"
    PaymentMethod.CARD -> "Credit or debit card"
    PaymentMethod.APPLE_PAY -> "Apple Pay"
    PaymentMethod.GOOGLE_WALLET -> "Google Wallet"
}

/** "Ticket 2 of 3". */
internal fun Ticket.label(): String = "Ticket $number of $of"

/** "General admission · Paid with MB WAY", or "General admission · Free". */
internal fun Ticket.detail(): String =
    payment?.let { "General admission · Paid with ${it.label()}" } ?: "General admission · Free"
