package utn.kotlin.travelkeeper.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.models.Trip
import java.text.SimpleDateFormat
import java.util.*

class MyTripsAdapter(private val myDataset: MutableList<Trip>) :
    RecyclerView.Adapter<MyTripsAdapter.TripsViewHolder>() {

    var firstTitleUsed = false
    var secondTitleUsed = false

    class TripsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        return TripsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.my_trips_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.trip_title).text = myDataset[position].title

        val dateFormatter = SimpleDateFormat(
            holder.view.context.getString(R.string.my_trips_date_format),
            Locale.getDefault()
        )

        val datesString = dateFormatter.format(myDataset[position].startDate) + " - " +
                dateFormatter.format(myDataset[position].endDate)

        holder.view.findViewById<TextView>(R.id.trip_dates).text = datesString

        if (myDataset[position].isTripNow()) {
            if (!firstTitleUsed) {
                holder.view.findViewById<TextView>(R.id.section_title).text =
                    holder.view.context.getString(R.string.current);
                holder.view.findViewById<TextView>(R.id.section_title).visibility = View.VISIBLE;
                firstTitleUsed = true
            }
        } else {
            if (!secondTitleUsed) {
                holder.view.findViewById<TextView>(R.id.section_title).text =
                    holder.view.context.getString(R.string.next_trips);
                holder.view.findViewById<TextView>(R.id.section_title).visibility = View.VISIBLE;
                secondTitleUsed = true
            }
        }
    }

    override fun getItemCount() = myDataset.size
}