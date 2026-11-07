package app.homeout

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import app.homeout.designsystem.HoFlowerBorder
import app.homeout.designsystem.HoTheme
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.GetEvent
import app.homeout.domain.GetUpcomingEvents
import app.homeout.event.EventRoute
import app.homeout.home.HomeRoute
import app.homeout.home.HomeStateHolder
import kotlinx.datetime.TimeZone
import org.koin.compose.koinInject

/** The design reserves a 50 px status-bar band above the header; Web has no status bar, so keep the band there too. */
private val DesignStatusBar = WindowInsets(top = 50.dp)

/** The design's push: 360 ms, cubic-bezier(0.2, 0.8, 0.2, 1). Home moves 28% left and fades. */
private val push = tween<Float>(360, easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f))
private val pushOffset = tween<androidx.compose.ui.unit.IntOffset>(360, easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f))
private fun behind(width: Int) = -(width * 0.28f).toInt()

private val enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    { slideInHorizontally(pushOffset) { it } }
private val exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    { slideOutHorizontally(pushOffset, ::behind) + fadeOut(push) }
private val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition =
    { slideInHorizontally(pushOffset, ::behind) + fadeIn(push) }
private val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition =
    { slideOutHorizontally(pushOffset) { it } }

/**
 * The app shell: background, flower border and header stay fixed while Home and the
 * event page swap beneath them. Dependencies come from Koin, started by each platform entry.
 * [onNavHostReady] lets Web bind the browser's back button.
 */
@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    onNavHostReady: suspend (NavHostController) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val timeZone = koinInject<TimeZone>()
    val getUpcomingEvents = koinInject<GetUpcomingEvents>()
    val countEventsThisWeek = koinInject<CountEventsThisWeek>()
    val getEvent = koinInject<GetEvent>()
    val holder = remember(getUpcomingEvents, countEventsThisWeek) {
        HomeStateHolder(getUpcomingEvents, countEventsThisWeek, scope)
    }
    val backToHome: () -> Unit = { navController.popBackStack(HomeDestination, inclusive = false) }

    HoTheme {
        Box(Modifier.fillMaxSize().background(HoTheme.colors.background)) {
            HoFlowerBorder()
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars.union(DesignStatusBar))) {
                AppHeader(Modifier.padding(start = 20.dp, end = 20.dp, top = 6.dp))
                NavHost(
                    navController = navController,
                    startDestination = HomeDestination,
                    modifier = Modifier.weight(1f),
                    enterTransition = enter,
                    exitTransition = exit,
                    popEnterTransition = popEnter,
                    popExitTransition = popExit,
                ) {
                    composable<HomeDestination> {
                        HomeRoute(holder, timeZone, onEventClick = { navController.navigate(EventDestination(it)) })
                    }
                    composable<EventDestination> { entry ->
                        EventRoute(entry.toRoute<EventDestination>().id, getEvent, timeZone, onBack = backToHome)
                    }
                }
            }
        }
    }
    LaunchedEffect(navController) { onNavHostReady(navController) }
}
