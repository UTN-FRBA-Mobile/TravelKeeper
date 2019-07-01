package utn.kotlin.travelkeeper.domain

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Hotel(var id: String?, var name: String, var start_date: Date, var end_date: Date) : Serializable {

    fun createMapFromObject(dateFormatter: SimpleDateFormat): HashMap<String, String> {
        val returnMap = HashMap<String, String>()
        returnMap["name"] = this.name
        returnMap["start_date"] = dateFormatter.format(this.start_date)
        returnMap["end_date"] = dateFormatter.format(this.end_date)

        return returnMap
    }

    companion object {
        fun createObjectFromSnapshot(
            snapshot: DocumentSnapshot,
            dateParser: SimpleDateFormat,
            id: String
        ): Hotel {
            return Hotel(
                id,
                snapshot.getString("name")!!,
                dateParser.parse(snapshot.getString("start_date")),
                dateParser.parse(snapshot.getString("end_date"))
            )
        }
    }
}
