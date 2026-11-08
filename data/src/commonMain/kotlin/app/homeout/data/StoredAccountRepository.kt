package app.homeout.data

import app.homeout.domain.Account
import app.homeout.domain.AccountRepository

/**
 * Accounts and the signed-in session, as keys in a [KeyValueStore]. Emails arrive normalized.
 * It keeps no copy of its own, so it always reads what the store holds.
 */
class StoredAccountRepository(private val store: KeyValueStore) : AccountRepository {

    override suspend fun find(email: String): Account? =
        store.getString(nameKey(email))?.let { Account(it, email) }

    override suspend fun add(account: Account, passwordHash: String) {
        store.putString(nameKey(account.email), account.name)
        store.putString(passwordKey(account.email), passwordHash)
    }

    override suspend fun passwordHash(email: String): String? = store.getString(passwordKey(email))

    override fun session(): String? = store.getString(SESSION)

    override suspend fun signIn(email: String) = store.putString(SESSION, email)

    private fun nameKey(email: String) = "account.$email.name"

    private fun passwordKey(email: String) = "account.$email.password"

    private companion object {
        const val SESSION = "session.email"
    }
}
