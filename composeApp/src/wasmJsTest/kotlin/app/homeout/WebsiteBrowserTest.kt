package app.homeout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import app.homeout.website.Website
import kotlinx.browser.window
import kotlinx.datetime.TimeZone
import org.koin.compose.viewmodel.koinViewModel
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalTestApi::class)
class WebsiteBrowserTest {

    private val clock = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    private fun addressWith(fragment: String) =
        window.history.replaceState(null, "", window.location.pathname + window.location.search + "#" + fragment)

    private fun cleanUrl() = window.history.replaceState(null, "", window.location.pathname + window.location.search)

    @Test
    fun aRestoredEventAddressStillShowsTheWebsite() = runComposeUiTest {
        withKoin(clock, TimeZone.of("Europe/Lisbon"), signedIn = false) {
            cleanUrl()
            addressWith("app.homeout.EventDestination/fado-old-cistern")
            setContent { Box(Modifier.size(1280.dp, 820.dp)) { Website(koinViewModel(), TimeZone.of("Europe/Lisbon")) } }
            onNodeWithTag("website").assertExists()
            assertTrue(onAllNodes(hasText("HOMEOUT PRESENTS")).fetchSemanticsNodes().isEmpty(), "no event page")
            assertTrue(onAllNodes(hasText("GET THE APP")).fetchSemanticsNodes().isEmpty(), "no Get the app screen")
            cleanUrl()
        }
    }
}
