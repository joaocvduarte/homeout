package app.homeout.website

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
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
 * The design's motion. The hero follows the scroll through a pure function, so it tests without pixels;
 * the showcase plays once its section comes into view. With `reduceMotion` every piece is at its final, still frame.
 */

private fun clamp01(v: Float) = v.coerceIn(0f, 1f)

/** The hero content's transform, in dp and fractions. */
@Immutable
internal data class HeroFrame(val translateY: Float, val scale: Float, val alpha: Float)

/** Scrolling one 0.8 viewport sinks the hero 120, shrinks it to 0.92 and fades it to 0.1. */
internal fun heroFrame(scrollY: Float, viewportH: Float, reduceMotion: Boolean = false): HeroFrame {
    if (reduceMotion) return HeroFrame(0f, 1f, 1f)
    val h = clamp01(scrollY / (viewportH * 0.8f))
    return HeroFrame(translateY = h * 120f, scale = 1f - h * 0.08f, alpha = 1f - h * 0.9f)
}

/**
 * Whether the showcase is playing. It starts once its top rises above 40% of the viewport and plays
 * back when its top drops below 70% again; between the two it keeps its state, so it can't flicker at an edge.
 */
internal fun showcaseOn(wasOn: Boolean, top: Float, viewportH: Float): Boolean =
    if (wasOn) top <= viewportH * 0.7f else top < viewportH * 0.4f

/** One poster of the fan, in dp and degrees. */
@Immutable
internal data class FanCard(val dx: Float, val dy: Float, val rotation: Float)

/**
 * The showcase's pieces, each easing between its start and its final frame on its own clock, as the
 * design's CSS transitions do. The values are read inside `graphicsLayer` lambdas, so nothing recomposes per frame.
 */
@Stable
internal class ShowcaseFrame(
    private val panel: State<Float>,
    private val stage: State<Float>,
    private val cards: State<Float>,
    private val copyFade: State<Float>,
    private val copyRise: State<Float>,
) {
    /** From 0.84 to full width. */
    val panelScale: Float get() = 0.84f + 0.16f * panel.value
    val panelRadius: Float get() = 48f * (1f - panel.value)
    val posterRise: Float get() = 170f * (1f - stage.value)
    val copyAlpha: Float get() = copyFade.value
    val copyOffset: Float get() = 30f * (1f - copyRise.value)

    /** The poster at offset [k] from the middle one (-1, 0, 1): ±92 apart, ±9° tilted, the sides 22 lower. */
    fun fan(k: Float): FanCard = FanCard(dx = k * 92f * cards.value, dy = kotlin.math.abs(k) * 22f * cards.value, rotation = k * 9f * cards.value)
}

private val panelEasing = CubicBezierEasing(0.7f, 0f, 0.2f, 1f)
private val riseEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
private val cssEase = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

/** The panel opens over 1100 ms, the posters rise (1000 ms after 250) and fan out (900 after 650), the copy arrives (700 after 800). Reduced motion jumps to the final frame. */
@Composable
internal fun rememberShowcaseFrame(on: Boolean, reduceMotion: Boolean): ShowcaseFrame {
    val target = if (on) 1f else 0f
    fun spec(duration: Int, delay: Int, easing: Easing): AnimationSpec<Float> =
        if (reduceMotion) snap() else tween(duration, delay, easing)
    val panel = animateFloatAsState(target, spec(1100, 0, panelEasing), label = "showcasePanel")
    val stage = animateFloatAsState(target, spec(1000, 250, riseEasing), label = "showcaseStage")
    val cards = animateFloatAsState(target, spec(900, 650, riseEasing), label = "showcaseCards")
    val copyFade = animateFloatAsState(target, spec(700, 800, cssEase), label = "showcaseCopyFade")
    val copyRise = animateFloatAsState(target, spec(700, 800, riseEasing), label = "showcaseCopyRise")
    return remember(panel, stage, cards, copyFade, copyRise) { ShowcaseFrame(panel, stage, cards, copyFade, copyRise) }
}

/** Whether the showcase is on, from where its top sits in the viewport. Without motion, or with reduced motion, always on. */
@Composable
internal fun rememberShowcaseOn(motion: SiteMotion?, top: () -> Int): Boolean {
    var on by remember { mutableStateOf(motion == null || motion.reduce) }
    if (motion != null && !motion.reduce) {
        LaunchedEffect(motion) {
            snapshotFlow { top() to motion.scroll.value }.collect { (anchor, scrolled) ->
                // The anchor is 0 until the page has been laid out; the showcase is never first.
                if (anchor > 0) on = showcaseOn(on, (anchor - scrolled).toFloat(), motion.viewportPx)
            }
        }
    }
    return on
}

/** What scroll-driven motion needs from the page: the scroll, the viewport, and whether to hold still. */
internal class SiteMotion(val scroll: androidx.compose.foundation.ScrollState, val viewportPx: Float, val reduce: Boolean)

internal val LocalSiteMotion = staticCompositionLocalOf<SiteMotion?> { null }
