package app.homeout.data

import app.homeout.domain.Category
import app.homeout.domain.Price
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

class SampleEventRepositoryTest {

    private fun repository(now: String) = SampleEventRepository(
        clock = object : Clock {
            override fun now() = Instant.parse(now)
        },
        timeZone = TimeZone.UTC,
    )

    @Test
    fun holdsTheDesignEvents() = runTest {
        val events = repository("2026-11-14T19:00:00Z").events()

        assertEquals(
            listOf(
                Row("Fado at the Old Cistern", Category.MUSIC, "Alfama", "2026-11-14T21:30:00Z", Price.Euros(1800)),
                Row("Rooftop Vinyl Session", Category.MUSIC, "Bairro Alto", "2026-11-14T22:00:00Z", Price.Free),
                Row("Night Market by the River", Category.FOOD, "Cais do Sodré", "2026-11-15T18:00:00Z", Price.Free),
                Row("Petiscos Tasting Night", Category.FOOD, "Campo de Ourique", "2026-11-15T20:00:00Z", Price.Euros(2500)),
            ),
            events.map { Row(it.title, it.category, it.venue, it.startsAt.toString(), it.price) },
        )
    }

    @Test
    fun everyEventHasArtistsAndAUniqueId() = runTest {
        val events = repository("2026-11-14T19:00:00Z").events()

        assertTrue(events.all { it.artists.isNotEmpty() })
        assertEquals(events.size, events.map { it.id }.toSet().size)
    }

    @Test
    fun datesFollowTheClock() = runTest {
        val events = repository("2027-03-02T08:00:00Z").events()

        assertEquals(
            listOf("2027-03-02T21:30:00Z", "2027-03-02T22:00:00Z", "2027-03-03T18:00:00Z", "2027-03-03T20:00:00Z"),
            events.map { it.startsAt.toString() },
        )
    }

    private data class Row(val title: String, val category: Category, val venue: String, val startsAt: String, val price: Price)
}
