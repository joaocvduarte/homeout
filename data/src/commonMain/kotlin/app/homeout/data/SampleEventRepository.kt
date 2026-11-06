package app.homeout.data

import app.homeout.domain.Category
import app.homeout.domain.Event
import app.homeout.domain.EventRepository
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

/** Local mock events, dated today and tomorrow so they stay upcoming. */
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
            ),
            Event(
                id = "rooftop-vinyl",
                title = "Rooftop Vinyl Session",
                category = Category.MUSIC,
                artists = listOf("DJ Marta Rios"),
                venue = "Bairro Alto",
                startsAt = today.at(22, 0),
                price = Price.Free,
            ),
            Event(
                id = "night-market-river",
                title = "Night Market by the River",
                category = Category.FOOD,
                artists = listOf("Chef Rui Pais"),
                venue = "Cais do Sodré",
                startsAt = tomorrow.at(18, 0),
                price = Price.Free,
            ),
            Event(
                id = "petiscos-tasting",
                title = "Petiscos Tasting Night",
                category = Category.FOOD,
                artists = listOf("Chef Joana Lobo"),
                venue = "Campo de Ourique",
                startsAt = tomorrow.at(20, 0),
                price = Price.Euros(2500),
            ),
        )
    }

    private fun LocalDate.at(hour: Int, minute: Int) = atTime(LocalTime(hour, minute)).toInstant(timeZone)
}
