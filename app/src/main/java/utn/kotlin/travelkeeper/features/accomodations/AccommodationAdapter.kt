package utn.kotlin.travelkeeper.features.accomodations

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.accommodation_item_view.view.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.domain.Accommodation
import utn.kotlin.travelkeeper.utils.dateOnlyFormat
import java.util.*

class AccommodationAdapter(
    private val accommodationListInterface: AccommodationListInterface,
    private val accommodationList: MutableList<Accommodation>
) : RecyclerView.Adapter<AccommodationViewHolder>() {
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
        holder.view.accommodation_date_text.text =
            getDate(accommodation.startDate) + " - " + getDate(accommodation.endDate)
        holder.view.accommodation_address.text = accommodation.address

        holder.view.accommodation_location.setOnClickListener {
            var intent = Intent(context, AccommodationMapsActivity::class.java)
            intent.putExtra("ACCOMMODATION_LOCATION_NAME", accommodation.name)
            intent.putExtra("ACCOMMODATION_LOCATION_LATITUDE", accommodation.latitude)
            intent.putExtra("ACCOMMODATION_LOCATION_LONGITUDE", accommodation.longitude)
            context.startActivity(intent)
        }

        holder.view.setOnClickListener {
            accommodationListInterface.showEditAccommodationActivity(position)
        }

        holder.view.tag = position

        var activity = context as Activity
        activity.registerForContextMenu(holder.view)
    }

    private fun getDate(date: Date): String {
        return dateOnlyFormat().format(date)
    }

}

class AccommodationViewHolder(val view: View) : RecyclerView.ViewHolder(view)