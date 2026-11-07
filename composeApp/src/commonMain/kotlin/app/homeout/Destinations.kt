package app.homeout

import kotlinx.serialization.Serializable

@Serializable
internal data object HomeDestination

@Serializable
internal data class EventDestination(val id: String)
