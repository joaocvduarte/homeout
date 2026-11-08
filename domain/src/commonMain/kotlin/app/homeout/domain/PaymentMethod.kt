package app.homeout.domain

/** How a paid order is paid. Payment is simulated: no provider is contacted. */
enum class PaymentMethod { MB_WAY, CARD, APPLE_PAY, GOOGLE_WALLET }
