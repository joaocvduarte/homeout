package app.homeout.buy

import app.homeout.domain.PaymentMethod
import app.homeout.domain.Price
import kotlin.test.Test
import kotlin.test.assertEquals

class BuyFormatTest {

    private val fado = Price.Euros(1800)

    @Test
    fun paidCopy() {
        assertEquals("€18 each", fado.eachLabel())
        assertEquals("€36", fado.totalLabel(2))
        assertEquals("Pay €36", fado.payLabel(2))
        assertEquals("1 ticket · Total", totalCaption(1))
        assertEquals("Payment confirmed. 2 tickets for Fado at the Old Cistern added.", confirmationMessage(false, 2, "Fado at the Old Cistern"))
    }

    @Test
    fun freeCopy() {
        assertEquals("Free entry", Price.Free.eachLabel())
        assertEquals("Free", Price.Free.totalLabel(3))
        assertEquals("Reserve 1 ticket", Price.Free.payLabel(1))
        assertEquals("Reserve 3 tickets", Price.Free.payLabel(3))
        assertEquals("Reserved. 1 ticket for Rooftop Vinyl Session added.", confirmationMessage(true, 1, "Rooftop Vinyl Session"))
    }

    @Test
    fun methodNotesFollowTheDesign() {
        assertEquals(
            listOf(
                "Confirm the payment in the MB WAY app",
                "Visa, Mastercard, American Express",
                "Pay with Face ID or Touch ID",
                "Pay with a card saved to your Google account",
            ),
            PaymentMethod.entries.map { it.note() },
        )
    }
}
