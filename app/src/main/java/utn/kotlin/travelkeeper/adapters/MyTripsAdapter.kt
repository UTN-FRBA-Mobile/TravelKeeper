package utn.kotlin.travelkeeper.adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.TripTimeLineActivity
import utn.kotlin.travelkeeper.models.Trip
import java.text.SimpleDateFormat
import java.util.*

class MyTripsAdapter(private val myDataset: MutableList<Trip>) :
    RecyclerView.Adapter<MyTripsAdapter.TripsViewHolder>() {
    private lateinit var context: Context

    class TripsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_trips_list_item, parent, false)

        return TripsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        holder.view.setOnClickListener{
            val tripTimeLine = Intent(context, TripTimeLineActivity::class.java)
            context.startActivity(tripTimeLine)
        }

        holder.view.findViewById<TextView>(R.id.trip_title).text = myDataset[position].title

        val dateFormatter = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
        holder.view.findViewById<TextView>(R.id.trip_dates).text = dateFormatter.format(myDataset[position].startDate)
    }

    override fun getItemCount() = myDataset.size
}