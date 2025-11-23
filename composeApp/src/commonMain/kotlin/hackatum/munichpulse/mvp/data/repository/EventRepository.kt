package hackatum.munichpulse.mvp.data.repository

import androidx.compose.runtime.collectAsState
import hackatum.munichpulse.mvp.backend.FirebaseInterface
import hackatum.munichpulse.mvp.data.model.Event
import hackatum.munichpulse.mvp.data.model.eventFuncs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Repository interface for accessing event data.
 */
class EventRepository {
//    /**
//     * Returns a flow of trending events.
//     */
//    fun getTrendingEvents(): Flow<List<Event>>
//
//    /**
//     * Returns a flow of events near the user.
//     */
//    fun getNearbyEvents(): Flow<List<Event>>
//
//    /**
//     * Returns a flow of all available events.
//     */
//    fun getAllEvents(): Flow<List<Event>>
//
//    /**
//     * Returns a flow of a specific event by its ID.
//     */
//    fun getEventById(eventId: String): Flow<Event?>

    private val events = getAllEvents()

    fun getAllEvents(): Flow<List<Event>> = eventFuncs.getAllEventsFlow()

    // ANNAHME: 'getAllEvents()' liefert den Basis-Flow<List<Event>>
    fun getTrendingEvents(): Flow<List<Event>> {
        return getAllEvents()
            .map { eventList ->
                // Hier filtern wir die Liste, die *innerhalb* des Flows ankommt
                eventList.filter { it.isTrending }
            }
    }

    fun getNearbyEvents(): Flow<List<Event>> {
        return getAllEvents()
            .map { eventList ->
                // Hier nehmen wir die ersten 3 Elemente der Liste
                eventList.take(3)
            }
    }

    fun getEventById(eventId: String): Flow<Event?> {
        return getAllEvents()
            .map { eventList ->
                // Hier finden wir das Event innerhalb der Liste
                eventList.find { it.id == eventId }
            }
    }

}

//class MockEventRepository : EventRepository {
//    private val events = listOf(
//        Event(
//            id = "1",
//            title = "Tollwood Festival",
//            location = "Olympiapark Süd",
//            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuARp07lOoSMJ0Vi0n70MTVqcPnb7AIvrkaDNJG6S9WkBY-wnUR3IKzMyFLzcg0H8DsrvW8o29d7OiXlvJghqvGdVPqXffihptmghZqAYNlLaj_2n3aGjrVzOvx8QY9MHadn17cLoCzI5f0spIN0tvdIbMho2yKiorks4K-jkuHIjHwFEJX0jK0eK71OXP2wprm3_8B_2_jASWw8_bA26eMxTFILBdq1ZPZy9cFe_XyIvH4Wls54nJ9ZZttTPLWnkbRncAa3jLip1TWI",
//            fullnessPercentage = 25,
//            isTrending = true
//        ),
//        Event(
//            id = "2",
//            title = "Blade Night",
//            location = "Bavariapark",
//            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAiuPnZ9n2gHkIOOmG_UTy9hBthXvbsnU21vcH7mJ-9mDSr23C6kKB5T9rR_977ClHtPVScuqbzMYc3D27XR6ww5knIJGMM08Rb4IbJj6oxvTZwhjVdK8Z6BmhRX6sv7I7Oj5_iO0NudOvIHg9plw2---zcL0WOnpsX_TRczGBOk68QPzufvflC1FZQItPcNEEw7xL5imCcVRqCMv8BMGkSQamOXQJukcyBwjWu1jwWb1_gTNiA1WzkXdSArs4P5KvxVwR5aiV7-Q8e",
//            fullnessPercentage = 15,
//            isTrending = true
//        ),
//        Event(
//            id = "3",
//            title = "Open Air Kino",
//            location = "Königsplatz",
//            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDn_niMUwvoQJeWNAJmCC-h4-HCIP6qkNC6q2D54DCr4A5_w-2yGCk8hn0FdEJpx50Yo9OBBWdt5Q30JqKySuCbyWlv4FyynZq_qGDtEuSQPOcBdHLEGHwf2KCqXBO9h1D7MYhNOCAJGb-MCZUDsmbhyjUgOCEc5BF8vdv-1qRtNZId64Ku0-PXzNniica37wmAJlFAQF7w0t-lfqZQDaoTULTSm_1OGj_9Cx9e6O_u4U9naUJ7SKQ05-zNh-zh1Hoo1wrXN7W3dGqq",
//            fullnessPercentage = 40
//        ),
//        Event(
//            id = "4",
//            title = "Street Food Market",
//            location = "Werksviertel",
//            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDQJLTB34FdZDgETNFhkLJsLrQNOg-Ngjy_FQVqVDaDOOAfX_KH6vmACBk0orx0tEIr1nooiF7My5ROFmLTfYWnGZqCrJCHwaooCsjUoy0GnPsPwbQpn0kJUPJin_ZrkSGFcZ3Nm-bSeB7U-L3d5AURRVrYYE1EImgwm-I4UbsJQdBuT0b_XAB6YsS0guQ4Ybs3jTZsjgtRBGBcgWWrJgyuBgA9aQxSfWe88ZZ51UF9MftP6ECw0wdhY643uVAgtFAIvZdSZ2N6zzSz",
//            fullnessPercentage = 60
//        ),
//        Event(
//            id = "5",
//            title = "Olympiapark Concert",
//            location = "Olympiapark",
//            imageUrl = "https://images.unsplash.com/photo-1459749411177-0473ef71607b?q=80&w=2070&auto=format&fit=crop",
//            fullnessPercentage = 95,
//            isTrending = true
//        ),
//        Event(
//            id = "6",
//            title = "Pinakothek der Moderne",
//            location = "Maxvorstadt",
//            imageUrl = "https://images.unsplash.com/photo-1566054757965-8c4d79636511?q=80&w=2069&auto=format&fit=crop",
//            fullnessPercentage = 30
//        )
//    )
//}
