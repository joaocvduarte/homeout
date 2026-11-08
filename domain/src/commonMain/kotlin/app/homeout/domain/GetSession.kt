package app.homeout.domain

/** The email of whoever is signed in, or null. */
class GetSession(private val accounts: AccountRepository) {
    operator fun invoke(): String? = accounts.session()
}
