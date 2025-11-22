package hackatum.munichpulse.mvp

import hackatum.munichpulse.mvp.backend.FirebaseInterface

// The Communicator between the View models and the model
// in the MVVM-Approach
class DataController {
    companion object {
        private var INSTANCE: DataController = DataController()
        fun getInstance(): DataController = INSTANCE
    }

    // Expose suspend functions to be called from ViewModels
    suspend fun addUserToEventGroup(eventId: String): String {
        return FirebaseInterface
            .getInstance()
            .addUserToAvailableGroup(eventId, FirebaseInterface.getInstance().getUserId(), maxSize = 5)
    }

    suspend fun addUserToEventIndividuals(eventId: String) {
        FirebaseInterface
            .getInstance()
            .addUserToEventIndividuals(eventId, FirebaseInterface.getInstance().getUserId())
    }
}