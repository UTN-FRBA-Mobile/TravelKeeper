package utn.kotlin.travelkeeper.domain

import java.util.*

interface TripElement {
    fun getType(): TripElementType
    fun getBeginDate(): Date
}