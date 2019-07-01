package utn.kotlin.travelkeeper.domain

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Flight(
    var id: String? = null,
    var airline: String,
    var flightNumber: String,
    var departureAirport: String,
    var arrivalAirport: String,
    var takeOffDate: Date,
    var reservationNumber: String? = null
) : TripElement, Serializable {

    override fun getBeginDate(): Date {
        return takeOffDate
    }

    override fun getType(): TripElementType {
        return TripElementType.FLIGHT
    }

    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["airline"] = airline
        returnMap["flight_number"] = flightNumber
        returnMap["departure_airport"] = departureAirport
        returnMap["arrival_airport"] = arrivalAirport
        returnMap["takeoff_date"] = dateFormatter.format(this.takeOffDate)
        reservationNumber?.let { returnMap["reservation_number"] = it }

        return returnMap
    }

    companion object {
        fun createObjectFromSnapshot(
            snapshot: DocumentSnapshot,
            dateParser: SimpleDateFormat,
            id: String
        ): Flight {
            return Flight(
                id,
                snapshot.getString("airline")!!,
                snapshot.getString("flight_number")!!,
                snapshot.getString("departure_airport")!!,
                snapshot.getString("arrival_airport")!!,
                dateParser.parse(snapshot.getString("takeoff_date")),
                snapshot.getString("reservation_number")
            )
        }
    }
}