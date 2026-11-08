package app.homeout.website

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.homeout.domain.Event
import app.homeout.domain.GetUpcomingEvents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** An upcoming event with whether it happens today, which the card's kicker needs. */
data class WebsiteEvent(val event: Event, val isToday: Boolean) {
    val id: String get() = event.id
}

/** [events] are the first [WebsiteViewModel.CARDS] upcoming ones; [total] counts them all. */
data class WebsiteState(
    val events: List<WebsiteEvent> = emptyList(),
    val total: Int = 0,
)

/** The website's next events, loaded once. A plain flow, updated directly. */
class WebsiteViewModel(private val getUpcomingEvents: GetUpcomingEvents) : ViewModel() {
    private val mutableState = MutableStateFlow(WebsiteState())
    val state: StateFlow<WebsiteState> = mutableState.asStateFlow()

    init {
        viewModelScope.launch {
            val all = getUpcomingEvents().flatMap { group -> group.events.map { WebsiteEvent(it, group.isToday) } }
            mutableState.value = WebsiteState(events = all.take(CARDS), total = all.size)
        }
    }

    companion object {
        const val CARDS = 4
    }
}
