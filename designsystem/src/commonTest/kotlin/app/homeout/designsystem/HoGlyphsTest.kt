package app.homeout.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HoGlyphsTest {

    private fun ImageVector.paths() = root.iterator().asSequence().filterIsInstance<VectorPath>().toList()

    private val singleColor = mapOf(
        "Ticket" to HoGlyphs.Ticket,
        "Minus" to HoGlyphs.Minus,
        "Plus" to HoGlyphs.Plus,
        "ChevronLeft" to HoGlyphs.ChevronLeft,
        "ChevronRight" to HoGlyphs.ChevronRight,
        "CheckCircle" to HoGlyphs.CheckCircle,
        "QrMini" to HoGlyphs.QrMini,
        "Phone" to HoGlyphs.Phone,
        "Card" to HoGlyphs.Card,
        "Wallet" to HoGlyphs.Wallet,
        "Account" to HoGlyphs.Account,
        "AlertCircle" to HoGlyphs.AlertCircle,
    )

    @Test
    fun everyGlyphIsTheDesigns24GridAt24dp() {
        val tabs = listOf(
            HoGlyphs.home(Color.Black, Color.Transparent),
            HoGlyphs.tickets(Color.Black, Color.Transparent),
            HoGlyphs.profile(Color.Black, Color.Transparent),
        )
        (singleColor.values + tabs).forEach {
            assertEquals(24f, it.viewportWidth, it.name)
            assertEquals(24f, it.viewportHeight, it.name)
            assertEquals(24.dp, it.defaultWidth, it.name)
            assertTrue(it.paths().isNotEmpty(), it.name)
        }
    }

    @Test
    fun singleColorGlyphsAreUnfilledStrokesForTinting() {
        singleColor.forEach { (name, glyph) ->
            glyph.paths().forEach {
                assertNull(it.fill, name)
                assertEquals(SolidColor(Color.Black), it.stroke, name)
            }
        }
    }

    @Test
    fun tabGlyphsTakeTheirStrokeAndFill() {
        val stroke = Color(0xFF335C57)
        val fill = Color(0xFF4F817A).copy(alpha = 0.12f)
        listOf(HoGlyphs.home(stroke, fill), HoGlyphs.tickets(stroke, fill), HoGlyphs.profile(stroke, fill)).forEach { glyph ->
            glyph.paths().forEach {
                assertEquals(SolidColor(stroke), it.stroke, glyph.name)
                assertEquals(SolidColor(fill), it.fill, glyph.name)
            }
        }
    }

    @Test
    fun ticketCarriesItsDashedStub() {
        assertEquals(2, HoGlyphs.Ticket.paths().size)
    }
}
