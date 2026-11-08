package app.homeout.domain

interface AccountRepository {
    suspend fun find(email: String): Account?

    suspend fun add(account: Account, passwordHash: String)

    suspend fun passwordHash(email: String): String?

    /** The email of whoever is signed in, or null. Read at once so the first screen is known without waiting. */
    fun session(): String?

    suspend fun signIn(email: String)
}
