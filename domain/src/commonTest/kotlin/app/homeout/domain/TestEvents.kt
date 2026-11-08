package app.homeout.domain

import kotlin.time.Instant

/** An event with placeholder page details, for tests that only care about listing fields. */
internal fun testEvent(
    id: String,
    title: String,
    category: Category,
    artists: List<String>,
    venue: String,
    startsAt: Instant,
    price: Price = Price.Free,
    icon: EventIcon = EventIcon.MUSIC_NOTES,
) = Event(
    id = id,
    title = title,
    category = category,
    artists = artists,
    venue = venue,
    startsAt = startsAt,
    price = price,
    venueName = "Venue",
    description = "Description",
    poster = PosterColors(0xFF000000, 0xFFFFFFFF),
    icon = icon,
)
