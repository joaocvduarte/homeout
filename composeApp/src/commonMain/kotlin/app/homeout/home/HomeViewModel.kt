package app.homeout.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.homeout.domain.Category
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.EventFilter
import app.homeout.domain.GetUpcomingEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** Home's filter and its events. It lives as long as Home's back-stack entry, across recreation. */
class HomeViewModel(
    private val getUpcomingEvents: GetUpcomingEvents,
    private val countEventsThisWeek: CountEventsThisWeek,
) : ViewModel() {
    private val filter = MutableStateFlow(EventFilter())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeState> = filter
        .mapLatest { HomeState(it.category, getUpcomingEvents(it), countEventsThisWeek()) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, HomeState())

    fun onQueryChange(query: String) = filter.update { it.copy(query = query) }

    /** null selects All. */
    fun onCategorySelect(category: Category?) = filter.update { it.copy(category = category) }
}
