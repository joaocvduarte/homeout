package app.homeout.domain

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class GetUpcomingEvents(
    private val repository: EventRepository,
    private val clock: Clock,
    private val timeZone: TimeZone,
) {
    suspend operator fun invoke(filter: EventFilter = EventFilter()): List<DayGroup> {
        val now = clock.now()
        val today = now.toLocalDateTime(timeZone).date
        val query = filter.query.trim()

        return repository.events()
            .asSequence()
            .filter { it.startsAt >= now }
            .filter { filter.category == null || it.category == filter.category }
            .filter { query.isEmpty() || it.matches(query) }
            .sortedBy { it.startsAt }
            .groupBy { it.startsAt.toLocalDateTime(timeZone).date }
            .map { (date, events) -> DayGroup(date, isToday = date == today, events = events) }
    }

    private fun Event.matches(query: String): Boolean =
        title.contains(query, ignoreCase = true) ||
            venue.contains(query, ignoreCase = true) ||
            artists.any { it.contains(query, ignoreCase = true) }
}
