package utn.kotlin.travelkeeper.models

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

data class Accommodation(
    val id: String,
    val name: String,
    val address: String,
    val startDate: Date,
    val endDate: Date,
    val telephoneNumber: String? = null,
    val reservationCode: String? = null
) {
    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["name"] = this.name
        returnMap["start_date"] = dateFormatter.format(this.startDate)
        returnMap["end_date"] = dateFormatter.format(this.endDate)
        returnMap["address"] = address
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
                dateParser.parse(snapshot.getString("start_date")),
                dateParser.parse(snapshot.getString("end_date")),
                snapshot.getString("telephoneNumber"),
                snapshot.getString("reservationCode") //chequear si con nulos no se rompe
            )
        }
    }
} //todo: ver el tema del archivo