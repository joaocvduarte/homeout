package app.homeout.data

import app.homeout.domain.Account
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class StoredAccountRepositoryTest {
    private val store = InMemoryKeyValueStore()
    private val repo = StoredAccountRepository(store)

    @Test
    fun startsWithNoAccountAndNoSession() = runTest {
        assertNull(repo.find("ana@x.pt"))
        assertNull(repo.passwordHash("ana@x.pt"))
        assertNull(repo.session())
    }

    @Test
    fun readsBackWhatItStored() = runTest {
        repo.add(Account("Ana", "ana@x.pt"), "the-hash")
        repo.signIn("ana@x.pt")
        assertEquals(Account("Ana", "ana@x.pt"), repo.find("ana@x.pt"))
        assertEquals("the-hash", repo.passwordHash("ana@x.pt"))
        assertEquals("ana@x.pt", repo.session())
    }

    @Test
    fun aNewRepositoryOverTheSameStoreSeesTheSessionAndAccounts() = runTest {
        repo.add(Account("Ana", "ana@x.pt"), "the-hash")
        repo.signIn("ana@x.pt")
        val relaunched = StoredAccountRepository(store)
        assertEquals("ana@x.pt", relaunched.session())
        assertEquals(Account("Ana", "ana@x.pt"), relaunched.find("ana@x.pt"))
    }

    @Test
    fun keepsNoCopyOfItsOwn() = runTest {
        repo.add(Account("Ana", "ana@x.pt"), "the-hash")
        repo.signIn("ana@x.pt")
        val cleared = InMemoryKeyValueStore()
        assertNull(StoredAccountRepository(cleared).session())
        assertTrue(store.getString("session.email") != null)
    }

    @Test
    fun theAccountOfOneEmailIsNotFoundUnderAnother() = runTest {
        repo.add(Account("Ana", "ana@x.pt"), "the-hash")
        assertNull(repo.find("bia@x.pt"))
        assertFalse(repo.passwordHash("bia@x.pt") != null)
    }
}
