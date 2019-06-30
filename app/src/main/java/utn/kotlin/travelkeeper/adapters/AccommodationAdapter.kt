package utn.kotlin.travelkeeper.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.accommodation_item_view.view.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.models.Accommodation
import java.text.SimpleDateFormat
import java.util.*

class AccommodationAdapter (
    private val accommodationList: MutableList<Accommodation>,
    private val tripId: String,
    private val destinationId: String) :RecyclerView.Adapter<AccommodationViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccommodationViewHolder {
        val accommodationItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.accommodation_item_view, parent, false)

        context = parent.context
        return AccommodationViewHolder(accommodationItem)
    }

    override fun getItemCount() = accommodationList.size

    override fun onBindViewHolder(holder: AccommodationViewHolder, position: Int) {
    val accommodation = accommodationList[position]
        holder.view.accommodation_desc_text.text = accommodation.name
        holder.view.accommodation_date_text.text = getDate(accommodation.startDate) + " - " + getDate(accommodation.endDate)
        holder.view.accommodation_address.text = accommodation.address

        holder.view.tag = position

        var activity = context as Activity
        activity.registerForContextMenu(holder.view)
    }

    private fun getDate(date: Date): String {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(date)
    }

}

class AccommodationViewHolder(val view: View) : RecyclerView.ViewHolder(view)