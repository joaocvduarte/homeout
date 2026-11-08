package app.homeout.domain

interface EventRepository {
    suspend fun events(): List<Event>
}
