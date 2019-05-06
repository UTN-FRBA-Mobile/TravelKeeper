package utn.kotlin.travelkeeper.apis

import utn.kotlin.travelkeeper.models.Trip
import java.util.*

class MyTripsApi {
    fun getTrips(): MutableList<Trip> {
        val dataset = mutableListOf<Trip>()
        for (i in 0..4) {
            dataset.add(Trip(null, "Trip $i", Date(), Date()))
        }
        return dataset
    }
}