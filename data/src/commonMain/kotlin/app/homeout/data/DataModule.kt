package app.homeout.data

import app.homeout.domain.EventRepository
import app.homeout.domain.OrderRepository
import org.koin.dsl.module

/** The data layer: where events come from and where orders are kept. */
val dataModule = module {
    single<EventRepository> { SampleEventRepository(get(), get()) }
    single<OrderRepository> { InMemoryOrderRepository() }
}
