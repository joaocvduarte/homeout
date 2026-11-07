package app.homeout.data

import app.homeout.domain.Category
import app.homeout.domain.Event
import app.homeout.domain.EventRepository
import app.homeout.domain.PosterColors
import app.homeout.domain.Price
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/** Local mock events from the design, dated today and tomorrow so they stay upcoming. */
class SampleEventRepository(
    private val clock: Clock,
    private val timeZone: TimeZone,
) : EventRepository {

    override suspend fun events(): List<Event> {
        val today = clock.now().toLocalDateTime(timeZone).date
        val tomorrow = today.plus(DatePeriod(days = 1))
        return listOf(
            Event(
                id = "fado-old-cistern",
                title = "Fado at the Old Cistern",
                category = Category.MUSIC,
                artists = listOf("Inês Calado", "Trio da Sé"),
                venue = "Alfama",
                startsAt = today.at(21, 30),
                price = Price.Euros(1800),
                venueName = "Cisterna do Largo",
                doorsAt = today.at(21, 0),
                poster = PosterColors(background = 0xFF8E4754, content = 0xFFFBE9EC),
                description = "An intimate night of fado inside a 17th-century water cistern beneath Alfama. " +
                    "Three fadistas, a Portuguese guitar and a viola, with about 80 seats lit by candles. " +
                    "The show runs around 90 minutes with one interval; drinks are served at the bar upstairs.",
            ),
            Event(
                id = "rooftop-vinyl",
                title = "Rooftop Vinyl Session",
                category = Category.MUSIC,
                artists = listOf("DJ Marta Rios"),
                venue = "Bairro Alto",
                startsAt = today.at(22, 0),
                price = Price.Free,
                venueName = "Terraço Calçada",
                endsAt = tomorrow.at(2, 0),
                poster = PosterColors(background = 0xFF335C57, content = 0xFFC9F2EB),
                description = "Local selectors play soul, Brazilian funk and disco from original records on a rooftop over the Tejo. " +
                    "Entry is free with a ticket, but space is limited, so arrive before 23:00.",
            ),
            Event(
                id = "night-market-river",
                title = "Night Market by the River",
                category = Category.FOOD,
                artists = listOf("Chef Rui Pais"),
                venue = "Cais do Sodré",
                startsAt = tomorrow.at(18, 0),
                price = Price.Free,
                venueName = "Praça do Cais",
                endsAt = tomorrow.at(23, 30),
                poster = PosterColors(background = 0xFFF69DAB, content = 0xFF283432),
                description = "Forty stalls of street food, natural wine and local makers along the river, with live sets from 20:00. " +
                    "Entry is free; reserve a ticket to use the fast lane at the gate.",
            ),
            Event(
                id = "petiscos-tasting",
                title = "Petiscos Tasting Night",
                category = Category.FOOD,
                artists = listOf("Chef Joana Lobo"),
                venue = "Campo de Ourique",
                startsAt = tomorrow.at(20, 0),
                price = Price.Euros(2500),
                venueName = "Tasca da Esquina",
                endsAt = tomorrow.at(22, 30),
                poster = PosterColors(background = 0xFFC9F2EB, content = 0xFF284D48),
                description = "Seven small plates from the neighbourhood’s best tascas, each paired with a Portuguese wine. " +
                    "Seated at shared tables of eight; a vegetarian menu is available on request.",
            ),
        )
    }

    private fun LocalDate.at(hour: Int, minute: Int) = atTime(LocalTime(hour, minute)).toInstant(timeZone)
}
