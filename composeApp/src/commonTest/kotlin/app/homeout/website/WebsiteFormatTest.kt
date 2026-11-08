package app.homeout.website

import app.homeout.data.SampleEventRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import app.homeout.event.friday

class WebsiteFormatTest {

    private val lisbon = TimeZone.of("Europe/Lisbon")

    private suspend fun event(id: String) = SampleEventRepository(friday, lisbon).events().first { it.id == id }

    @Test
    fun aPaidEventTonightReadsLikeTheDesign() = runTest {
        val fado = event("fado-old-cistern")
        assertEquals("Tonight", WebsiteEvent(fado, isToday = true).kicker(lisbon))
        assertEquals("Fri 13 Nov · 21:30", fado.websiteDate(lisbon))
        assertEquals("Music · 21:30", fado.categoryAndTime(lisbon))
        assertEquals("€18 per person", fado.websitePrice())
        assertEquals("Get tickets for Fado at the Old Cistern", fado.ticketsLabel())
    }

    @Test
    fun aFreeEventTomorrowNamesTheWeekday() = runTest {
        val market = event("night-market-river")
        assertEquals("Saturday", WebsiteEvent(market, isToday = false).kicker(lisbon))
        assertEquals("Free with a ticket", market.websitePrice())
    }

    @Test
    fun seeAllIsSingularOrPlural() {
        assertEquals("See all 4 events", seeAllLabel(4))
        assertEquals("See all 1 event", seeAllLabel(1))
    }
}
