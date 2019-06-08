package utn.kotlin.travelkeeper.models

import java.io.Serializable
import java.util.*


class TripTimeLineInfo(
    var detail: String,
    var type: String,
    var start_date: Date,
    var end_date: Date
): Serializable
