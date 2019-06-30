package utn.kotlin.travelkeeper.interfaces

import utn.kotlin.travelkeeper.models.Destination
import utn.kotlin.travelkeeper.models.Flight

interface TripTimeLineInterface {
    fun showEditFlightActivity(flight: Flight, position: Int)
    fun showEditDestinationActivity(destination: Destination, position: Int)
}