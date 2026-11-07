package app.homeout.event

import app.homeout.domain.EventDetail
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class EventFormatTest {

    private val utc = TimeZone.UTC

    @Test
    fun doorsFormForFado() = runTest {
        val fado = sampleDetail("fado-old-cistern")
        assertEquals("Fri 13 Nov · Doors 21:00 · Starts 21:30", fado.whenLine(utc))
        assertEquals("Fri 13 Nov · 21:30", fado.posterDate(utc))
        assertEquals("Cisterna do Largo, Alfama", fado.fullVenue())
        assertEquals("Music · Tonight · Fri 13 Nov", fado.categoryAndDay(utc))
        assertEquals("per person", fado.priceNote())
        assertEquals("Buy tickets", fado.ticketLabel())
    }

    @Test
    fun endTimeFormRunsPastMidnight() = runTest {
        val vinyl = sampleDetail("rooftop-vinyl")
        assertEquals("Fri 13 Nov · 22:00 – 02:00", vinyl.whenLine(utc))
        assertEquals("Ticket required", vinyl.priceNote())
        assertEquals("Get free tickets", vinyl.ticketLabel())
    }

    @Test
    fun tomorrowHasNoTonight() = runTest {
        assertEquals("Food · Sat 14 Nov", sampleDetail("petiscos-tasting").categoryAndDay(utc))
    }

    @Test
    fun plainStartWhenNoDoorsOrEnd() = runTest {
        val petiscos = sampleDetail("petiscos-tasting")
        val plain = EventDetail(petiscos.event.copy(doorsAt = null, endsAt = null), isToday = false)
        assertEquals("Sat 14 Nov · 20:00", plain.whenLine(utc))
    }
}
