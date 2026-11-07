package app.homeout.di

import androidx.lifecycle.SavedStateHandle
import app.homeout.domain.Category
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.Event
import app.homeout.domain.EventRepository
import app.homeout.domain.GetEvent
import app.homeout.domain.GetUpcomingEvents
import app.homeout.domain.PosterColors
import app.homeout.domain.Price
import app.homeout.event.EventViewModel
import app.homeout.home.HomeViewModel
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import app.homeout.withKoin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.time.Clock
import kotlin.time.Instant

class KoinGraphTest {

    private val friday = object : Clock {
        override fun now() = Instant.parse("2026-11-13T19:00:00Z")
    }

    @Test
    fun appGraphResolvesEveryBindingAndViewModelWithLisbonAndTheSystemClock() = runTest {
        initKoin()
        try {
            val koin = KoinPlatform.getKoin()
            koin.get<EventRepository>()
            koin.get<GetUpcomingEvents>()
            koin.get<GetEvent>()
            koin.get<CountEventsThisWeek>()
            assertEquals(TimeZone.of("Europe/Lisbon"), koin.get<TimeZone>())
            assertSame(Clock.System, koin.get<Clock>())
            koin.get<HomeViewModel>()
            koin.get<EventViewModel> { parametersOf(SavedStateHandle(mapOf("id" to "fado-old-cistern"))) }
        } finally {
            stopKoin()
        }
    }

    @Test
    fun overridesReplaceTheRepositoryAndClock() = runTest {
        val only = Event(
            id = "only",
            title = "Only Event",
            category = Category.MUSIC,
            artists = listOf("Someone"),
            venue = "Alfama",
            startsAt = Instant.parse("2026-11-13T21:00:00Z"),
            price = Price.Free,
            venueName = "Somewhere",
            description = "A fake event.",
            poster = PosterColors(0xFF000000, 0xFFFFFFFF),
        )
        val fake = module {
            single<EventRepository> {
                object : EventRepository {
                    override suspend fun events() = listOf(only)
                }
            }
        }
        withKoin(friday, overrides = listOf(fake)) {
            val koin = KoinPlatform.getKoin()
            assertSame(friday, koin.get<Clock>())
            assertEquals(TimeZone.UTC, koin.get<TimeZone>())
            assertEquals(only, koin.get<GetEvent>()("only")?.event)
            assertEquals(listOf(only), koin.get<GetUpcomingEvents>()().flatMap { it.events })
        }
    }
}
