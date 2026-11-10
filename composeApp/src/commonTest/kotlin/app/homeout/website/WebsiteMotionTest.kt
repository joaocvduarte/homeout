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
    fun theStageStartsOnTheFirstSwipeDownAndPlaysBackOnlyNearTheTop() {
        val vh = 1000f
        assertEquals(false, stageOn(false, direction = 0, scrolled = 0f, viewportH = vh), "at rest at the top")
        assertEquals(false, stageOn(false, direction = 10, scrolled = 4f, viewportH = vh), "4 px is not a swipe yet")
        assertEquals(true, stageOn(false, direction = 10, scrolled = 5f, viewportH = vh), "a swipe down starts it")
        assertEquals(true, stageOn(false, direction = -10, scrolled = 501f, viewportH = vh), "past half a viewport it starts whichever way it moves")
        assertEquals(false, stageOn(false, direction = -10, scrolled = 500f, viewportH = vh), "at half, going up, it stays off")
        assertEquals(true, stageOn(true, direction = 10, scrolled = 100f, viewportH = vh), "going on down it keeps playing")
        assertEquals(true, stageOn(true, direction = -10, scrolled = 900f, viewportH = vh), "up, but not yet within 90%")
        assertEquals(false, stageOn(true, direction = -10, scrolled = 899f, viewportH = vh), "up within 90% it plays back")
        assertEquals(true, stageOn(true, direction = 0, scrolled = 100f, viewportH = vh), "still, it holds")
    }

    @Test
    fun theStageStartsOnTheFullScreenPosterAndPlaysEachPieceOnItsOwnClock() = runComposeUiTest {
        mainClock.autoAdvance = false
        var on by mutableStateOf(false)
        lateinit var frame: StageFrame
        setContent { frame = rememberStageFrame(on, reduceMotion = false) }
        assertNear(0f, frame.shrink, "poster before")
        assertNear(1f, frame.frameAlpha, "frame before")
        assertNear(1f, frame.floraAlpha, "flowers before")
        assertNear(1f, frame.copyAlpha, "words before")
        assertNear(0f, frame.copyOffset, "words offset before")
        assertNear(0f, frame.sideAlpha, "side cards before")
        assertNear(70f, frame.sideShift, "side shift before")
        assertNear(0.9f, frame.sideScale, "side scale before")
        assertNear(0f, frame.headAlpha, "heading before")
        assertNear(28f, frame.headOffset, "heading offset before")

        on = true
        mainClock.advanceTimeBy(16)
        mainClock.advanceTimeBy(300)
        assertTrue(frame.shrink > 0f, "the poster is already shrinking at 300 ms")
        assertTrue(frame.copyAlpha < 1f, "the words are already fading")
        assertNear(0f, frame.sideAlpha, "the side cards wait for 350 ms")
        assertNear(0f, frame.headAlpha, "the heading waits for 500 ms")

        mainClock.advanceTimeBy(2000)
        assertNear(1f, frame.shrink, "poster after")
        assertNear(0f, frame.frameAlpha, "frame after")
        assertNear(0f, frame.floraAlpha, "flowers after")
        assertNear(0f, frame.copyAlpha, "words after")
        assertNear(-70f, frame.copyOffset, "words offset after")
        assertTrue(frame.copyGone, "words gone")
        assertNear(1f, frame.sideAlpha, "side cards after")
        assertNear(0f, frame.sideShift, "side shift after")
        assertNear(1f, frame.sideScale, "side scale after")
        assertNear(1f, frame.headAlpha, "heading after")
        assertNear(0f, frame.headOffset, "heading offset after")
    }

    @Test
    fun reducedMotionJumpsToTheFinalFrame() = runComposeUiTest {
        lateinit var frame: StageFrame
        setContent { frame = rememberStageFrame(on = true, reduceMotion = true) }
        assertNear(1f, frame.shrink, "poster")
        assertNear(1f, frame.headAlpha, "heading")
        assertNear(1f, frame.sideAlpha, "side cards")
        assertNear(0f, frame.copyAlpha, "words")
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
