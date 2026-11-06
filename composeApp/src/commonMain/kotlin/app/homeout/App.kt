package app.homeout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import app.homeout.data.SampleEventRepository
import app.homeout.designsystem.HoTheme
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.GetUpcomingEvents
import app.homeout.home.HomeRoute
import app.homeout.home.HomeStateHolder
import kotlinx.datetime.TimeZone
import kotlin.time.Clock

@Composable
fun App(
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.of("Europe/Lisbon"),
) {
    val scope = rememberCoroutineScope()
    val holder = remember(clock, timeZone) {
        val repository = SampleEventRepository(clock, timeZone)
        HomeStateHolder(
            getUpcomingEvents = GetUpcomingEvents(repository, clock, timeZone),
            countEventsThisWeek = CountEventsThisWeek(repository, clock, timeZone),
            scope = scope,
        )
    }
    HoTheme {
        HomeRoute(holder, timeZone)
    }
}
