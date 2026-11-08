package app.homeout.website

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** WCAG 2.1 AA for every text and background pair the website uses. */
class WebsiteColorsTest {

    private data class Check(val name: String, val text: Color, val background: Color, val large: Boolean = false)

    private val pairs = listOf(
        Check("nav links and hero title", WebsiteColors.ink, WebsiteColors.page),
        Check("hero intro and scroll hint", WebsiteColors.muted, WebsiteColors.page),
        Check("get the app copy", WebsiteColors.muted, WebsiteColors.page),
        Check("wordmark", WebsiteColors.ink, WebsiteColors.brand, large = true),
        Check("pill", WebsiteColors.pillInk, WebsiteColors.pill),
        Check("outline pill", WebsiteColors.ink, WebsiteColors.page),
        Check("showcase copy", WebsiteColors.darkInk, WebsiteColors.panel),
        Check("events headline", WebsiteColors.darkInk, WebsiteColors.lightSection),
        Check("events price", WebsiteColors.lightMuted, WebsiteColors.lightSection),
        Check("events kicker label", WebsiteColors.kicker, WebsiteColors.lightSection),
        Check("see all pill", WebsiteColors.lightSection, WebsiteColors.darkInk),
        Check("footer", WebsiteColors.muted, WebsiteColors.footer),
        Check("get tickets pill", WebsiteColors.pillInk, WebsiteColors.pill),
    ) + WebsiteColors.steps.mapIndexed { i, (background, ink) -> Check("step ${i + 1}", ink, background) }

    @Test
    fun everyPairMeetsAA() {
        val failures = pairs.mapNotNull {
            val ratio = ratio(it.text, it.background)
            val needed = if (it.large) 3.0 else 4.5
            if (ratio < needed) "${it.name} = $ratio (needs $needed)" else null
        }
        assertTrue(failures.isEmpty(), "pairs below AA: $failures")
    }

    @Test
    fun theRatioMatchesKnownValues() {
        assertEquals(21.0, ratio(Color.Black, Color.White), 0.01)
        assertEquals(1.0, ratio(Color.White, Color.White), 0.01)
    }

    private fun ratio(a: Color, b: Color): Double {
        val la = luminance(a)
        val lb = luminance(b)
        return (max(la, lb) + 0.05) / (min(la, lb) + 0.05)
    }

    private fun luminance(c: Color): Double {
        fun channel(v: Float): Double = if (v <= 0.04045f) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        return 0.2126 * channel(c.red) + 0.7152 * channel(c.green) + 0.0722 * channel(c.blue)
    }
}
