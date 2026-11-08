package app.homeout.domain

/** An event poster's colors as ARGB, e.g. 0xFF8E4754. [content] is the text drawn on [background]. */
data class PosterColors(
    val background: Long,
    val content: Long,
)
