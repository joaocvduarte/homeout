package app.homeout

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.AndroidComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.v2.runAndroidComposeUiTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class SystemBackTest {

    private val clock = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    @Test
    fun systemBackReturnsHomeWithItsFilterAndSearch() = runAndroidComposeUiTest<ComponentActivity> {
        withKoin(clock, TimeZone.of("Europe/Lisbon")) { systemBack() }
    }

    private suspend fun AndroidComposeUiTest<ComponentActivity>.systemBack() {
        setContent { App() }
        onNodeWithText("Food").performClick()
        onNodeWithTag("search").performTextInput("Petiscos")
        waitForIdle()
        onNodeWithTag("event-petiscos-tasting").performClick()
        waitForIdle()
        onNodeWithText("PETISCOS TASTING NIGHT").assertExists()

        runOnUiThread { activity!!.onBackPressedDispatcher.onBackPressed() }
        waitForIdle()

        onNodeWithText("UPCOMING NEAR YOU").assertExists()
        assertEquals(0, onAllNodes(hasText("HOMEOUT PRESENTS")).fetchSemanticsNodes().size)
        onNodeWithText("Food").assertIsSelected()
        onNodeWithTag("search").assertTextEquals("Petiscos")
    }
}
