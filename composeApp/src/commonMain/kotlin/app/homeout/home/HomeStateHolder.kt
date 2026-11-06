package app.homeout.home

import app.homeout.domain.Category
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.EventFilter
import app.homeout.domain.GetUpcomingEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeStateHolder(
    private val getUpcomingEvents: GetUpcomingEvents,
    private val countEventsThisWeek: CountEventsThisWeek,
    scope: CoroutineScope,
) {
    private val filter = MutableStateFlow(EventFilter())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeState> = filter
        .mapLatest { HomeState(it.category, getUpcomingEvents(it), countEventsThisWeek()) }
        .stateIn(scope, SharingStarted.Eagerly, HomeState())

    fun onQueryChange(query: String) = filter.update { it.copy(query = query) }

    /** null selects All. */
    fun onCategorySelect(category: Category?) = filter.update { it.copy(category = category) }
}
