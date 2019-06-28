package utn.kotlin.travelkeeper.models

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Destination(
    var destination: String? = null,
    var startDate: Date? = null,
    var endDate: Date? = null,
    var id: String? = null
) : Serializable, TripElement {

    override fun getType(): Int {
        return TripElementType.DESTINATION.type
    }

    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["name"] = this.destination!!
        returnMap["type"] = "Lugar"
        returnMap["start_date"] = dateFormatter.format(this.startDate)
        returnMap["end_date"] = dateFormatter.format(this.endDate)

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
