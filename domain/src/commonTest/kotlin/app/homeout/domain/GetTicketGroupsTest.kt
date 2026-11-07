package app.homeout.domain

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetTicketGroupsTest {

    private val orders = FakeOrders()
    private val groups = GetTicketGroups(FakeEvents(), orders)

    @Test
    fun noOrdersMeansNoGroups() = runTest {
        assertEquals(emptyList(), groups().first())
    }

    @Test
    fun ticketsAreNumberedAndCodedPerEvent() = runTest {
        orders.add(Order("fado-old-cistern", 2, PaymentMethod.GOOGLE_WALLET))
        val fado = groups().first().single()
        assertEquals("fado-old-cistern", fado.event.id)
        assertEquals(
            listOf(
                Ticket("HO-FADO-001", 1, 2, PaymentMethod.GOOGLE_WALLET),
                Ticket("HO-FADO-002", 2, 2, PaymentMethod.GOOGLE_WALLET),
            ),
            fado.tickets,
        )
    }

    @Test
    fun moreTicketsForTheSameEventJoinItsGroup() = runTest {
        orders.add(Order("fado-old-cistern", 2, PaymentMethod.GOOGLE_WALLET))
        orders.add(Order("fado-old-cistern", 1, PaymentMethod.MB_WAY))
        val tickets = groups().first().single().tickets
        assertEquals(listOf("HO-FADO-001", "HO-FADO-002", "HO-FADO-003"), tickets.map { it.code })
        assertEquals(listOf(3, 3, 3), tickets.map { it.of })
        assertEquals(PaymentMethod.MB_WAY, tickets.last().payment)
    }

    @Test
    fun groupsFollowTheAgendaAndFreeTicketsHaveNoPayment() = runTest {
        orders.add(Order("petiscos-tasting", 1, PaymentMethod.CARD))
        orders.add(Order("night-market-river", 1, null))
        orders.add(Order("fado-old-cistern", 1, PaymentMethod.APPLE_PAY))
        val all = groups().first()
        assertEquals(listOf("fado-old-cistern", "night-market-river", "petiscos-tasting"), all.map { it.event.id })
        assertEquals(Ticket("HO-NIGH-001", 1, 1, null), all[1].tickets.single())
    }
}
