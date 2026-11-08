package app.homeout.event

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.homeout.EventDestination
import app.homeout.domain.EventDetail
import app.homeout.domain.GetEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface EventState {
    data object Loading : EventState
    data object Missing : EventState
    data class Found(val detail: EventDetail) : EventState
}

/** The event its route names, looked up once and kept across recreation. */
class EventViewModel(
    savedStateHandle: SavedStateHandle,
    getEvent: GetEvent,
) : ViewModel() {
    private val id = savedStateHandle.toRoute<EventDestination>().id
    private val _state = MutableStateFlow<EventState>(EventState.Loading)
    val state: StateFlow<EventState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = getEvent(id)?.let(EventState::Found) ?: EventState.Missing
        }
    }
}
