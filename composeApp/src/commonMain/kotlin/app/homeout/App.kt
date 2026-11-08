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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.homeout.auth.LoginRoute
import app.homeout.auth.LoginViewModel
import app.homeout.auth.SignupRoute
import app.homeout.designsystem.HoFlowerBorder
import app.homeout.domain.GetSession
import app.homeout.designsystem.HoTheme
import app.homeout.event.EventRoute
import app.homeout.buy.BuyRoute
import app.homeout.qr.QrRoute
import app.homeout.root.RootScreen
import app.homeout.root.RootViewModel
import kotlinx.datetime.TimeZone
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

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
 * The app shell: background, flower border and header stay fixed while the tabs and the
 * screens pushed over them swap beneath. Dependencies come from Koin, started by each platform entry.
 * [onNavHostReady] hands the controller out once the graph is set, so a test can bind the browser's back button.
 */
@Composable
fun App(
    navController: NavHostController = rememberNavController(),
    onNavHostReady: suspend (NavHostController) -> Unit = {},
) {
    val timeZone = koinInject<TimeZone>()
    val back: () -> Unit = { if (navController.previousBackStackEntry != null) navController.popBackStack() }

    // Someone already signed in lands on Home; everyone else starts on Log in. Read once, before the first frame.
    val getSession = koinInject<GetSession>()
    val start: Any = remember { if (getSession() != null) RootDestination else LoginDestination }
    val current by navController.currentBackStackEntryAsState()
    val onAuth = current?.destination?.let { it.hasRoute<LoginDestination>() || it.hasRoute<SignupDestination>() }
        ?: (start == LoginDestination)
    // Signing in leaves neither Log in nor Create account behind, so back from Home leaves the app.
    val signedIn: () -> Unit = {
        val destination = navController.currentDestination
        if (destination != null && (destination.hasRoute<LoginDestination>() || destination.hasRoute<SignupDestination>())) {
            navController.navigate(RootDestination) { popUpTo<LoginDestination> { inclusive = true } }
        }
    }

    HoTheme {
        Box(Modifier.fillMaxSize().background(HoTheme.colors.background)) {
            HoFlowerBorder()
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars.union(DesignStatusBar))) {
                AppHeader(Modifier.padding(start = 20.dp, end = 20.dp, top = 6.dp), visible = !onAuth)
                NavHost(
                    navController = navController,
                    startDestination = start,
                    modifier = Modifier.weight(1f),
                    enterTransition = enter,
                    exitTransition = exit,
                    popEnterTransition = popEnter,
                    popExitTransition = popExit,
                ) {
                    composable<LoginDestination> {
                        LoginRoute(
                            viewModel = koinViewModel(),
                            onSignedIn = signedIn,
                            onCreateAccount = { navController.navigate(SignupDestination) },
                        )
                    }
                    composable<SignupDestination> {
                        // Log in instead fills the email on Log in's own ViewModel, then pops back to it.
                        val loginEntry = remember(it) { navController.getBackStackEntry<LoginDestination>() }
                        val login = koinViewModel<LoginViewModel>(viewModelStoreOwner = loginEntry)
                        SignupRoute(
                            viewModel = koinViewModel(),
                            onBack = back,
                            onSignedIn = signedIn,
                            onLoginInstead = { email ->
                                login.prefill(email)
                                navController.popBackStack()
                            },
                        )
                    }
                    composable<BuyDestination> {
                        val rootEntry = remember(it) { navController.getBackStackEntry<RootDestination>() }
                        val root = koinViewModel<RootViewModel>(viewModelStoreOwner = rootEntry)
                        BuyRoute(
                            viewModel = koinViewModel(),
                            timeZone = timeZone,
                            onBack = back,
                            onPaid = { message ->
                                root.showTickets(message)
                                navController.popBackStack(RootDestination, inclusive = false)
                            },
                        )
                    }
                    composable<QrDestination> {
                        QrRoute(koinViewModel(), timeZone, onBack = back)
                    }
                    composable<RootDestination> {
                        RootScreen(
                            root = koinViewModel(),
                            home = koinViewModel(),
                            tickets = koinViewModel(),
                            timeZone = timeZone,
                            onEventClick = { navController.navigate(EventDestination(it)) },
                            onTicketClick = { eventId, index -> navController.navigate(QrDestination(eventId, index)) },
                        )
                    }
                    composable<EventDestination> {
                        EventRoute(koinViewModel(), timeZone, onBack = back, onBuy = { navController.navigate(BuyDestination(it)) })
                    }
                }
            }
        }
    }
    LaunchedEffect(navController) { onNavHostReady(navController) }
}
