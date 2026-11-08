package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CreateAccountTest {
    private val repo = FakeAccounts()
    private val create = CreateAccount(repo, PrefixHasher)

    @Test
    fun createsTheAccountStoresAHashAndSignsIn() = runTest {
        val result = create("  Ana ", "Ana@X.pt ", "12345678", true)
        assertEquals(CreateAccountResult.Created(Account("Ana", "ana@x.pt")), result)
        assertEquals("hashed:12345678", repo.passwordHash("ana@x.pt"))
        assertEquals("ana@x.pt", repo.session())
    }

    @Test
    fun anExistingEmailInAnyCaseIsTakenAndNothingChanges() = runTest {
        create("Ana", "ana@x.pt", "12345678", true)
        repo.signedIn = null
        assertEquals(CreateAccountResult.EmailTaken, create("Other", "ANA@x.pt", "87654321", true))
        assertEquals(1, repo.accounts.size)
        assertEquals("hashed:12345678", repo.passwordHash("ana@x.pt"))
        assertNull(repo.session())
    }

    @Test
    fun invalidInputIsRejectedAndNothingIsStored() = runTest {
        assertFailsWith<IllegalArgumentException> { create("", "a@b.pt", "12345678", true) }
        assertFailsWith<IllegalArgumentException> { create("Ana", "a@b", "12345678", true) }
        assertFailsWith<IllegalArgumentException> { create("Ana", "a@b.pt", "1234567", true) }
        assertFailsWith<IllegalArgumentException> { create("Ana", "a@b.pt", "12345678", false) }
        assertTrue(repo.accounts.isEmpty())
    }
}
