package app.homeout.website

import app.homeout.domain.Event
import app.homeout.domain.Price
import app.homeout.event.posterDate
import app.homeout.home.label
import app.homeout.home.timeLabel
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** "Tonight" for today's events, else the full weekday: "Saturday". */
internal fun WebsiteEvent.kicker(timeZone: TimeZone): String {
    if (isToday) return "Tonight"
    return when (event.startsAt.toLocalDateTime(timeZone).dayOfWeek) {
        DayOfWeek.MONDAY -> "Monday"
        DayOfWeek.TUESDAY -> "Tuesday"
        DayOfWeek.WEDNESDAY -> "Wednesday"
        DayOfWeek.THURSDAY -> "Thursday"
        DayOfWeek.FRIDAY -> "Friday"
        DayOfWeek.SATURDAY -> "Saturday"
        DayOfWeek.SUNDAY -> "Sunday"
    }
}

/** "Fri 13 Nov · 21:30", printed on the poster. */
internal fun Event.websiteDate(timeZone: TimeZone): String = posterDate(timeZone)

/** "Music · 21:30". */
internal fun Event.categoryAndTime(timeZone: TimeZone): String = "${category.label()} · ${startsAt.timeLabel(timeZone)}"

/** "€18 per person" or "Free with a ticket". */
internal fun Event.websitePrice(): String = when (val p = price) {
    Price.Free -> "Free with a ticket"
    is Price.Euros -> "${p.label()} per person"
}

/** "Get tickets for Fado at the Old Cistern": the card button's accessible name. */
internal fun Event.ticketsLabel(): String = "Get tickets for $title"

/** "See all 4 events" or "See all 1 event". */
internal fun seeAllLabel(total: Int): String = if (total == 1) "See all 1 event" else "See all $total events"
