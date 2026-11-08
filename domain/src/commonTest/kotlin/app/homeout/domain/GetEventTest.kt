package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

class GetEventTest {

    private val fado = testEvent("fado", "Fado at the Old Cistern", Category.MUSIC, listOf("Inês"), "Alfama", Instant.parse("2026-11-13T21:30:00Z"))
    private val petiscos = testEvent("petiscos", "Petiscos Tasting Night", Category.FOOD, listOf("Joana"), "Campo de Ourique", Instant.parse("2026-11-14T20:00:00Z"))
    private val started = testEvent("started", "Afternoon Jazz", Category.MUSIC, listOf("Trio"), "Alfama", Instant.parse("2026-11-13T16:00:00Z"))

    private val getEvent = GetEvent(
        repository = object : EventRepository {
            override suspend fun events() = listOf(fado, petiscos, started)
        },
        clock = object : Clock {
            override fun now() = Instant.parse("2026-11-13T19:00:00Z")
        },
        timeZone = TimeZone.UTC,
    )

    @Test
    fun tonightsEventIsToday() = runTest {
        assertEquals(EventDetail(fado, isToday = true), getEvent("fado"))
    }

    @Test
    fun tomorrowsEventIsNotToday() = runTest {
        assertEquals(EventDetail(petiscos, isToday = false), getEvent("petiscos"))
    }

    @Test
    fun anEventThatHasStartedIsStillFound() = runTest {
        assertEquals(EventDetail(started, isToday = true), getEvent("started"))
    }

    @Test
    fun unknownIdIsNull() = runTest {
        assertNull(getEvent("unknown"))
    }
}
