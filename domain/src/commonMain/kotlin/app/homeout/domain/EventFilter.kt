package app.homeout.domain

/** [category] null means every category. A blank [query] matches every event. */
data class EventFilter(
    val category: Category? = null,
    val query: String = "",
)
