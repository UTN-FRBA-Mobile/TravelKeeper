package utn.kotlin.travelkeeper.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.dateToString(): String {
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))
    return sdf.format(this)
}