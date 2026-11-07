package app.homeout.event

import androidx.compose.ui.graphics.Color
import app.homeout.data.SampleEventRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertTrue

/** WCAG 2.1: the poster's small mono labels need 4.5:1 against the poster color. */
class PosterContrastTest {

    @Test
    fun everyPosterMeetsAA() = runTest {
        val failures = SampleEventRepository(friday, TimeZone.UTC).events().mapNotNull { event ->
            val ratio = ratio(Color(event.poster.content), Color(event.poster.background))
            if (ratio < 4.5) "${event.id} = $ratio" else null
        }
        assertTrue(failures.isEmpty(), "posters below 4.5:1: $failures")
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
