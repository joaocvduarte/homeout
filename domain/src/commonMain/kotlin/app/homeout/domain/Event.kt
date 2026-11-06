package app.homeout.domain

import kotlin.time.Instant

data class Event(
    val id: String,
    val title: String,
    val category: Category,
    val artists: List<String>,
    val venue: String,
    val startsAt: Instant,
    val price: Price,
)
