package app.homeout.di

import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.GetEvent
import app.homeout.domain.GetUpcomingEvents
import kotlinx.datetime.TimeZone
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * The domain layer: the use cases, plus the clock and time zone they read.
 * Declared here so `domain` itself stays free of DI code.
 */
val domainModule = module {
    single<Clock> { Clock.System }
    single<TimeZone> { TimeZone.of("Europe/Lisbon") }
    factory { GetUpcomingEvents(get(), get(), get()) }
    factory { GetEvent(get(), get(), get()) }
    factory { CountEventsThisWeek(get(), get(), get()) }
}
