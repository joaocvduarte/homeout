package app.homeout.data

import app.homeout.domain.AccountRepository
import app.homeout.domain.EventRepository
import app.homeout.domain.OrderRepository
import app.homeout.domain.PasswordHasher
import org.koin.dsl.module

/**
 * The data layer: where events come from, where orders are kept, and the accounts on the device.
 * The [KeyValueStore] behind the accounts comes from each platform's own module.
 */
val dataModule = module {
    single<EventRepository> { SampleEventRepository(get(), get()) }
    single<OrderRepository> { InMemoryOrderRepository() }
    single<AccountRepository> { StoredAccountRepository(get()) }
    single<PasswordHasher> { Pbkdf2PasswordHasher() }
}
