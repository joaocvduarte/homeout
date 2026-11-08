package app.homeout.root

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.homeout.home.HomeRoute
import app.homeout.home.HomeViewModel
import app.homeout.tickets.TicketsScreen
import app.homeout.tickets.TicketsViewModel
import kotlinx.datetime.TimeZone

private val tabEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

/** The tab pane and the tab bar. A new pane fades up (the design's 320 ms `ho-in`); the old one goes at once. */
@Composable
internal fun RootScreen(
    root: RootViewModel,
    home: HomeViewModel,
    tickets: TicketsViewModel,
    timeZone: TimeZone,
    onEventClick: (String) -> Unit,
    onTicketClick: (eventId: String, index: Int) -> Unit,
) {
    val tab by root.tab.collectAsState()
    val ticketCount by root.ticketCount.collectAsState()
    val rise = with(LocalDensity.current) { 10.dp.roundToPx() }
    Column(Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = tab,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            transitionSpec = {
                (fadeIn(tween(320, easing = tabEasing)) + slideInVertically(tween<IntOffset>(320, easing = tabEasing)) { rise }) togetherWith
                    fadeOut(snap())
            },
            label = "tab",
        ) { shown ->
            when (shown) {
                Tab.HOME -> HomeRoute(home, timeZone, onEventClick)
                Tab.TICKETS -> {
                    val groups by tickets.groups.collectAsState()
                    val confirmation by root.confirmation.collectAsState()
                    TicketsScreen(
                        groups = groups,
                        confirmation = confirmation,
                        timeZone = timeZone,
                        onTicketClick = { eventId, index ->
                            root.clearConfirmation()
                            onTicketClick(eventId, index)
                        },
                        onFindEvents = { root.select(Tab.HOME) },
                    )
                }
            }
        }
        TabBar(selected = tab, ticketCount = ticketCount, onSelect = root::select)
    }
}
