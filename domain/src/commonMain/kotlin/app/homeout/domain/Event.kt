package app.homeout.domain

import kotlin.time.Instant

/**
 * [venue] is the area shown on Home ("Alfama"); [venueName] is the place itself
 * ("Cisterna do Largo"). [doorsAt] and [endsAt] are given only when the event states them.
 */
data class Event(
    val id: String,
    val title: String,
    val category: Category,
    val artists: List<String>,
    val venue: String,
    val startsAt: Instant,
    val price: Price,
    val venueName: String,
    val description: String,
    val poster: PosterColors,
    val icon: EventIcon,
    val doorsAt: Instant? = null,
    val endsAt: Instant? = null,
)
