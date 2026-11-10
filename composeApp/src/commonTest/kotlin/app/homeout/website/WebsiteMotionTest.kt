package app.homeout.website

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class WebsiteMotionTest {

    private fun assertNear(expected: Float, actual: Float, what: String) = assertEquals(expected, actual, 0.001f, what)

    @Test
    fun heroSinksShrinksAndFadesOverAnEightOfAViewport() {
        val start = heroFrame(0f, 800f)
        assertNear(0f, start.translateY, "translateY at 0")
        assertNear(1f, start.scale, "scale at 0")
        assertNear(1f, start.alpha, "alpha at 0")

        val end = heroFrame(640f, 800f)
        assertNear(120f, end.translateY, "translateY at 0.8 viewport")
        assertNear(0.92f, end.scale, "scale at 0.8 viewport")
        assertNear(0.1f, end.alpha, "alpha at 0.8 viewport")

        assertEquals(end, heroFrame(5000f, 800f), "it stops at the end")
        assertNear(60f, heroFrame(320f, 800f).translateY, "halfway")
    }

    @Test
    fun showcaseStartsAbove40PercentAndPlaysBackBelow70PercentAndHoldsBetween() {
        assertEquals(false, showcaseOn(wasOn = false, top = 800f, viewportH = 1000f), "far below")
        assertEquals(false, showcaseOn(wasOn = false, top = 400f, viewportH = 1000f), "at 40% it has not started")
        assertEquals(true, showcaseOn(wasOn = false, top = 399f, viewportH = 1000f), "past 40% it starts")
        assertEquals(true, showcaseOn(wasOn = true, top = 700f, viewportH = 1000f), "at 70% it keeps playing")
        assertEquals(false, showcaseOn(wasOn = true, top = 701f, viewportH = 1000f), "past 70% it plays back")
        assertEquals(false, showcaseOn(wasOn = false, top = 600f, viewportH = 1000f), "between the two it stays off")
        assertEquals(true, showcaseOn(wasOn = true, top = 600f, viewportH = 1000f), "between the two it stays on")
    }

    @Test
    fun theShowcaseStartsStackedAndPlaysEachPieceOnItsOwnClock() = runComposeUiTest {
        mainClock.autoAdvance = false
        var on by mutableStateOf(false)
        lateinit var frame: ShowcaseFrame
        setContent { frame = rememberShowcaseFrame(on, reduceMotion = false) }
        assertNear(0.84f, frame.panelScale, "panel before")
        assertNear(48f, frame.panelRadius, "radius before")
        assertNear(170f, frame.posterRise, "rise before")
        assertNear(0f, frame.copyAlpha, "copy before")
        assertNear(30f, frame.copyOffset, "copy offset before")
        assertEquals(FanCard(0f, 0f, 0f), frame.fan(1f), "stacked before")

        on = true
        mainClock.advanceTimeBy(16)
        mainClock.advanceTimeBy(500)
        assertTrue(frame.panelScale > 0.84f, "the panel is already opening at 500 ms")
        assertNear(0f, frame.copyAlpha, "the copy waits for 800 ms")
        assertEquals(FanCard(0f, 0f, 0f), frame.fan(1f), "the posters wait for 650 ms")

        mainClock.advanceTimeBy(2000)
        assertNear(1f, frame.panelScale, "panel after")
        assertNear(0f, frame.panelRadius, "radius after")
        assertNear(0f, frame.posterRise, "rise after")
        assertNear(1f, frame.copyAlpha, "copy after")
        assertNear(0f, frame.copyOffset, "copy offset after")
        assertEquals(FanCard(92f, 22f, 9f), frame.fan(1f))
        assertEquals(FanCard(-92f, 22f, -9f), frame.fan(-1f))
        assertEquals(FanCard(0f, 0f, 0f), frame.fan(0f))
    }

    @Test
    fun reducedMotionIsTheFinalFrameEverywhere() = runComposeUiTest {
        assertEquals(HeroFrame(0f, 1f, 1f), heroFrame(640f, 800f, reduceMotion = true))
        lateinit var frame: ShowcaseFrame
        setContent { frame = rememberShowcaseFrame(on = true, reduceMotion = true) }
        assertNear(1f, frame.panelScale, "panel")
        assertNear(1f, frame.copyAlpha, "copy")
        assertEquals(FanCard(92f, 22f, 9f), frame.fan(1f))
    }

    @Test
    fun aRevealedItemFadesInOver700MsAfterItsDelayAndNotBefore() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            val scroll = rememberScrollState()
            CompositionLocalProvider(LocalSiteMotion provides SiteMotion(scroll, viewportPx = 1000f, reduce = false)) {
                // Its top (10) is inside the viewport, so it is revealed from the first frame, 110 ms late.
                Box(Modifier.size(100.dp).reveal(delayMillis = 110).testTag("item"))
            }
        }
        mainClock.advanceTimeBy(100)
        assertEquals("hidden", state("item"), "still hidden before its delay")
        mainClock.advanceTimeBy(300)
        assertEquals("hidden", state("item"), "not finished at 400 ms")
        mainClock.advanceTimeBy(500)
        assertEquals("revealed", state("item"), "finished by 900 ms")
    }

    @Test
    fun anItemOutsideTheViewportStaysHiddenUntilItsTopPassesNinetyTwoPercent() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            val scroll = rememberScrollState()
            CompositionLocalProvider(LocalSiteMotion provides SiteMotion(scroll, viewportPx = 100f, reduce = false)) {
                // Placed 500 down while the viewport is 100 high.
                Box(Modifier.size(100.dp, 700.dp)) {
                    Box(Modifier.padding(top = 500.dp).size(100.dp).reveal().testTag("item"))
                }
            }
        }
        mainClock.advanceTimeBy(2000)
        assertEquals("hidden", state("item"), "far below the fold")
    }

    @Test
    fun withReducedMotionAnItemIsNeverHidden() = runComposeUiTest {
        mainClock.autoAdvance = false
        setContent {
            val scroll = rememberScrollState()
            CompositionLocalProvider(LocalSiteMotion provides SiteMotion(scroll, viewportPx = 100f, reduce = true)) {
                Box(Modifier.size(100.dp).reveal().testTag("item"))
            }
        }
        assertNull(state("item"), "no hiding state at all on the first frame")
    }

    private fun androidx.compose.ui.test.ComposeUiTest.state(tag: String): String? =
        onNodeWithTag(tag).fetchSemanticsNode().config.getOrNull(SemanticsProperties.StateDescription)
}
