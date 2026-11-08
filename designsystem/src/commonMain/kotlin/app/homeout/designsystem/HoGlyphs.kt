package app.homeout.designsystem

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The design's own 24×24 stroke icons (Home Screen page), as opposed to the Phosphor set in
 * [HoIcons]. Single-color glyphs are drawn in black for tinting; the tab glyphs take their
 * stroke and fill, since the active tab fills its icon with a second color.
 */
object HoGlyphs {
    private const val THIN = 1.6f
    private const val REGULAR = 1.8f
    private const val BOLD = 2f

    /** The ticket outline: the tab's shape. */
    private const val TICKET = "M3.5 7.5h17v3a2 2 0 0 0 0 3v3h-17v-3a2 2 0 0 0 0-3z"

    /** The design's dashed stub (dash 1.5, gap 2), drawn as its three dashes. */
    private const val TICKET_STUB = "M14.5 7.5v1.5M14.5 11v1.5M14.5 14.5v1.5"

    fun home(stroke: Color, fill: Color): ImageVector =
        glyph("home", REGULAR, stroke, fill, "M4 10.5L12 4l8 6.5V20h-5.5v-5.5h-5V20H4z")

    fun tickets(stroke: Color, fill: Color): ImageVector = glyph("tickets", REGULAR, stroke, fill, TICKET)

    fun profile(stroke: Color, fill: Color): ImageVector =
        glyph("profile", REGULAR, stroke, fill, "M8 8.5a4 4 0 1 0 8 0a4 4 0 1 0 -8 0z", "M4.5 20a7.5 7.5 0 0 1 15 0z")

    /** Buy tickets and the empty Tickets tab. */
    val Ticket: ImageVector by lazy { glyph("ticket", REGULAR, Color.Black, null, TICKET, TICKET_STUB) }

    val Minus: ImageVector by lazy { glyph("minus", BOLD, Color.Black, null, "M6 12h12") }

    val Plus: ImageVector by lazy { glyph("plus", BOLD, Color.Black, null, "M6 12h12", "M12 6v12") }

    val ChevronLeft: ImageVector by lazy { glyph("chevron-left", BOLD, Color.Black, null, "M15 6l-6 6 6 6") }

    val ChevronRight: ImageVector by lazy { glyph("chevron-right", BOLD, Color.Black, null, "M9 6l6 6-6 6") }

    val CheckCircle: ImageVector by lazy {
        glyph("check-circle", BOLD, Color.Black, null, "M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0z", "M8 12.5l2.7 2.7L16 9.8")
    }

    /** The mark on the Google sign-in buttons: a head and shoulders in a circle. */
    val Account: ImageVector by lazy {
        glyph("account", REGULAR, Color.Black, null, "M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0z", "M9 10a3 3 0 1 0 6 0a3 3 0 1 0 -6 0z", "M6.5 18.2a6.5 6.5 0 0 1 11 0")
    }

    /** The mark on an error alert: an exclamation in a circle. */
    val AlertCircle: ImageVector by lazy {
        glyph("alert-circle", BOLD, Color.Black, null, "M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0z", "M12 7.5v5.5", "M12 16.5h.01")
    }

    /** The small QR mark on each ticket row. */
    val QrMini: ImageVector by lazy {
        glyph(
            "qr-mini", THIN, Color.Black, null,
            "M4 4h6v6H4zM14 4h6v6h-6zM4 14h6v6H4z",
            "M14 14h2.5v2.5H14zM17.5 17.5H20V20h-2.5zM14 19.5h1.5M19.5 14v1.5",
        )
    }

    val Phone: ImageVector by lazy {
        glyph(
            "phone", REGULAR, Color.Black, null,
            "M9 2.5h6a2 2 0 0 1 2 2v15a2 2 0 0 1-2 2H9a2 2 0 0 1-2-2v-15a2 2 0 0 1 2-2z",
            "M11 18.5h2",
        )
    }

    val Card: ImageVector by lazy {
        glyph(
            "card", REGULAR, Color.Black, null,
            "M4.5 5.5h15a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-15a2 2 0 0 1-2-2v-9a2 2 0 0 1 2-2z",
            "M2.5 9.5h19",
            "M6 15h4",
        )
    }

    val Wallet: ImageVector by lazy {
        glyph(
            "wallet", REGULAR, Color.Black, null,
            "M4 7.5h14.5a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5.5a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h11",
            "M16 13.5h1.5",
        )
    }

    private fun glyph(name: String, width: Float, stroke: Color, fill: Color?, vararg paths: String): ImageVector =
        ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
            .apply {
                paths.forEach { d ->
                    addPath(
                        pathData = PathParser().parsePathString(d).toNodes(),
                        fill = fill?.let(::SolidColor),
                        stroke = SolidColor(stroke),
                        strokeLineWidth = width,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    )
                }
            }
            .build()
}
