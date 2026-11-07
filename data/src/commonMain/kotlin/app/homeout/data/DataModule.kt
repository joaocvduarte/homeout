package app.homeout.data

import app.homeout.domain.EventRepository
import org.koin.dsl.module

/** The data layer: where events come from. */
val dataModule = module {
    single<EventRepository> { SampleEventRepository(get(), get()) }
}
