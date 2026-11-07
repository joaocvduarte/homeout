package app.homeout.di

import app.homeout.event.EventViewModel
import app.homeout.home.HomeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** The presentation layer: one ViewModel per screen. */
val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::EventViewModel)
}
