package app.homeout.qr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import app.homeout.QrDestination
import app.homeout.domain.GetTicketGroups
import app.homeout.domain.TicketGroup
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Every ticket of the route's event; the screen opens on the route's index. */
class QrViewModel(handle: SavedStateHandle, getTicketGroups: GetTicketGroups) : ViewModel() {
    private val route = handle.toRoute<QrDestination>()

    /** The ticket the screen opens on. */
    val initialIndex: Int = route.index

    val group: StateFlow<TicketGroup?> = getTicketGroups()
        .map { groups -> groups.firstOrNull { it.event.id == route.eventId } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
