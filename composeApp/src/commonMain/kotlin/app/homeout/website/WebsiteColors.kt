package app.homeout.website

import androidx.compose.ui.graphics.Color

/**
 * The website's single look (homeout Website page), the same in a light or dark setting.
 * It lives here, not in the design system, so the phone app's colors can't change.
 */
internal object WebsiteColors {
    val page = Color(0xFF283432)
    val ink = Color(0xFFE7ECEB)
    val muted = Color(0xFFB3BDBB)
    val brand = Color(0xFF64837E)
    val panel = Color(0xFF70A89F)
    val lightSection = Color(0xFFE0EBE9)
    val darkInk = Color(0xFF0F1817)
    val lightMuted = Color(0xFF4A5654)
    val kicker = Color(0xFF8E4754)
    val pill = Color(0xFFC9F2EB)
    val pillInk = Color(0xFF284D48)
    val footer = Color(0xFF1F2927)

    /** The three steps of How it works: background and ink. */
    val steps = listOf(page to ink, panel to darkInk, pill to pillInk)
}
