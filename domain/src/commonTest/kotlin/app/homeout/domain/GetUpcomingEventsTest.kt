package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant

class GetUpcomingEventsTest {

    private val now = Instant.parse("2026-11-14T19:00:00Z")

    private val fado = event("fado", "Fado at the Old Cistern", Category.MUSIC, "Alfama", "2026-11-14T21:30:00Z", "Ana Moura")
    private val vinyl = event("vinyl", "Rooftop Vinyl Session", Category.MUSIC, "Bairro Alto", "2026-11-14T22:00:00Z", "DJ Lúcia")
    private val market = event("market", "Night Market by the River", Category.FOOD, "Cais do Sodré", "2026-11-15T18:00:00Z", "Chef Rui")
    private val petiscos = event("petiscos", "Petiscos Tasting Night", Category.FOOD, "Campo de Ourique", "2026-11-15T20:00:00Z", "Chef Marta")
    private val finished = event("finished", "Afternoon Jazz", Category.MUSIC, "Alfama", "2026-11-14T16:00:00Z", "Trio Sul")

    private fun useCase(vararg events: Event) = GetUpcomingEvents(
        repository = object : EventRepository {
            override suspend fun events() = events.toList()
        },
        clock = object : Clock {
            override fun now() = now
        },
        timeZone = TimeZone.UTC,
    )

    private val all = useCase(petiscos, vinyl, market, fado, finished)

    @Test
    fun noFilterReturnsUpcomingEventsSortedAndGroupedByDay() = runTest {
        val groups = all()

        assertEquals(listOf(LocalDate(2026, 11, 14), LocalDate(2026, 11, 15)), groups.map { it.date })
        assertEquals(listOf(true, false), groups.map { it.isToday })
        assertEquals(listOf(fado, vinyl), groups[0].events)
        assertEquals(listOf(market, petiscos), groups[1].events)
    }

    @Test
    fun categoryFilterKeepsOnlyThatCategory() = runTest {
        assertEquals(listOf(fado, vinyl), all(EventFilter(category = Category.MUSIC)).flatMap { it.events })
        assertEquals(listOf(market, petiscos), all(EventFilter(category = Category.FOOD)).flatMap { it.events })
    }

    @Test
    fun queryMatchesTitleVenueOrArtist() = runTest {
        assertEquals(listOf(fado), all(EventFilter(query = "fado")).flatMap { it.events })
        assertEquals(listOf(market), all(EventFilter(query = "Cais do")).flatMap { it.events })
        assertEquals(listOf(petiscos), all(EventFilter(query = "Marta")).flatMap { it.events })
    }

    @Test
    fun queryIgnoresCaseAndSurroundingSpaces() = runTest {
        assertEquals(listOf(vinyl), all(EventFilter(query = "  dj lÚCIA ")).flatMap { it.events })
    }

    @Test
    fun categoryAndQueryCombine() = runTest {
        assertTrue(all(EventFilter(category = Category.FOOD, query = "fado")).isEmpty())
        assertEquals(
            listOf(petiscos),
            all(EventFilter(category = Category.FOOD, query = "petiscos")).flatMap { it.events },
        )
    }

    @Test
    fun blankQueryIsNoQuery() = runTest {
        assertEquals(all(), all(EventFilter(query = "   ")))
    }

    @Test
    fun eventsThatAlreadyStartedAreExcluded() = runTest {
        assertFalse(all().flatMap { it.events }.contains(finished))
    }

    @Test
    fun noMatchReturnsEmptyList() = runTest {
        assertTrue(all(EventFilter(query = "opera")).isEmpty())
        assertTrue(useCase()().isEmpty())
    }

    private fun event(
        id: String,
        title: String,
        category: Category,
        venue: String,
        startsAt: String,
        artist: String,
    ) = Event(id, title, category, listOf(artist), venue, Instant.parse(startsAt), Price.Free)
}
