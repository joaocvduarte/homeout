package app.homeout

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class AppTest {

    private val clock = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private val titles = mapOf(
        "fado-old-cistern" to "FADO AT THE OLD CISTERN",
        "rooftop-vinyl" to "ROOFTOP VINYL SESSION",
        "night-market-river" to "NIGHT MARKET BY THE RIVER",
        "petiscos-tasting" to "PETISCOS TASTING NIGHT",
    )

    /** A UI test with the app's Koin graph on a fixed Friday evening in Lisbon. */
    private fun appTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon")) { block() }
    }

    private fun ComposeUiTest.showApp(): () -> NavHostController {
        lateinit var nav: NavHostController
        setContent {
            nav = rememberNavController()
            App(navController = nav)
        }
        waitForIdle()
        return { nav }
    }

    private fun ComposeUiTest.onHome() = onAllNodes(hasText("UPCOMING NEAR YOU")).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun appOpensOnHomeInLisbonTime() = appTest {
        showApp()
        onNodeWithText("HOMEOUT").assertExists()
        onNodeWithText("TONIGHT · FRI 13 NOV").assertExists()
        onNodeWithText("Music · Alfama · 21:30").assertExists()
        assertEquals(0, onAllNodes(hasText("Notifications") and hasClickAction()).fetchSemanticsNodes().size)
    }

    private fun ComposeUiTest.opensAndReturns(id: String) {
        showApp()
        onNodeWithTag("event-$id").performClick()
        waitForIdle()
        onNodeWithText(titles.getValue(id)).assertExists()
        assertEquals(1, onAllNodes(hasText("HOMEOUT PRESENTS")).fetchSemanticsNodes().size, id)

        onNodeWithContentDescription("Back to events").performClick()
        waitForIdle()
        assertTrue(onHome(), "back from $id returns Home")
        assertEquals(0, onAllNodes(hasText("HOMEOUT PRESENTS")).fetchSemanticsNodes().size)
    }

    @Test
    fun fadoRowOpensFado() = appTest { opensAndReturns("fado-old-cistern") }

    @Test
    fun vinylRowOpensVinyl() = appTest { opensAndReturns("rooftop-vinyl") }

    @Test
    fun marketRowOpensMarket() = appTest { opensAndReturns("night-market-river") }

    @Test
    fun petiscosRowOpensPetiscos() = appTest { opensAndReturns("petiscos-tasting") }

    @Test
    fun headerStaysAndBottomNavHidesOnTheEventPage() = appTest {
        showApp()
        onNodeWithTag("event-fado-old-cistern").performClick()
        waitForIdle()
        onNodeWithText("HOMEOUT").assertExists()
        listOf("Explore", "Tickets", "Profile").forEach {
            assertEquals(0, onAllNodes(hasText(it)).fetchSemanticsNodes().size, it)
        }
    }

    @Test
    fun filterAndSearchSurviveGoingBack() = appTest {
        showApp()
        onNodeWithText("Food").performClick()
        onNodeWithTag("search").performTextInput("Petiscos")
        waitForIdle()
        onNodeWithTag("event-petiscos-tasting").performClick()
        waitForIdle()
        onNodeWithContentDescription("Back to events").performClick()
        waitForIdle()

        onNodeWithText("Food").assertIsSelected()
        onNodeWithTag("search").assertTextEquals("Petiscos")
        assertEquals(1, onAllNodes(hasTestTag("event-petiscos-tasting")).fetchSemanticsNodes().size)
        assertEquals(0, onAllNodes(hasTestTag("event-night-market-river")).fetchSemanticsNodes().size)
    }

    @Test
    fun unknownEventReturnsHome() = appTest {
        val nav = showApp()
        runOnIdle { nav().navigate(EventDestination("unknown")) }
        waitForIdle()
        assertTrue(onHome(), "an unknown event goes back Home")
        assertEquals(0, onAllNodes(hasText("HOMEOUT PRESENTS")).fetchSemanticsNodes().size)
    }

    private fun ComposeUiTest.onEvent() = onAllNodes(hasText("HOMEOUT PRESENTS")).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun pushTransitionTakes360Milliseconds() = appTest {
        showApp()
        mainClock.autoAdvance = false
        onNodeWithTag("event-fado-old-cistern").performClick()

        mainClock.advanceTimeBy(300)
        assertTrue(onHome() && onEvent(), "both screens are on screen while the push runs")
        mainClock.advanceTimeBy(150)
        assertTrue(!onHome() && onEvent(), "Home has left once 360 ms are over")

        onNodeWithContentDescription("Back to events").performClick()
        mainClock.advanceTimeBy(300)
        assertTrue(onHome() && onEvent(), "both screens are on screen while going back")
        mainClock.advanceTimeBy(150)
        assertTrue(onHome() && !onEvent(), "the event page has left once 360 ms are over")
        mainClock.autoAdvance = true
    }
}
