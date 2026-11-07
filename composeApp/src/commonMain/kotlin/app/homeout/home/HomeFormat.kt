package app.homeout.home

import app.homeout.domain.Category
import app.homeout.domain.DayGroup
import app.homeout.domain.Price
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

private val dayFormat = LocalDate.Format {
    dayOfWeek(DayOfWeekNames.ENGLISH_ABBREVIATED)
    char(' ')
    day(padding = Padding.NONE)
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
}

/** "Fri 13 Nov". */
internal fun LocalDate.shortLabel(): String = format(dayFormat)

/** "Tonight · Fri 13 Nov" for today, "Sat 14 Nov" otherwise. */
internal fun dayLabel(date: LocalDate, isToday: Boolean): String {
    val day = date.shortLabel()
    return if (isToday) "Tonight · $day" else day
}

internal fun DayGroup.label(): String = dayLabel(date, isToday)

internal fun Category.label(): String = when (this) {
    Category.MUSIC -> "Music"
    Category.FOOD -> "Food"
}

/** "€18", "€18.50" or "Free". */
internal fun Price.label(): String = when (this) {
    Price.Free -> "Free"
    is Price.Euros -> {
        val euros = cents / 100
        val rest = cents % 100
        if (rest == 0L) "€$euros" else "€$euros.${rest.toString().padStart(2, '0')}"
    }
}

/** 24-hour "21:30". */
internal fun Instant.timeLabel(timeZone: TimeZone): String {
    val time = toLocalDateTime(timeZone).time
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

internal fun eventsThisWeekLabel(count: Int): String =
    if (count == 1) "1 event this week" else "$count events this week"
