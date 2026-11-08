package app.homeout.tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.homeout.domain.GetTicketGroups
import app.homeout.domain.TicketGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/** Every ticket bought, grouped by event. */
class TicketsViewModel(getTicketGroups: GetTicketGroups) : ViewModel() {
    val groups: StateFlow<List<TicketGroup>> = getTicketGroups()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
