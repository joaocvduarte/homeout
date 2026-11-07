package app.homeout.home

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runComposeUiTest
import app.homeout.designsystem.HoTheme
import app.homeout.withKoin
import kotlinx.datetime.TimeZone
import org.koin.compose.viewmodel.koinViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class HomeScreenTest {

    private val ids = listOf("fado-old-cistern", "rooftop-vinyl", "night-market-river", "petiscos-tasting")
    private val music = ids.take(2)
    private val food = ids.drop(2)

    private val opened = mutableListOf<String>()

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    /** A UI test with the app's Koin graph on a fixed Friday evening, in UTC. */
    private fun homeTest(block: suspend ComposeUiTest.() -> Unit) = runComposeUiTest {
        withKoin(friday) { block() }
    }

    private fun ComposeUiTest.showHome(darkTheme: Boolean = true) {
        setContent {
            HoTheme(darkTheme = darkTheme) { HomeRoute(koinViewModel(), TimeZone.UTC, onEventClick = { opened += it }) }
        }
        waitForIdle()
    }

    private fun ComposeUiTest.shownEvents(): List<String> =
        ids.filter { onAllNodes(hasTestTag("event-$it")).fetchSemanticsNodes().isNotEmpty() }

    @Test
    fun opensWithTodayThenTomorrowAndEachEventsCategory() = homeTest {
        showHome()
        onNodeWithText("UPCOMING NEAR YOU").assertExists()
        onNodeWithText("Lisbon · 4 events this week").assertExists()
        onNodeWithText("TONIGHT · FRI 13 NOV").assertExists()
        onNodeWithText("SAT 14 NOV").assertExists()
        onNodeWithText("Music · Alfama · 21:30").assertExists()
        onNodeWithText("Music · Bairro Alto · 22:00").assertExists()
        onNodeWithText("Food · Cais do Sodré · 18:00").assertExists()
        onNodeWithText("Food · Campo de Ourique · 20:00").assertExists()
        onNodeWithText("€18").assertExists()
        assertEquals(ids, shownEvents())
    }

    @Test
    fun categoryFilterShowsOnlyThatCategory() = homeTest {
        showHome()
        onNodeWithText("All").assertIsSelected()

        onNodeWithText("Music").performClick()
        waitForIdle()
        onNodeWithText("Music").assertIsSelected()
        onNodeWithText("All").assertIsNotSelected()
        assertEquals(music, shownEvents())

        onNodeWithText("Food").performClick()
        waitForIdle()
        assertEquals(food, shownEvents())

        onNodeWithText("All").performClick()
        waitForIdle()
        assertEquals(ids, shownEvents())
    }

    @Test
    fun searchFindsAnEventByArtist() = homeTest {
        showHome()
        onNodeWithTag("search").performTextInput("Joana Lobo")
        waitForIdle()
        assertEquals(listOf("petiscos-tasting"), shownEvents())
    }

    @Test
    fun searchWithinFoodFindsNoFado() = homeTest {
        showHome()
        onNodeWithText("Food").performClick()
        onNodeWithTag("search").performTextInput("fado")
        waitForIdle()
        assertEquals(emptyList(), shownEvents())

        onNodeWithTag("search").performTextClearance()
        waitForIdle()
        assertEquals(food, shownEvents())
    }

    @Test
    fun eachRowOpensItsOwnEvent() = homeTest {
        showHome()
        ids.forEach { onNodeWithTag("event-$it").assertHasClickAction().performClick() }
        assertEquals(ids, opened)
    }

    @Test
    fun bottomTabsHaveNoAction() = homeTest {
        showHome()
        listOf("Explore", "Tickets", "Profile", "Home").forEach {
            assertEquals(0, onAllNodes(hasText(it) and hasClickAction()).fetchSemanticsNodes().size, it)
        }
    }

    @Test
    fun lightThemeRendersTheSameContent() = homeTest {
        showHome(darkTheme = false)
        onNodeWithText("UPCOMING NEAR YOU").assertExists()
        assertEquals(ids, shownEvents())
    }
}
