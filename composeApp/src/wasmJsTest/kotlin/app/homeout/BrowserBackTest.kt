package app.homeout

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import kotlinx.browser.window
import kotlinx.datetime.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class, ExperimentalBrowserHistoryApi::class)
class BrowserBackTest {

    private val clock = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    /** Hands control back to the browser so it can deliver popstate; waitUntil alone would starve it. */
    private suspend fun browserTick() = suspendCoroutine { cont ->
        window.setTimeout({ cont.resume(Unit); null }, 50)
    }

    private suspend fun ComposeUiTest.eventually(what: String, condition: () -> Boolean) {
        repeat(60) {
            waitForIdle()
            if (condition()) return
            browserTick()
        }
        assertTrue(condition(), "$what did not happen within 3 s")
    }

    private fun ComposeUiTest.has(text: String) = onAllNodes(hasText(text)).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun browserBackReturnsHome() = runComposeUiTest { withKoin(clock, TimeZone.of("Europe/Lisbon")) { browserBack() } }

    private suspend fun ComposeUiTest.browserBack() {
        setContent {
            App(onNavHostReady = { it.bindToBrowserNavigation() })
        }
        eventually("Home shows") { has("UPCOMING NEAR YOU") }
        val homeHash = window.location.hash

        onNodeWithTag("event-fado-old-cistern").performClick()
        eventually("the event page opens") { has("HOMEOUT PRESENTS") }
        eventually("the URL names the event") { window.location.hash != homeHash }
        val eventHash = window.location.hash

        window.history.back()
        eventually("browser back shows Home") { has("UPCOMING NEAR YOU") && !has("HOMEOUT PRESENTS") }
        assertNotEquals(eventHash, window.location.hash, "the URL leaves the event")
    }
}
