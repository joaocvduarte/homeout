package app.homeout.getapp

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.homeout.GetAppDestination
import app.homeout.domain.EventDetail
import app.homeout.domain.GetEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GetAppState {
    data object Loading : GetAppState
    data object Missing : GetAppState
    data class Found(val detail: EventDetail) : GetAppState
}

/** The event Buy tickets was tapped on, looked up once. A plain state flow, so a Web test sees it on the next frame. */
class GetAppViewModel(
    savedStateHandle: SavedStateHandle,
    getEvent: GetEvent,
) : ViewModel() {
    private val id = savedStateHandle.toRoute<GetAppDestination>().id
    private val _state = MutableStateFlow<GetAppState>(GetAppState.Loading)
    val state: StateFlow<GetAppState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.value = getEvent(id)?.let(GetAppState::Found) ?: GetAppState.Missing
        }
    }
}
