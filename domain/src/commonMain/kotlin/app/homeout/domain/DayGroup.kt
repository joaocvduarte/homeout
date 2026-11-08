package app.homeout.domain

import kotlinx.datetime.LocalDate

data class DayGroup(
    val date: LocalDate,
    val isToday: Boolean,
    val events: List<Event>,
)
