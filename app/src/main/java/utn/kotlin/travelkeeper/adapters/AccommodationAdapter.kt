package utn.kotlin.travelkeeper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.accommodation_item_view.view.*
import utn.kotlin.travelkeeper.AccommodationMapsActivity
import utn.kotlin.travelkeeper.EditAccommodationActivity
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.models.Accommodation
import java.text.SimpleDateFormat
import java.util.*

class AccommodationAdapter (
    private val accommodationList: MutableList<Accommodation>,
    private val tripId: String,
    private val destinationId: String) :RecyclerView.Adapter<AccommodationViewHolder>() {
    private lateinit var context: Context
    val EDIT_ACCOMMODATION_INTENT = 2


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

        holder.view.accommodation_location.setOnClickListener {
            var intent = Intent(context, AccommodationMapsActivity::class.java)
            intent.putExtra("ACCOMMODATION_LOCATION_NAME",accommodation.name)
            intent.putExtra("ACCOMMODATION_LOCATION_LATITUDE",accommodation.latitude)
            intent.putExtra("ACCOMMODATION_LOCATION_LONGITUDE",accommodation.longitude)
            context.startActivity(intent)
        }

        holder.view.setOnClickListener {
            val editDestIntent = Intent(context, EditAccommodationActivity::class.java)
            editDestIntent.putExtra("ACCOMMODATION_EDIT", accommodation)
            editDestIntent.putExtra("TRIPID_ACCOMMODATION_EDIT", tripId)
            editDestIntent.putExtra("DESTID_ACCOMMODATION_EDIT", destinationId)
            editDestIntent.putExtra("EDIT_ACCOMMODATION_POSITION", position)

            val act = context as Activity
            act.startActivityForResult(editDestIntent, EDIT_ACCOMMODATION_INTENT)
        }

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