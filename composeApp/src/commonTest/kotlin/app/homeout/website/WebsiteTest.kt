package app.homeout.website

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoTheme
import app.homeout.event.friday
import app.homeout.withKoin
import kotlinx.datetime.TimeZone
import org.koin.compose.viewmodel.koinViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class WebsiteTest {

    private val lisbon = TimeZone.of("Europe/Lisbon")
    private val ids = listOf("fado-old-cistern", "rooftop-vinyl", "night-market-river", "petiscos-tasting")

    /** [dark] wraps the page in an outer theme, to prove the website ignores it. */
    private fun ComposeUiTest.show(width: Int, height: Int, dark: Boolean? = null, reduceMotion: Boolean = false) = setContent {
        val page: @Composable () -> Unit = {
            Box(Modifier.wrapContentSize(Alignment.TopStart, unbounded = true).size(width.dp, height.dp)) { Website(koinViewModel(), lisbon, reduceMotion) }
        }
        if (dark == null) page() else HoTheme(darkTheme = dark, content = page)
    }

    /** Positions are unclipped: a node below the viewport reports no bounds, but still has a position. */
    private fun ComposeUiTest.top(tag: String) = onNodeWithTag(tag).fetchSemanticsNode().positionInRoot.y
    private fun ComposeUiTest.left(tag: String) = onNodeWithTag(tag).fetchSemanticsNode().positionInRoot.x
    private fun ComposeUiTest.navLink(text: String) =
        onAllNodes(hasText(text) and hasAnyAncestor(hasTestTag("website-nav"))).onFirst()

    private fun ComposeUiTest.homeLink() = onNode(hasContentDescription("homeout home"))
    private fun ComposeUiTest.exists(text: String) = onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()

    @Test
    fun sectionsAppearInTheDesignsOrderWithItsCopy() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(1280, 820)
            val tops = listOf("section-top", "section-showcase", "section-events", "section-how", "section-app", "website-footer").map { top(it) }
            assertEquals(tops.sorted(), tops, "sections out of order: $tops")
            listOf(
                "Head out tonight.",
                "Fado in candlelit cisterns, vinyl on the rooftops, night markets by the river. " +
                    "homeout finds what's on in Lisbon and keeps every ticket in your pocket.",
                "Download the app", "See what's on", "SCROLL",
                "ONE APP", "EVERY NIGHT OUT IN ONE PLACE.",
                "Browse tonight's events, buy tickets in a few taps, and walk in with the QR code on your phone.",
                "THIS WEEK IN LISBON", "NEXT EVENTS", "See all 4 events",
                "From “what's on?” to through the door in three taps.",
                "01", "Find your night", "02", "Buy in seconds", "03", "Walk straight in",
                "HEAD OUT\nTONIGHT",
                "Free on iPhone and Android. Log in with your Google account or create one in a minute.",
                "Get it for iPhone", "Get it for Android", "Made in Lisbon · © 2026",
            ).forEach { assertTrue(exists(it), "missing copy: $it") }
        }
    }

    @Test
    fun nextEventsShowFourCardsInOneRowWithEachLabel() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(1280, 820)
            val tops = ids.map { top("website-event-$it") }
            assertEquals(1, tops.toSet().size, "the four cards share a row: $tops")
            listOf(
                "Tonight", "Saturday", "FADO AT THE OLD CISTERN", "FRI 13 NOV · 21:30", "ALFAMA",
                "Music · 21:30", "€18 per person", "Food · 18:00", "Free with a ticket",
            ).forEach { assertTrue(exists(it), "missing label: $it") }
            ids.forEach { onNodeWithTag("website-tickets-$it").assertExists() }
        }
    }

    @Test
    fun navLinksScrollToTheirSection() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(1280, 820)
            navLink("Next events").performClick()
            waitForIdle()
            onNodeWithTag("section-events").assertTopPositionInRootIsEqualTo(0.dp)

            navLink("How it works").performClick()
            waitForIdle()
            onNodeWithTag("section-how").assertTopPositionInRootIsEqualTo(0.dp)

            navLink("Get the app").performClick()
            waitForIdle()
            onNodeWithTag("section-app").assertIsDisplayed()
        }
    }

    @Test
    fun ticketDownloadAndSeeAllActionsReachGetTheApp() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(1000, 700) // the test window is 1024 wide: every tapped control must lie inside it
            // The cards sit below the fold of the 768 high test window, so their buttons are activated
            // through their click action rather than at a screen position.
            val actions: List<() -> SemanticsNodeInteraction> = listOf(
                { onNodeWithTag("website-tickets-fado-old-cistern") },
                { onNodeWithTag("website-tickets-petiscos-tasting") },
                { onNodeWithTag("website-see-all") },
                { onNodeWithText("Get it for iPhone") },
                { onNodeWithText("Download the app") },
                { onNodeWithTag("nav-get-app") },
            )
            actions.forEachIndexed { index, action ->
                homeLink().performClick()
                waitForIdle()
                onNodeWithTag("section-app").assertIsNotDisplayed()
                action().performSemanticsAction(SemanticsActions.OnClick)
                waitForIdle()
                assertTrue(
                    top("section-app") < 700f,
                    "action $index did not reach Get the app; it is at ${top("section-app")}",
                )
            }

            homeLink().performClick()
            waitForIdle()
            onNodeWithText("See what's on").performClick()
            waitForIdle()
            onNodeWithTag("section-events").assertTopPositionInRootIsEqualTo(0.dp)
        }
    }

    @Test
    fun tabletWidthUsesTwoEventColumnsOneStepColumnAndKeepsTheNavLinks() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(900, 820)
            assertEquals(top("website-event-fado-old-cistern"), top("website-event-rooftop-vinyl"))
            assertTrue(
                top("website-event-night-market-river") > top("website-event-fado-old-cistern"),
                "second row: ${top("website-event-night-market-river")} vs ${top("website-event-fado-old-cistern")}",
            )
            val find = onNodeWithText("Find your night").fetchSemanticsNode().positionInRoot
            val buy = onNodeWithText("Buy in seconds").fetchSemanticsNode().positionInRoot
            assertEquals(find.x, buy.x, "steps stack in one column")
            assertTrue(buy.y > find.y, "stacked: $buy vs $find")
            navLink("Next events").assertExists()
        }
    }

    @Test
    fun phoneWidthHidesNavLinksKeepsThePillAndScrollsEventsSideways() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(375, 740)
            onAllNodes(hasText("Next events") and hasAnyAncestor(hasTestTag("website-nav"))).assertCountEquals(0)
            onAllNodes(hasText("How it works") and hasAnyAncestor(hasTestTag("website-nav"))).assertCountEquals(0)
            onNodeWithTag("nav-get-app").assertIsDisplayed()
            onNodeWithTag("website-event-fado-old-cistern").assertExists()
            assertTrue(
                onAllNodes(hasTestTag("website-event-petiscos-tasting")).fetchSemanticsNodes().isEmpty() ||
                    left("website-event-petiscos-tasting") > 375f,
                "later cards sit off screen in the scrolling row",
            )
            val find = onNodeWithText("Find your night").fetchSemanticsNode().positionInRoot
            val buy = onNodeWithText("Buy in seconds").fetchSemanticsNode().positionInRoot
            assertEquals(find.x, buy.x, "steps are stacked")
        }
    }

    @Test
    fun footerKeepsEventsAndHowItWorksOnly() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(1280, 820)
            val inFooter = hasAnyAncestor(hasTestTag("website-footer"))
            onNode(hasText("Events") and inFooter).assertExists()
            onNode(hasText("How it works") and inFooter).assertExists()
            onAllNodesWithText("Terms", substring = true, ignoreCase = true).assertCountEquals(0)
            onAllNodesWithText("Privacy", substring = true, ignoreCase = true).assertCountEquals(0)
        }
    }

    /** The hero's background, the nav pill's fill and the events section's background, as drawn. */
    private fun ComposeUiTest.drawnColors(dark: Boolean): List<Color> {
        show(400, 700, dark)
        val density = density.density
        val pill = onNodeWithTag("nav-get-app").fetchSemanticsNode().boundsInRoot
        val top = onNodeWithTag("website").captureToImage().toPixelMap()
        val pillFill = top[(pill.left + 6 * density).toInt(), pill.center.y.toInt()]
        // Below the flowers (320 dp) and left of the content: nothing but the page color.
        val heroBackground = top[(8 * density).toInt(), (600 * density).toInt()]
        onNodeWithText("See what's on").performSemanticsAction(SemanticsActions.OnClick)
        waitForIdle()
        val events = onNodeWithTag("website").captureToImage().toPixelMap()
        return listOf(heroBackground, pillFill, events[(8 * density).toInt(), (400 * density).toInt()])
    }

    @Test
    fun theLookIsTheSameInALightAndADarkSetting() = runComposeUiTest {
        val expected = listOf(WebsiteColors.page, WebsiteColors.pill, WebsiteColors.lightSection)
        withKoin(friday, lisbon, signedIn = false) { assertEquals(expected, drawnColors(dark = false)) }
    }

    @Test
    fun theLookIsTheSameInADarkSetting() = runComposeUiTest {
        val expected = listOf(WebsiteColors.page, WebsiteColors.pill, WebsiteColors.lightSection)
        withKoin(friday, lisbon, signedIn = false) { assertEquals(expected, drawnColors(dark = true)) }
    }

    private fun ComposeUiTest.navState() =
        onNodeWithTag("website-nav").fetchSemanticsNode().config.getOrNull(SemanticsProperties.StateDescription)

    @Test
    fun theNavTurnsTranslucentOncePastTwentyFourDpOfScroll() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            show(1000, 700)
            assertEquals("top", navState())
            onNodeWithText("See what's on").performSemanticsAction(SemanticsActions.OnClick)
            waitForIdle()
            assertEquals("scrolled", navState())
            homeLink().performClick()
            waitForIdle()
            assertEquals("top", navState())
        }
    }

    @Test
    fun withReducedMotionEverythingIsShownAtOnceAndAnchorsJump() = runComposeUiTest {
        withKoin(friday, lisbon, signedIn = false) {
            mainClock.autoAdvance = false
            show(1000, 700, reduceMotion = true)
            mainClock.advanceTimeByFrame()
            // Nothing is hidden on the first frame, so the cards carry no hiding state.
            ids.forEach { id ->
                assertTrue(
                    onNodeWithTag("website-event-$id").fetchSemanticsNode().config.getOrNull(SemanticsProperties.StateDescription) != "hidden",
                    "$id is hidden",
                )
            }
            onNodeWithText("See what's on").performSemanticsAction(SemanticsActions.OnClick)
            mainClock.advanceTimeByFrame()
            mainClock.advanceTimeByFrame()
            onNodeWithTag("section-events").assertTopPositionInRootIsEqualTo(0.dp)
            assertEquals("scrolled", navState())
        }
    }
}
