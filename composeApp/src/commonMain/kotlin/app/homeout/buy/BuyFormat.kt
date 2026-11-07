package app.homeout.buy

import app.homeout.domain.PaymentMethod
import app.homeout.domain.Price
import app.homeout.home.label
import app.homeout.tickets.ticketCountLabel

/** "€18 each", or "Free entry". */
internal fun Price.eachLabel(): String = when (this) {
    Price.Free -> "Free entry"
    is Price.Euros -> "${label()} each"
}

/** The price of [quantity] tickets: "€36", or "Free". */
internal fun Price.totalLabel(quantity: Int): String = when (this) {
    Price.Free -> "Free"
    is Price.Euros -> Price.Euros(cents * quantity).label()
}

/** "2 tickets · Total". */
internal fun totalCaption(quantity: Int): String = "${ticketCountLabel(quantity)} · Total"

/** "Pay €36", or "Reserve 2 tickets" for a free event. */
internal fun Price.payLabel(quantity: Int): String = when (this) {
    Price.Free -> "Reserve ${ticketCountLabel(quantity)}"
    is Price.Euros -> "Pay ${totalLabel(quantity)}"
}

/** "Payment confirmed. 2 tickets for Fado at the Old Cistern added.", or "Reserved. …" for a free event. */
internal fun confirmationMessage(free: Boolean, quantity: Int, title: String): String =
    "${if (free) "Reserved" else "Payment confirmed"}. ${ticketCountLabel(quantity)} for $title added."

internal fun PaymentMethod.note(): String = when (this) {
    PaymentMethod.MB_WAY -> "Confirm the payment in the MB WAY app"
    PaymentMethod.CARD -> "Visa, Mastercard, American Express"
    PaymentMethod.APPLE_PAY -> "Pay with Face ID or Touch ID"
    PaymentMethod.GOOGLE_WALLET -> "Pay with a card saved to your Google account"
}
