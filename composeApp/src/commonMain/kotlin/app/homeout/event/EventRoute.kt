package app.homeout.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.datetime.TimeZone

/** Shows the event its ViewModel found, or goes back when there is none (a typed Web URL, say). */
@Composable
fun EventRoute(viewModel: EventViewModel, timeZone: TimeZone, onBack: () -> Unit, onBuy: (String) -> Unit) {
    val state by viewModel.state.collectAsState()
    when (val current = state) {
        EventState.Loading -> Unit
        EventState.Missing -> LaunchedEffect(Unit) { onBack() }
        is EventState.Found -> EventScreen(current.detail, timeZone, onBack, onBuy = { onBuy(current.detail.event.id) })
    }
}
