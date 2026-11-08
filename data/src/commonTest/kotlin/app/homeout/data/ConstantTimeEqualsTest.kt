package app.homeout.data

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConstantTimeEqualsTest {
    @Test
    fun equalArraysMatch() = assertTrue(constantTimeEquals(byteArrayOf(1, 2, 3), byteArrayOf(1, 2, 3)))

    @Test
    fun aDifferenceAnywhereDoesNot() {
        assertFalse(constantTimeEquals(byteArrayOf(1, 2, 3), byteArrayOf(9, 2, 3)))
        assertFalse(constantTimeEquals(byteArrayOf(1, 2, 3), byteArrayOf(1, 2, 9)))
    }

    @Test
    fun differentLengthsDoNot() {
        assertFalse(constantTimeEquals(byteArrayOf(1, 2), byteArrayOf(1, 2, 3)))
        assertFalse(constantTimeEquals(byteArrayOf(), byteArrayOf(0)))
    }
}
