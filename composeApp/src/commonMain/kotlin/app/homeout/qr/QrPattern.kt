package app.homeout.qr

/**
 * The design's placeholder QR pattern (Home Screen page, `qrPath`), ported as is: 25×25 modules
 * seeded from the ticket code with FNV-1a and xorshift, with three finders, an alignment square
 * and timing rows. Real, scannable codes come with the backend.
 */
object QrPattern {
    const val SIZE = 25

    /** Rows of modules, top to bottom; true is dark. */
    fun modules(code: String): List<BooleanArray> {
        var h = 2166136261u
        for (c in code) {
            h = h xor c.code.toUInt()
            h *= 16777619u
        }
        if (h == 0u) h = 1u
        fun random(): Double {
            h = h xor (h shl 13)
            h = h xor (h shr 17)
            h = h xor (h shl 5)
            return h.toDouble() / 4294967296.0
        }
        val finders = listOf(0 to 0, SIZE - 7 to 0, 0 to SIZE - 7)
        return List(SIZE) { y ->
            BooleanArray(SIZE) { x ->
                var dark = false
                var reserved = false
                for ((ox, oy) in finders) {
                    if (x in ox - 1..ox + 7 && y in oy - 1..oy + 7) {
                        reserved = true
                        val dx = x - ox
                        val dy = y - oy
                        if (dx in 0..6 && dy in 0..6) {
                            dark = dx == 0 || dx == 6 || dy == 0 || dy == 6 || (dx in 2..4 && dy in 2..4)
                        }
                    }
                }
                if (!reserved && x in 16..20 && y in 16..20) {
                    reserved = true
                    dark = x == 16 || x == 20 || y == 16 || y == 20 || (x == 18 && y == 18)
                }
                if (!reserved) {
                    dark = when {
                        y == 6 -> x % 2 == 0
                        x == 6 -> y % 2 == 0
                        else -> random() < 0.48
                    }
                }
                dark
            }
        }
    }
}
