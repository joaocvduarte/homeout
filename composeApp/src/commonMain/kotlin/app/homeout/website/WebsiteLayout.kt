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

    private val phone: Boolean get() = width <= 640.dp

    /** The stage's middle card: `clamp(180px, 21vw, 300px)` wide and 1.3 times as high. */
    val cardWidth: Dp get() = clampDp(180f, 21f, 300f, width)
    val cardHeight: Dp get() = cardWidth * 1.3f

    /** Where the middle card's center sits: 60% down the viewport, 72% up to 640. */
    val cardCenterY: Dp get() = height * if (phone) 0.72f else 0.6f

    /** The gap between the middle card and the side cards. */
    val cardGap: Dp get() = if (phone) 14.dp else 24.dp

    /** How far above the middle card its heading starts. */
    val headHeight: Dp get() = if (phone) 170.dp else 210.dp

    fun clamp(min: Float, vw: Float, max: Float): Dp = clampDp(min, vw, max, width)

    companion object {
        val ContentMax = 1200.dp
        val NavHeight = 72.dp
    }
}
