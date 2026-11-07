package app.homeout

import kotlinx.serialization.Serializable

/** The tabs (Home, Tickets) and their bar. Every other screen is pushed over it. */
@Serializable
internal data object RootDestination

@Serializable
internal data class EventDestination(val id: String)
