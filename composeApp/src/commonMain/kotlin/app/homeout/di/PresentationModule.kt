package app.homeout.di

import app.homeout.auth.LoginViewModel
import app.homeout.auth.SignupViewModel
import app.homeout.buy.BuyViewModel
import app.homeout.event.EventViewModel
import app.homeout.getapp.GetAppViewModel
import app.homeout.home.HomeViewModel
import app.homeout.qr.QrViewModel
import app.homeout.root.RootViewModel
import app.homeout.tickets.TicketsViewModel
import app.homeout.website.WebsiteViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** The presentation layer: one ViewModel per screen. */
val presentationModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::RootViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::TicketsViewModel)
    viewModelOf(::EventViewModel)
    viewModelOf(::BuyViewModel)
    viewModelOf(::QrViewModel)
    viewModelOf(::GetAppViewModel)
    viewModelOf(::WebsiteViewModel)
}
