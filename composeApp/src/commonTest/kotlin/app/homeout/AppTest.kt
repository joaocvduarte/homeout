package app.homeout

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class AppTest {
    @Test
    fun appOpensOnHomeInLisbonTime() = runComposeUiTest {
        val clock = object : Clock {
            override fun now() = Instant.parse("2026-11-13T19:00:00Z")
        }
        setContent { App(clock = clock, timeZone = TimeZone.of("Europe/Lisbon")) }
        waitForIdle()
        onNodeWithText("HOMEOUT").assertExists()
        onNodeWithText("TONIGHT · FRI 13 NOV").assertExists()
        onNodeWithText("Music · Alfama · 21:30").assertExists()
    }
}
