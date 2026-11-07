package app.homeout.event

import app.homeout.domain.Event
import app.homeout.domain.EventDetail
import app.homeout.domain.Price
import app.homeout.home.dayLabel
import app.homeout.home.label
import app.homeout.home.shortLabel
import app.homeout.home.timeLabel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun EventDetail.day(timeZone: TimeZone) = event.startsAt.toLocalDateTime(timeZone).date.shortLabel()

/** "Fri 13 Nov · 21:30", printed on the poster. */
internal fun EventDetail.posterDate(timeZone: TimeZone): String = event.posterDate(timeZone)

/** "Fri 13 Nov · 21:30": the event's day and start time. */
internal fun Event.posterDate(timeZone: TimeZone): String =
    "${startsAt.toLocalDateTime(timeZone).date.shortLabel()} · ${startsAt.timeLabel(timeZone)}"

/** "Fri 13 Nov · Doors 21:00 · Starts 21:30", "Fri 13 Nov · 22:00 – 02:00", or "Fri 13 Nov · 21:30". */
internal fun EventDetail.whenLine(timeZone: TimeZone): String {
    val start = event.startsAt.timeLabel(timeZone)
    val doors = event.doorsAt
    val ends = event.endsAt
    val times = when {
        doors != null -> "Doors ${doors.timeLabel(timeZone)} · Starts $start"
        ends != null -> "$start – ${ends.timeLabel(timeZone)}"
        else -> start
    }
    return "${day(timeZone)} · $times"
}

/** "Cisterna do Largo, Alfama". */
internal fun EventDetail.fullVenue(): String = "${event.venueName}, ${event.venue}"

/** "Music · Tonight · Fri 13 Nov". */
internal fun EventDetail.categoryAndDay(timeZone: TimeZone): String =
    "${event.category.label()} · ${dayLabel(event.startsAt.toLocalDateTime(timeZone).date, isToday)}"

private val EventDetail.isFree get() = event.price == Price.Free

internal fun EventDetail.priceNote(): String = if (isFree) "Ticket required" else "per person"

internal fun EventDetail.ticketLabel(): String = if (isFree) "Get free tickets" else "Buy tickets"
