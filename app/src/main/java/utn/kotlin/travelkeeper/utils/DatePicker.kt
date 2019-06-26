package utn.kotlin.travelkeeper.utils

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

object DatePicker {

    fun showDialog(
        onDateSetListener: DatePickerDialog.OnDateSetListener,
        calendar: Calendar,
        context: Context
    ) {
        val datePickerDialog = DatePickerDialog(
            context,
            onDateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

}