package app.homeout.root

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.homeout.domain.GetTicketGroups
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class Tab { HOME, TICKETS }

/** The selected tab and the last purchase's confirmation, kept across recreation, plus the ticket count. */
class RootViewModel(
    private val handle: SavedStateHandle,
    getTicketGroups: GetTicketGroups,
) : ViewModel() {

    val tab: StateFlow<Tab> = handle.getStateFlow(TAB, Tab.HOME.name)
        .map(Tab::valueOf)
        .stateIn(viewModelScope, SharingStarted.Eagerly, Tab.valueOf(handle[TAB] ?: Tab.HOME.name))

    val confirmation: StateFlow<String?> = handle.getStateFlow(CONFIRMATION, null)

    val ticketCount: StateFlow<Int> = getTicketGroups()
        .map { groups -> groups.sumOf { it.tickets.size } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    /** Switching tab dismisses the confirmation. */
    fun select(tab: Tab) {
        handle[TAB] = tab.name
        handle[CONFIRMATION] = null
    }

    /** After paying or reserving: the Tickets tab, with [message] on top. */
    fun showTickets(message: String) {
        handle[TAB] = Tab.TICKETS.name
        handle[CONFIRMATION] = message
    }

    fun clearConfirmation() {
        handle[CONFIRMATION] = null
    }

    private companion object {
        const val TAB = "tab"
        const val CONFIRMATION = "confirmation"
    }
}
