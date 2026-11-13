package app.homeout.website

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The design's motion. The stage (hero into next events) plays once the page is swiped and plays
 * back when it is swiped up again; each piece eases on its own clock, like the design's CSS
 * transitions. With `reduceMotion` every piece jumps to its final frame.
 */

/**
 * Whether the stage is playing. One swipe down starts it, and scrolling back up to within 90% of a
 * viewport from the top plays it back. [direction] is the scroll since the last frame and [scrolled]
 * how far the page has gone down.
 */
internal fun stageOn(wasOn: Boolean, direction: Int, scrolled: Float, viewportH: Float): Boolean =
    if (wasOn) !(direction < 0 && scrolled < viewportH * 0.9f)
    else (direction > 0 || scrolled > viewportH * 0.5f) && scrolled > 4f

private val panelEasing = CubicBezierEasing(0.7f, 0f, 0.2f, 1f)
private val riseEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
private val cssEase = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

/**
 * The stage's pieces between their first and final frame, 0 to 1. The values are read inside
 * `graphicsLayer` lambdas, so nothing recomposes per frame.
 */
@Stable
internal class StageFrame(
    private val clip: State<Float>,
    private val frame: State<Float>,
    private val flora: State<Float>,
    private val copyFade: State<Float>,
    private val copyLift: State<Float>,
    private val sideFade: State<Float>,
    private val sideSlide: State<Float>,
    private val headFade: State<Float>,
    private val headRise: State<Float>,
) {
    /** The poster shrinking from the whole screen into the middle card. */
    val shrink: Float get() = clip.value
    val frameAlpha: Float get() = 1f - frame.value
    val floraAlpha: Float get() = 1f - flora.value
    val copyAlpha: Float get() = 1f - copyFade.value

    /** The hero words lift 70 dp away. */
    val copyOffset: Float get() = -70f * copyLift.value
    val copyGone: Boolean get() = copyLift.value >= 1f
    val sideAlpha: Float get() = sideFade.value

    /** The side cards start 70 dp closer to the middle and 0.9 the size. */
    val sideShift: Float get() = 70f * (1f - sideSlide.value)
    val sideScale: Float get() = 0.9f + 0.1f * sideSlide.value
    val headAlpha: Float get() = headFade.value
    val headOffset: Float get() = 28f * (1f - headRise.value)
}

@Composable
private fun phase(on: Boolean, reduceMotion: Boolean, duration: Int, delay: Int, easing: Easing): State<Float> =
    animateFloatAsState(
        targetValue = if (on) 1f else 0f,
        animationSpec = if (reduceMotion) snap() else tween(duration, delay, easing),
        label = "stage",
    )

/** The clip opens over 1100 ms, the words lift away (500 fade, 800 lift), the side cards arrive after 300 ms and the heading after 450 ms. */
@Composable
internal fun rememberStageFrame(on: Boolean, reduceMotion: Boolean): StageFrame {
    val clip = phase(on, reduceMotion, 1100, 0, panelEasing)
    val frame = phase(on, reduceMotion, 400, 0, cssEase)
    val flora = phase(on, reduceMotion, 700, 0, cssEase)
    val copyFade = phase(on, reduceMotion, 500, 0, cssEase)
    val copyLift = phase(on, reduceMotion, 800, 0, panelEasing)
    val sideFade = phase(on, reduceMotion, 600, 350, cssEase)
    val sideSlide = phase(on, reduceMotion, 900, 300, riseEasing)
    val headFade = phase(on, reduceMotion, 600, 500, cssEase)
    val headRise = phase(on, reduceMotion, 800, 450, riseEasing)
    return remember(clip, frame, flora, copyFade, copyLift, sideFade, sideSlide, headFade, headRise) {
        StageFrame(clip, frame, flora, copyFade, copyLift, sideFade, sideSlide, headFade, headRise)
    }
}

/** Whether the stage is on, from the scroll's direction and distance. Outside the website it stays off. */
@Composable
internal fun rememberStageOn(motion: SiteMotion?): Boolean {
    var on by remember { mutableStateOf(false) }
    if (motion != null) {
        LaunchedEffect(motion) {
            var last = motion.scroll.value
            snapshotFlow { motion.scroll.value }.collect { y ->
                on = stageOn(on, y - last, y.toFloat(), motion.viewportPx)
                last = y
            }
        }
    }
    return on
}

/** What scroll-driven motion needs from the page: the scroll, the viewport, and whether to hold still. */
internal class SiteMotion(val scroll: androidx.compose.foundation.ScrollState, val viewportPx: Float, val reduce: Boolean)

internal val LocalSiteMotion = staticCompositionLocalOf<SiteMotion?> { null }
