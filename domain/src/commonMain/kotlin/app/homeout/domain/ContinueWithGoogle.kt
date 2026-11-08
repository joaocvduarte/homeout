package app.homeout.domain

/** The fake Google sign-in: a session for a placeholder email, with no account stored. */
class ContinueWithGoogle(private val accounts: AccountRepository) {
    suspend operator fun invoke() = accounts.signIn(GOOGLE_EMAIL)

    companion object {
        const val GOOGLE_EMAIL = "google@homeout.local"
    }
}
