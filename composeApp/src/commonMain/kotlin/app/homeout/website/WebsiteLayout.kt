package app.homeout.website

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** CSS `clamp(min px, vw vw, max px)` for a page [width]: the design's fluid sizes. */
internal fun clampDp(min: Float, vw: Float, max: Float, width: Dp): Dp = (width.value * vw / 100f).coerceIn(min, max).dp

/** The design's breakpoints for a page [width] by [height] (the viewport). */
internal class WebsiteLayout(val width: Dp, val height: Dp) {
    /** Space on each side of the content: `clamp(16px, 4vw, 48px)`. */
    val gutter: Dp get() = clampDp(16f, 4f, 48f, width)

    /** Event cards per row: 4 above 1100, else 2. Up to 640 the cards form one scrolling row instead. */
    val eventColumns: Int get() = if (width > 1100.dp) 4 else 2

    val eventsScroll: Boolean get() = width <= 640.dp

    /** Steps per row: 3 above 900, else 1. */
    val stepColumns: Int get() = if (width > 900.dp) 3 else 1

    /** The nav's section links hide at 760 and below. */
    val navLinks: Boolean get() = width > 760.dp

    /** The poster fan shrinks to 0.8 at 520 and below. */
    val fanScale: Float get() = if (width <= 520.dp) 0.8f else 1f

    fun clamp(min: Float, vw: Float, max: Float): Dp = clampDp(min, vw, max, width)

    companion object {
        val ContentMax = 1200.dp
        val NavHeight = 72.dp
    }
}
