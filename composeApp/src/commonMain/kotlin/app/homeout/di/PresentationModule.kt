package app.homeout.di

import app.homeout.event.EventViewModel
import app.homeout.home.HomeViewModel
import app.homeout.root.RootViewModel
import app.homeout.tickets.TicketsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** The presentation layer: one ViewModel per screen. */
val presentationModule = module {
    viewModelOf(::RootViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::TicketsViewModel)
    viewModelOf(::EventViewModel)
}
