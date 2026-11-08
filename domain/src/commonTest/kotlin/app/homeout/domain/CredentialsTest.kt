package app.homeout.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CredentialsTest {
    @Test
    fun emailsNeedALocalPartAnAtAndADottedDomain() {
        assertTrue(Credentials.isValidEmail("a@b.c"))
        assertTrue(Credentials.isValidEmail("  maya@example.com "))
        assertFalse(Credentials.isValidEmail("maya@example"))
        assertFalse(Credentials.isValidEmail("a b@c.d"))
        assertFalse(Credentials.isValidEmail(""))
    }

    @Test
    fun passwordsNeedEightCharacters() {
        assertTrue(Credentials.isValidPassword("12345678"))
        assertFalse(Credentials.isValidPassword("1234567"))
        assertFalse(Credentials.isValidPassword(""))
    }

    @Test
    fun emailsAreTrimmedAndLowercased() = assertEquals("ana@x.pt", Credentials.normalizeEmail("  Ana@X.pt "))
}
