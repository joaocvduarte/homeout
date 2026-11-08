package app.homeout.domain

sealed interface Price {
    data object Free : Price

    data class Euros(val cents: Long) : Price {
        init {
            require(cents > 0) { "A paid price must be positive; use Free for zero." }
        }
    }
}
