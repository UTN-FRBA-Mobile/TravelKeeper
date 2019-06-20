package utn.kotlin.travelkeeper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.view_trip_time_line.view.*
import utn.kotlin.travelkeeper.R.*
import utn.kotlin.travelkeeper.TripDashboardActivity
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.text.SimpleDateFormat
import java.util.*

class TripTimeLineAdapter(private val destinations: MutableList<TripTimeLineInfo>, private val trip: Trip) :
    androidx.recyclerview.widget.RecyclerView.Adapter<TripTimeLineAdapter.TripTimeLineViewHolder>() {
    private lateinit var context: Context

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripTimeLineAdapter.TripTimeLineViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
            .inflate(layout.view_trip_time_line, parent, false)
        // set the view's size, margins, paddings and layout parameters
        context = parent.context

        return TripTimeLineViewHolder(view, viewType)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TripTimeLineViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.trip_info_date.text =
            getDate(destinations[position].start_date) + " - " + getDate(destinations[position].end_date)
        holder.view.trip_info_detail.text = destinations[position].name

        if (destinations[position].type == "Vuelo") {
            holder.view.trip_timeline.marker = ContextCompat.getDrawable(context, drawable.ic_airplane)
        }

        holder.view.setOnClickListener {
            if (destinations[position].type == "Lugar")
                showDashboard(position)
        }

        holder.view.tag = position

        var activity = context as Activity
        activity.registerForContextMenu(holder.view)
    }



    private fun showDashboard(position: Int) {
        val intent = Intent(context, TripDashboardActivity::class.java).apply {
            putExtra(
                "DESTINATION_ID",
                destinations[position].id
            )
            putExtra(
                "TRIP_ID",
                trip.id
            )
        }

        context.startActivity(intent)
    }

    private fun getDate(date: Date): String {
        val myFormat = "dd/MM/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale("es", "ES"))

        return sdf.format(date)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = destinations.size

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class TripTimeLineViewHolder(val view: View, viewType: Int) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val timeline = view.trip_timeline

        init {
            timeline.initLine(viewType)
        }
    }
}