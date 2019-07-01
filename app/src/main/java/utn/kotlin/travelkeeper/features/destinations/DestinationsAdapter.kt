package utn.kotlin.travelkeeper.features.destinations

import android.app.DatePickerDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.domain.Destination
import utn.kotlin.travelkeeper.utils.DatePicker
import utn.kotlin.travelkeeper.utils.toStringDateOnly
import java.util.*

class DestinationsAdapter : RecyclerView.Adapter<NewDestinationViewHolder>() {
    val data: MutableList<Destination> = mutableListOf()
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDestinationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_destination_item_view, parent, false)
        context = parent.context

        return NewDestinationViewHolder(view)
    }


    private fun onDateSetListener(
        calendar: Calendar,
        selectedDate: EditText,
        isEndDate: Boolean,
        position: Int
    ): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            selectedDate.setText(calendar.time.toStringDateOnly())

            if (isEndDate) data[position].endDate = calendar.time else data[position].startDate = calendar.time
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: NewDestinationViewHolder, position: Int) {
        var calendar = Calendar.getInstance()
        val fromDate = holder.view.findViewById(R.id.from_date_edit) as EditText
        fromDate.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText, false, position)
            DatePicker.showDialog(onDateSetListener, calendar, context)
        }

        val endDate = holder.view.findViewById(R.id.to_date_edit) as EditText
        endDate.setOnClickListener {
            val onDateSetListener = onDateSetListener(calendar, it as EditText, true, position)
            DatePicker.showDialog(onDateSetListener, calendar, context)
        }

        val destinationText = holder.view.findViewById(R.id.destination_edit) as EditText
        destinationText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                saveDestinationText((v as TextView).text.toString(), position)
            }
        }

        destinationText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                saveDestinationText(s.toString(), position)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

//        destinationText.setOnEditorActionListener { v, actionId, event ->
//            /* https://stackoverflow.com/questions/8063439/android-edittext-finished-typing-event */
//            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                actionId == EditorInfo.IME_ACTION_DONE ||
//                event != null &&
//                event.action == KeyEvent.ACTION_DOWN &&
//                event.keyCode == KeyEvent.KEYCODE_ENTER
//            ) {
//                if (event == null || !event.isShiftPressed) {
//
//                    true
//                }
//            }
//            false
//        }
    }

    private fun saveDestinationText(destinationText: String, position: Int) {
        data[position].name = destinationText
    }

}

class NewDestinationViewHolder(val view: View) : RecyclerView.ViewHolder(view)