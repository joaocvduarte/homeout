package app.homeout.website

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * The design's scroll script as pure functions of scroll progress, so they test without pixels.
 * With `reduceMotion` every function returns the final, still frame.
 */

private fun clamp01(v: Float) = v.coerceIn(0f, 1f)

/** The design's cubic ease-out: `1 - (1 - t)^3`. */
internal fun easeOut(t: Float): Float {
    val r = 1f - clamp01(t)
    return 1f - r * r * r
}

/** The hero content's transform, in dp and fractions. */
@Immutable
internal data class HeroFrame(val translateY: Float, val scale: Float, val alpha: Float)

/** Scrolling one 0.8 viewport sinks the hero 120, shrinks it to 0.92 and fades it to 0.1. */
internal fun heroFrame(scrollY: Float, viewportH: Float, reduceMotion: Boolean = false): HeroFrame {
    if (reduceMotion) return HeroFrame(0f, 1f, 1f)
    val h = clamp01(scrollY / (viewportH * 0.8f))
    return HeroFrame(translateY = h * 120f, scale = 1f - h * 0.08f, alpha = 1f - h * 0.9f)
}

/** How far through the showcase's pinned stretch the page is: 0 at its top, 1 once only the last viewport is left. */
internal fun showcaseProgress(scrollY: Float, sectionTop: Float, viewportH: Float): Float =
    clamp01((scrollY - sectionTop) / (viewportH * 1.4f).coerceAtLeast(1f))

/** One poster of the fan, in dp and degrees. */
@Immutable
internal data class FanCard(val dx: Float, val dy: Float, val rotation: Float)

@Immutable
internal data class ShowcaseFrame(
    val panelScale: Float,
    val panelRadius: Float,
    val posterRise: Float,
    val copyAlpha: Float,
    val copyOffset: Float,
    private val fanProgress: Float,
) {
    /** The poster at offset [k] from the middle one (-1, 0, 1): ±92 apart, ±9° tilted, the sides 22 lower. */
    fun fan(k: Float): FanCard = FanCard(dx = k * 92f * fanProgress, dy = kotlin.math.abs(k) * 22f * fanProgress, rotation = k * 9f * fanProgress)
}

/** The panel grows from 0.84 to full width, the posters rise and fan out, and the copy fades in. */
internal fun showcaseFrame(progress: Float, reduceMotion: Boolean = false): ShowcaseFrame {
    val p = if (reduceMotion) 1f else clamp01(progress)
    val grow = easeOut(clamp01(p / 0.45f))
    val rise = easeOut(clamp01(p / 0.6f))
    val fan = easeOut(clamp01((p - 0.2f) / 0.5f))
    val copy = clamp01((p - 0.25f) / 0.35f)
    return ShowcaseFrame(
        panelScale = 0.84f + 0.16f * grow,
        panelRadius = 48f * (1f - grow),
        posterRise = 140f * (1f - rise),
        copyAlpha = copy,
        copyOffset = 30f * (1f - copy),
        fanProgress = fan,
    )
}

/** What scroll-driven motion needs from the page: the scroll, the viewport, and whether to hold still. */
internal class SiteMotion(val scroll: androidx.compose.foundation.ScrollState, val viewportPx: Float, val reduce: Boolean)

internal val LocalSiteMotion = staticCompositionLocalOf<SiteMotion?> { null }
