package utn.kotlin.travelkeeper.utils

import java.text.SimpleDateFormat
import java.util.*

val DATE_ONLY_PATTERN = "dd/MM/yyyy"
val DATE_AND_HOUR_PATTERN = "dd/MM/yyyy HH:mm:ss"

private fun locale() = Locale("es", "ES") //probar con country es_AR
fun dateAndHourFormat() = SimpleDateFormat(DATE_AND_HOUR_PATTERN, locale())
fun dateOnlyFormat() = SimpleDateFormat(DATE_ONLY_PATTERN, locale())

fun Date.toStringDateOnly(): String {
    val sdf = SimpleDateFormat(DATE_ONLY_PATTERN, locale())
    return sdf.format(this)
}

fun parserWithFormat(format: String): SimpleDateFormat {
    return SimpleDateFormat(format, locale())
}

fun Date.getHour(): Int {
    val calendarDate = Calendar.getInstance()
    calendarDate.time = this
    return calendarDate.get(Calendar.HOUR_OF_DAY)
}

fun Date.getMinute(): Int {
    val calendarDate = Calendar.getInstance()
    calendarDate.time = this
    return calendarDate.get(Calendar.MINUTE)
}

fun Date.createCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this.time
    return calendar
}

fun Date.addHourAndMinutes(hourOfDay: Int, minutes: Int): Date {
    val calendarDate = Calendar.getInstance()
    calendarDate.time = this
    cleanCalendarHours(calendarDate)
    calendarDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
    calendarDate.set(Calendar.MINUTE, minutes)
    return calendarDate.time
}

private fun cleanCalendarHours(date: Calendar) {
    date.set(Calendar.HOUR_OF_DAY, 0)
    date.set(Calendar.MINUTE, 0)
    date.set(Calendar.SECOND, 0)
    date.set(Calendar.MILLISECOND, 0)
}