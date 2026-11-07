package app.homeout.event

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoColors
import app.homeout.designsystem.HoTheme
import app.homeout.domain.EventDetail
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class EventScreenTest {

    private fun ComposeUiTest.show(detail: EventDetail, darkTheme: Boolean = true, onBack: () -> Unit = {}) {
        setContent {
            HoTheme(darkTheme = darkTheme) {
                EventScreen(detail, TimeZone.UTC, onBack, Modifier.width(390.dp).height(760.dp))
            }
        }
        waitForIdle()
    }

    @Test
    fun paidTonightEventShowsItsPage() = runComposeUiTest {
        val fado = sampleDetail("fado-old-cistern")
        show(fado)
        onNodeWithText("HOMEOUT PRESENTS").assertExists()
        onNodeWithText("FADO AT THE OLD CISTERN").assertExists()
        onNodeWithText("FRI 13 NOV · 21:30").assertExists()
        onNodeWithText("ALFAMA").assertExists()
        onNodeWithText("MUSIC · TONIGHT · FRI 13 NOV").assertExists()
        onNodeWithText("Fado at the Old Cistern").assertExists()
        onNodeWithText("Fri 13 Nov · Doors 21:00 · Starts 21:30").assertExists()
        onNodeWithText("Cisterna do Largo, Alfama").assertExists()
        onNodeWithText("ABOUT THIS EVENT").assertExists()
        onNodeWithText(fado.event.description).assertExists()
        onNodeWithText("€18").assertIsDisplayed()
        onNodeWithText("per person").assertIsDisplayed()
        onNodeWithText("Buy tickets").assertIsDisplayed()
    }

    @Test
    fun freeEventInLightTheme() = runComposeUiTest {
        show(sampleDetail("rooftop-vinyl"), darkTheme = false)
        onNodeWithText("Free").assertIsDisplayed()
        onNodeWithText("Ticket required").assertIsDisplayed()
        onNodeWithText("Get free tickets").assertIsDisplayed()
        onNodeWithText("Fri 13 Nov · 22:00 – 02:00").assertExists()
    }

    @Test
    fun tomorrowsDayLabelUsesTheAccentText() = runComposeUiTest {
        show(sampleDetail("petiscos-tasting"))
        onNodeWithText("FOOD · SAT 14 NOV").assertExists()
        listOf(HoColors.Dark, HoColors.Light).forEach { colors ->
            assertEquals(colors.onPrimaryText, dayLabelColor(isToday = false, colors))
            assertEquals(colors.tertiary, dayLabelColor(isToday = true, colors))
        }
    }

    @Test
    fun ticketShareAndSaveDoNothing() = runComposeUiTest {
        var backs = 0
        show(sampleDetail("fado-old-cistern"), onBack = { backs++ })
        onNodeWithText("Buy tickets").assertHasClickAction().performClick()
        onNodeWithContentDescription("Share event").assertHasClickAction().performClick()
        onNodeWithContentDescription("Save event").assertHasClickAction().performClick()
        waitForIdle()
        assertEquals(0, backs)
        onNodeWithText("Buy tickets").assertIsDisplayed()
        onNodeWithText("Fado at the Old Cistern").assertExists()
    }

    @Test
    fun backCallsOnBackOnce() = runComposeUiTest {
        var backs = 0
        show(sampleDetail("fado-old-cistern"), onBack = { backs++ })
        onNodeWithContentDescription("Back to events").assertHasClickAction().performClick()
        waitForIdle()
        assertEquals(1, backs)
    }

    @Test
    fun pageScrollsWhileTheTicketBarStays() = runComposeUiTest {
        val fado = sampleDetail("fado-old-cistern")
        show(fado)
        onNodeWithTag("event-page").performScrollToNode(hasText(fado.event.description))
        onNodeWithText(fado.event.description).assertIsDisplayed()
        onNodeWithTag("ticket-bar").assertIsDisplayed()
        onNodeWithContentDescription("Back to events").assertIsDisplayed()
    }
}
