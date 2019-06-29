package utn.kotlin.travelkeeper.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.trip_timeline_view.view.*
import utn.kotlin.travelkeeper.R.drawable
import utn.kotlin.travelkeeper.R.layout
import utn.kotlin.travelkeeper.TripDashboardActivity
import utn.kotlin.travelkeeper.models.*
import utn.kotlin.travelkeeper.utils.toStringDateOnly

class TripTimeLineAdapter(private val tripElements: MutableList<TripElement>, private val trip: Trip) :
    androidx.recyclerview.widget.RecyclerView.Adapter<TripTimeLineAdapter.TripTimeLineViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TripTimeLineAdapter.TripTimeLineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layout.trip_timeline_view, parent, false)
        context = parent.context

        return TripTimeLineViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TripTimeLineViewHolder, position: Int) {
        when (tripElements[position].getType()) {
            TripElementType.FLIGHT -> {
                val flight = tripElements[position] as Flight
                holder.view.trip_info_date.text = flight.takeOffDate.toStringDateOnly()
                holder.view.trip_info_detail.text = flight.departureAirport + " - " + flight.arrivalAirport
                holder.view.trip_timeline.marker = ContextCompat.getDrawable(context, drawable.ic_airplane)
            }

            TripElementType.DESTINATION -> {
                val destination = tripElements[position] as Destination
                holder.view.trip_info_date.text =
                    destination.startDate?.toStringDateOnly() + " - " + destination.endDate?.toStringDateOnly()
                holder.view.trip_info_detail.text = destination.name
                holder.view.setOnClickListener {
                    showDashboard(position)
                }
            }
        }

        holder.view.tag = position

        val activity = context as Activity
        activity.registerForContextMenu(holder.view)
    }


    private fun showDashboard(position: Int) {
        val intent = Intent(context, TripDashboardActivity::class.java).apply {
            putExtra(
                "DESTINATION_ID",
                (tripElements[position] as Destination).id
            )
            putExtra(
                "TRIP_ID",
                trip.id
            )
        }

        context.startActivity(intent)
    }

    override fun getItemCount() = tripElements.size

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