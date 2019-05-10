package utn.kotlin.travelkeeper.apis

import utn.kotlin.travelkeeper.models.Trip
import java.util.*

class MyTripsApi {
    fun getTrips(): MutableList<Trip> {
        val dataset = mutableListOf<Trip>()
        val calendarStart = Calendar.getInstance();
        val calendarEnd = Calendar.getInstance();

        calendarStart.set(Calendar.HOUR_OF_DAY, 0)
        calendarStart.set(Calendar.MINUTE, 0)
        calendarStart.set(Calendar.SECOND, 0)

        calendarEnd.set(Calendar.HOUR_OF_DAY, 23)
        calendarEnd.set(Calendar.MINUTE, 59)
        calendarEnd.set(Calendar.SECOND, 59)
        for (i in 0..4) {
            calendarStart.add(Calendar.DAY_OF_MONTH, i)
            calendarEnd.add(Calendar.DAY_OF_MONTH, i + i)
            dataset.add(Trip(null, "Trip $i", calendarStart.time, calendarEnd.time))
        }
        return dataset
    }
}