package app.homeout.tickets

import app.homeout.domain.PaymentMethod
import app.homeout.domain.Ticket
import kotlin.test.Test
import kotlin.test.assertEquals

class TicketsFormatTest {

    @Test
    fun countsAndSummary() {
        assertEquals("1 ticket", ticketCountLabel(1))
        assertEquals("3 tickets", ticketCountLabel(3))
        assertEquals("Nothing booked yet", ticketsSummary(0, 0))
        assertEquals("1 ticket · 1 event", ticketsSummary(1, 1))
        assertEquals("4 tickets · 2 events", ticketsSummary(4, 2))
    }

    @Test
    fun ticketLabelAndDetail() {
        assertEquals("Ticket 2 of 3", Ticket("HO-FADO-002", 2, 3, PaymentMethod.MB_WAY).label())
        assertEquals("General admission · Paid with MB WAY", Ticket("HO-FADO-002", 2, 3, PaymentMethod.MB_WAY).detail())
        assertEquals("General admission · Free", Ticket("HO-NIGH-001", 1, 1, null).detail())
    }

    @Test
    fun paymentMethodNamesFollowTheDesign() {
        assertEquals(
            listOf("MB WAY", "Credit or debit card", "Apple Pay", "Google Wallet"),
            PaymentMethod.entries.map { it.label() },
        )
    }
}
