package utn.kotlin.travelkeeper.utils

import java.text.SimpleDateFormat
import java.util.*

fun Date.dateToString(): String {
    val myFormat = "dd/MM/yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))
    return sdf.format(this)
}

fun Date.createCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    return calendar
}

fun Date.addHourAndMinutes(hourOfDay: Int, minutes: Int): Calendar {
    val calendarDate = Calendar.getInstance()
    calendarDate.time = this
    cleanCalendarHours(calendarDate)
    calendarDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
    calendarDate.set(Calendar.MINUTE, minutes)
    return calendarDate
}

private fun cleanCalendarHours(date: Calendar) {
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
}