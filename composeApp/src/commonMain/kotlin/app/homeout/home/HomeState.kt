package app.homeout.home

import app.homeout.domain.Category
import app.homeout.domain.DayGroup

data class HomeState(
    val category: Category? = null,
    val groups: List<DayGroup> = emptyList(),
    val eventsThisWeek: Int = 0,
)
