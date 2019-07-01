package utn.kotlin.travelkeeper.features.trips

import utn.kotlin.travelkeeper.domain.Destination
import utn.kotlin.travelkeeper.domain.Flight

interface TripTimeLineInterface {
    fun showEditFlightActivity(flight: Flight, position: Int)
    fun showEditDestinationActivity(destination: Destination, position: Int)
}