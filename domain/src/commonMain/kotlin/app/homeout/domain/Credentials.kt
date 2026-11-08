package app.homeout.domain

/** The rules an email and a password must meet. */
object Credentials {
    const val MIN_PASSWORD_LENGTH = 8

    private val email = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun normalizeEmail(raw: String): String = raw.trim().lowercase()

    fun isValidEmail(raw: String): Boolean = email.matches(raw.trim())

    fun isValidPassword(raw: String): Boolean = raw.length >= MIN_PASSWORD_LENGTH
}
