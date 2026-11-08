package app.homeout

/** Loads the IANA time zone database so `TimeZone.of("Europe/Lisbon")` works on Wasm (kotlinx-datetime README). */
@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule
