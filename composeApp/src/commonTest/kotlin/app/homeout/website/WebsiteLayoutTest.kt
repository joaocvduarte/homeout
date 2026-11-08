package app.homeout.website

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WebsiteLayoutTest {

    private fun layout(width: Int) = WebsiteLayout(width.dp, 800.dp)

    @Test
    fun eventsUseFourColumnsAbove1100TwoAbove640AndARowBelow() {
        assertEquals(4, layout(1280).eventColumns)
        assertEquals(2, layout(1100).eventColumns)
        assertEquals(2, layout(900).eventColumns)
        assertFalse(layout(900).eventsScroll)
        assertFalse(layout(641).eventsScroll)
        assertTrue(layout(640).eventsScroll)
        assertTrue(layout(375).eventsScroll)
    }

    @Test
    fun stepsUseThreeColumnsAbove900AndOneBelow() {
        assertEquals(3, layout(1280).stepColumns)
        assertEquals(1, layout(900).stepColumns)
        assertEquals(1, layout(375).stepColumns)
    }

    @Test
    fun navLinksShowAbove760() {
        assertTrue(layout(761).navLinks)
        assertFalse(layout(760).navLinks)
        assertFalse(layout(375).navLinks)
    }

    @Test
    fun fanShrinksTo80PercentAt520AndBelow() {
        assertEquals(1f, layout(521).fanScale)
        assertEquals(0.8f, layout(520).fanScale)
    }

    @Test
    fun clampStopsAtItsBounds() {
        assertEquals(16.dp, clampDp(16f, 4f, 48f, 100.dp))
        assertEquals(40.dp, clampDp(16f, 4f, 48f, 1000.dp))
        assertEquals(48.dp, clampDp(16f, 4f, 48f, 5000.dp))
        assertEquals(16.dp, layout(375).gutter)
        assertEquals(48.dp, layout(1280).gutter)
    }
}
