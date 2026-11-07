package app.homeout.domain

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/** An event and whether it happens today, as its page shows it. */
data class EventDetail(
    val event: Event,
    val isToday: Boolean,
)

/** Looks up one event by id, whether or not it has started. */
class GetEvent(
    private val repository: EventRepository,
    private val clock: Clock,
    private val timeZone: TimeZone,
) {
    suspend operator fun invoke(id: String): EventDetail? {
        val event = repository.events().firstOrNull { it.id == id } ?: return null
        val today = clock.now().toLocalDateTime(timeZone).date
        return EventDetail(event, isToday = event.startsAt.toLocalDateTime(timeZone).date == today)
    }
}
