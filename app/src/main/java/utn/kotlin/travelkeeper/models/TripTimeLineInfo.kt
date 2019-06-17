package utn.kotlin.travelkeeper.models

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class TripTimeLineInfo( //Destination //destinos_en_viaje en la bd
    var id: String? = null,
    var name: String,
    var type: String,
    var start_date: Date,
    var end_date: Date
) : Serializable {

    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["name"] = this.name
        returnMap["type"] = this.type
        returnMap["start_date"] = dateFormatter.format(this.start_date)
        returnMap["end_date"] = dateFormatter.format(this.end_date)

        return returnMap
    }

    companion object {
        fun createObjectFromSnapshot(
            snapshot: DocumentSnapshot,
            dateParser: SimpleDateFormat,
            id: String
        ): TripTimeLineInfo {
            return TripTimeLineInfo(
                id,
                snapshot.getString("name")!!,
                snapshot.getString("type")!!,
                dateParser.parse(snapshot.getString("start_date")),
                dateParser.parse(snapshot.getString("end_date"))
            )
        }
    }
}
