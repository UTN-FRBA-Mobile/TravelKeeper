package utn.kotlin.travelkeeper.models

import java.util.*

class Trip(var id: Long?, var title: String, var startDate: Date, var endDate: Date) {

    fun isTripNow(): Boolean {
        val now = Date()
        if (now.after(startDate) && now.before(endDate)) {
            return true
        }
        return false
    }
}