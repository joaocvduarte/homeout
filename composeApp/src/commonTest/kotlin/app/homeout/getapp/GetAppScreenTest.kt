package app.homeout.getapp

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import app.homeout.designsystem.HoTheme
import app.homeout.event.sampleDetail
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class GetAppScreenTest {

    private fun screen(darkTheme: Boolean) = runComposeUiTest {
        val detail = sampleDetail("fado-old-cistern")
        setContent { HoTheme(darkTheme = darkTheme) { GetAppScreen(detail, TimeZone.of("Europe/Lisbon"), onBack = {}) } }
        onNodeWithText("GET THE APP").assertExists()
        onNodeWithText("Tickets live in the homeout app").assertExists()
        onNodeWithText("Download the app to buy tickets for this event and keep each ticket's QR code on your phone.").assertExists()
        onNodeWithText("Fado at the Old Cistern").assertExists()
    }

    @Test
    fun showsTheEventTheHeadlineAndTheCopyInLight() = screen(darkTheme = false)

    @Test
    fun showsTheEventTheHeadlineAndTheCopyInDark() = screen(darkTheme = true)

    @Test
    fun bothBadgesAreNamedAndClickableButGoNowhere() = runComposeUiTest {
        var backs = 0
        val detail = sampleDetail("fado-old-cistern")
        setContent { HoTheme(darkTheme = true) { GetAppScreen(detail, TimeZone.UTC, onBack = { backs++ }) } }

        onNodeWithTag("badge-app-store").assertHasClickAction().performClick()
        onNodeWithTag("badge-google-play").assertHasClickAction().performClick()

        onNodeWithContentDescription("Download on the App Store").assertExists()
        onNodeWithContentDescription("Get it on Google Play").assertExists()
        onNodeWithTag("getapp-page").assertExists()
        assertEquals(0, backs, "a badge doesn't navigate")
    }

    @Test
    fun theBackButtonGoesBack() = runComposeUiTest {
        var backs = 0
        val detail = sampleDetail("fado-old-cistern")
        setContent { HoTheme(darkTheme = false) { GetAppScreen(detail, TimeZone.UTC, onBack = { backs++ }) } }
        onNodeWithContentDescription("Back to event").performClick()
        assertEquals(1, backs)
    }
}
