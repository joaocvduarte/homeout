package app.homeout.domain

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/** Counts upcoming events from now until the end of Sunday of the current week. */
class CountEventsThisWeek(
    private val repository: EventRepository,
    private val clock: Clock,
    private val timeZone: TimeZone,
) {
    suspend operator fun invoke(): Int {
        val now = clock.now()
        val today = now.toLocalDateTime(timeZone).date
        val daysLeft = DayOfWeek.SUNDAY.ordinal - today.dayOfWeek.ordinal
        return repository.events().count { event ->
            val date = event.startsAt.toLocalDateTime(timeZone).date
            event.startsAt >= now && date.toEpochDays() - today.toEpochDays() <= daysLeft
        }
    }
}
