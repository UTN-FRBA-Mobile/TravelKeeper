package utn.kotlin.travelkeeper

import android.app.DatePickerDialog
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import utn.kotlin.travelkeeper.models.NewDestination
import java.text.SimpleDateFormat
import java.util.*

class DestinationsAdapter : RecyclerView.Adapter<NewDestinationViewHolder>() {
    val data: MutableList<NewDestination> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDestinationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_destination_view, parent, false)
        context = parent.context

        return NewDestinationViewHolder(view)
    }

    private fun showDatePickerDialog(
        selectedDate: EditText,
        position: Int,
        isEndDate: Boolean
    ) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate.setText(dateToString(calendar.time))

            if (isEndDate) data[position].endDate = calendar.time else data[position].startDate = calendar.time
        }

        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()

    }

    private fun dateToString(date: Date): String { //todo: pasarlo a algun utils o algo asi no se repite
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(date)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: NewDestinationViewHolder, position: Int) {
        val fromDate = holder.view.findViewById(R.id.from_date_edit) as EditText
        fromDate.setOnClickListener {
            showDatePickerDialog(it as EditText, position, false)
        }

        val endDate = holder.view.findViewById(R.id.to_date_edit) as EditText
        endDate.setOnClickListener {
            showDatePickerDialog(it as EditText, position, true)
        }

        val destinationText = holder.view.findViewById(R.id.destination_edit) as EditText
        destinationText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                saveDestinationText((v as TextView).text.toString(), position)
            }
        }

        destinationText.setOnEditorActionListener { v, actionId, event ->
            /* https://stackoverflow.com/questions/8063439/android-edittext-finished-typing-event */
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null &&
                event.action == KeyEvent.ACTION_DOWN &&
                event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                if (event == null || !event.isShiftPressed) {
                    saveDestinationText(v.text.toString(), position)
                    true
                }
            }
            false
        }
    }

    private fun saveDestinationText(destinationText: String, position: Int) {
        data[position].destination = destinationText
    }

}

class NewDestinationViewHolder(val view: View) : RecyclerView.ViewHolder(view)