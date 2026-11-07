package app.homeout.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import app.homeout.domain.EventDetail
import app.homeout.domain.GetEvent
import kotlinx.datetime.TimeZone

private sealed interface Lookup {
    data object Loading : Lookup
    data object Missing : Lookup
    data class Found(val detail: EventDetail) : Lookup
}

/** Shows the event with [id], or goes back when there is no such event (a typed Web URL, say). */
@Composable
fun EventRoute(id: String, getEvent: GetEvent, timeZone: TimeZone, onBack: () -> Unit) {
    val lookup by produceState<Lookup>(Lookup.Loading, id) {
        value = getEvent(id)?.let(Lookup::Found) ?: Lookup.Missing
    }
    when (val current = lookup) {
        Lookup.Loading -> Unit
        Lookup.Missing -> LaunchedEffect(Unit) { onBack() }
        is Lookup.Found -> EventScreen(current.detail, timeZone, onBack)
    }
}
