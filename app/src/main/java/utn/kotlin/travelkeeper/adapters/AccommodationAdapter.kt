package utn.kotlin.travelkeeper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.accommodation_item_view.view.*
import kotlinx.android.synthetic.main.borrar_text_view.view.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.models.Accommodation

class AccommodationAdapter (
    private val accommodationList: MutableList<Accommodation>,
    private val tripId: String,
    private val destinationId: String) :RecyclerView.Adapter<AccommodationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccommodationViewHolder {
        val accommodationItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.accommodation_item_view, parent, false)
        return AccommodationViewHolder(accommodationItem)
    }

    override fun getItemCount() = accommodationList.size

    override fun onBindViewHolder(holder: AccommodationViewHolder, position: Int) {
    val accommodation = accommodationList[position]
        holder.view.accommodation_desc_text.text = accommodation.name
        holder.view.accommodation_date_text.text = accommodation.startDate.toString()
    }

}

class AccommodationViewHolder(val view: View) : RecyclerView.ViewHolder(view)