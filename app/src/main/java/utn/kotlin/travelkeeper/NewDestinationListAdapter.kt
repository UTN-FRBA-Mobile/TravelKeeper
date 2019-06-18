package utn.kotlin.travelkeeper

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_new_destination.*
import java.text.SimpleDateFormat
import java.util.*

class NewDestinationListAdapter : RecyclerView.Adapter<NewDestinationViewHolder>() {
    val data: MutableList<NewDestination> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDestinationViewHolder { //fix: a partir del 3ro no me lo muestra
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_destination_view, parent, false)
        context = parent.context

        val fromDate = view.findViewById(R.id.from_date_edit) as EditText
        fromDate.setOnClickListener {
            showDatePickerDialog(it as EditText)
        }

        val endDate = view.findViewById(R.id.to_date_edit) as EditText
        endDate.setOnClickListener {
            showDatePickerDialog(it as EditText)
        }

        return NewDestinationViewHolder(view)
    }

    private fun showDatePickerDialog(selectedDate: EditText) { //todo: pasarlo a algun utils o algo asi no se repite
        // FIX: EL PRIMER CLICK NO ANDA
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate.setText(dateToString(calendar.time))
        }
        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    private fun dateToString(date: Date): String {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(date)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: NewDestinationViewHolder, position: Int) {
    }

}

class NewDestinationViewHolder(val view: View) : RecyclerView.ViewHolder(view)