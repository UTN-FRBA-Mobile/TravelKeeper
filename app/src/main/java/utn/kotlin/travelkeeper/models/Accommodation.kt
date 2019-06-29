package utn.kotlin.travelkeeper.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class Accommodation(
    var id: String? = null,
    var name: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var startDate: Date,
    var endDate: Date,
    val telephoneNumber: String? = null,
    val reservationCode: String? = null
) : Serializable {
    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["name"] = this.name
        returnMap["start_date"] = dateFormatter.format(this.startDate)
        returnMap["end_date"] = dateFormatter.format(this.endDate)
        returnMap["address"] = address
        returnMap["latitude"] = latitude.toString()
        returnMap["longitude"] = longitude.toString()
        telephoneNumber?.apply { returnMap["telephoneNumber"] = telephoneNumber }
        reservationCode?.apply { returnMap["reservationCode"] = reservationCode }

        return returnMap
    }

    companion object {
        fun createObjectFromSnapshot(
            snapshot: DocumentSnapshot,
            dateParser: SimpleDateFormat,
            id: String
        ): Accommodation {
            return Accommodation(
                id,
                snapshot.getString("name")!!,
                snapshot.getString("address")!!,
                (snapshot.getString("latitude")!!).toDouble(),
                (snapshot.getString("longitude")!!).toDouble(),
                dateParser.parse(snapshot.getString("start_date")),
                dateParser.parse(snapshot.getString("end_date")),
                snapshot.getString("telephoneNumber"),
                snapshot.getString("reservationCode") //chequear si con nulos no se rompe
            )
        }
    }
}