package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class CountEventsThisWeekTest {

    private fun count(now: String, vararg startsAt: String) = CountEventsThisWeek(
        repository = object : EventRepository {
            override suspend fun events() = startsAt.mapIndexed { i, start ->
                testEvent("$i", "Event $i", Category.MUSIC, listOf("Artist"), "Venue", Instant.parse(start))
            }
        },
        clock = object : Clock {
            override fun now() = Instant.parse(now)
        },
        timeZone = TimeZone.UTC,
    )

    @Test
    fun countsUpcomingEventsUntilSunday() = runTest {
        val friday = "2026-11-13T19:00:00Z"
        assertEquals(
            2,
            count(
                friday,
                "2026-11-13T21:30:00Z",
                "2026-11-15T23:59:00Z",
                "2026-11-16T00:00:00Z",
                "2026-11-13T10:00:00Z",
            )(),
        )
    }

    @Test
    fun onSundayOnlyTodayCounts() = runTest {
        assertEquals(1, count("2026-11-15T08:00:00Z", "2026-11-15T20:00:00Z", "2026-11-16T18:00:00Z")())
    }

    @Test
    fun noEventsCountsZero() = runTest {
        assertEquals(0, count("2026-11-13T19:00:00Z")())
    }
}
