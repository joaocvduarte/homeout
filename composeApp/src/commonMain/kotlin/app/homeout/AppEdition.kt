package app.homeout

/**
 * Which app is running. [App] is the phone app, with accounts, buying and tickets. [Web] only shows events:
 * no log in, no account in the header, no tabs, and Buy tickets sends people to the app stores.
 */
enum class AppEdition { App, Web }
