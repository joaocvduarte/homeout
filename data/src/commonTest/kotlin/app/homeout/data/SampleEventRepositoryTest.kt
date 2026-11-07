package app.homeout.data

import app.homeout.domain.Category
import app.homeout.domain.EventIcon
import app.homeout.domain.PosterColors
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

    @Test
    fun holdsTheDesignPageDetails() = runTest {
        val events = repository("2026-11-13T19:00:00Z").events()

        assertEquals(
            listOf(
                Details("Cisterna do Largo", "2026-11-13T21:00:00Z", null, PosterColors(0xFF8E4754, 0xFFFBE9EC)),
                Details("Terraço Calçada", null, "2026-11-14T02:00:00Z", PosterColors(0xFF335C57, 0xFFC9F2EB)),
                Details("Praça do Cais", null, "2026-11-14T23:30:00Z", PosterColors(0xFFF69DAB, 0xFF283432)),
                Details("Tasca da Esquina", null, "2026-11-14T22:30:00Z", PosterColors(0xFFC9F2EB, 0xFF284D48)),
            ),
            events.map { Details(it.venueName, it.doorsAt?.toString(), it.endsAt?.toString(), it.poster) },
        )
        assertEquals(
            listOf(
                "An intimate night of fado inside a 17th-century water cistern beneath Alfama. Three fadistas, a Portuguese guitar and a viola, with about 80 seats lit by candles. The show runs around 90 minutes with one interval; drinks are served at the bar upstairs.",
                "Local selectors play soul, Brazilian funk and disco from original records on a rooftop over the Tejo. Entry is free with a ticket, but space is limited, so arrive before 23:00.",
                "Forty stalls of street food, natural wine and local makers along the river, with live sets from 20:00. Entry is free; reserve a ticket to use the fast lane at the gate.",
                "Seven small plates from the neighbourhood’s best tascas, each paired with a Portuguese wine. Seated at shared tables of eight; a vegetarian menu is available on request.",
            ),
            events.map { it.description },
        )
    }

    @Test
    fun eachEventHasTheDesignIcon() = runTest {
        assertEquals(
            listOf(EventIcon.MUSIC_NOTES, EventIcon.VINYL_RECORD, EventIcon.FORK_KNIFE, EventIcon.WINE),
            repository("2026-11-13T19:00:00Z").events().map { it.icon },
        )
    }

    private data class Details(val venueName: String, val doorsAt: String?, val endsAt: String?, val poster: PosterColors)

    private data class Row(val title: String, val category: Category, val venue: String, val startsAt: String, val price: Price)
}
