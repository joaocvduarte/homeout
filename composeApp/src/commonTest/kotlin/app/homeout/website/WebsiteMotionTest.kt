package app.homeout.website

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.CompositionLocalProvider
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
    fun showcasePanelGrowsToFullWidthInTheFirstHalf() {
        assertNear(0.84f, showcaseFrame(0f).panelScale, "scale at 0")
        assertNear(48f, showcaseFrame(0f).panelRadius, "radius at 0")
        assertNear(1f, showcaseFrame(0.45f).panelScale, "scale at 0.45")
        assertNear(0f, showcaseFrame(0.45f).panelRadius, "radius at 0.45")
        assertNear(1f, showcaseFrame(1f).panelScale, "scale at 1")
    }

    @Test
    fun postersRiseAndFanOutAndTheCopyFadesIn() {
        val start = showcaseFrame(0f)
        assertNear(140f, start.posterRise, "rise at 0")
        assertNear(0f, start.copyAlpha, "copy at 0")
        assertNear(30f, start.copyOffset, "copy offset at 0")
        assertEquals(FanCard(0f, 0f, 0f), start.fan(1f), "stacked at 0")

        val end = showcaseFrame(1f)
        assertNear(0f, end.posterRise, "rise at 1")
        assertNear(1f, end.copyAlpha, "copy at 1")
        assertNear(0f, end.copyOffset, "copy offset at 1")
        assertEquals(FanCard(92f, 22f, 9f), end.fan(1f))
        assertEquals(FanCard(-92f, 22f, -9f), end.fan(-1f))
        assertEquals(FanCard(0f, 0f, 0f), end.fan(0f))

        val mid = showcaseFrame(0.45f)
        assertNear(1f - (1f - 0.45f / 0.6f).let { it * it * it }, 1f - mid.posterRise / 140f, "rise eases out")
        assertNear(0.2f / 0.35f, mid.copyAlpha, "copy at 0.45")
    }

    @Test
    fun progressRunsOverTheSectionsPinnedStretch() {
        assertNear(0f, showcaseProgress(500f, 1000f, 800f), "before")
        assertNear(0.5f, showcaseProgress(1560f, 1000f, 800f), "halfway")
        assertNear(1f, showcaseProgress(9000f, 1000f, 800f), "after")
    }

    @Test
    fun reducedMotionIsTheFinalFrameEverywhere() {
        assertEquals(HeroFrame(0f, 1f, 1f), heroFrame(640f, 800f, reduceMotion = true))
        assertEquals(showcaseFrame(1f), showcaseFrame(0f, reduceMotion = true))
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
