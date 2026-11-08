package app.homeout

import kotlinx.serialization.Serializable

/** Log in. The first screen unless someone is already signed in. */
@Serializable
internal data object LoginDestination

/** Create account, pushed over Log in. */
@Serializable
internal data object SignupDestination

/** The tabs (Home, Tickets) and their bar. Every other screen is pushed over it. */
@Serializable
internal data object RootDestination

@Serializable
internal data class EventDestination(val id: String)

@Serializable
internal data class BuyDestination(val id: String)

/** Web only: where Buy tickets sends people, to the app stores. */
@Serializable
internal data class GetAppDestination(val id: String)

/** The QR code of ticket [index] of [eventId]'s tickets. */
@Serializable
internal data class QrDestination(val eventId: String, val index: Int)
