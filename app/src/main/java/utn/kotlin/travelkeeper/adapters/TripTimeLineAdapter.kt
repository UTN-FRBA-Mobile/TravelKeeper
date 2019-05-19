package utn.kotlin.travelkeeper.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import utn.kotlin.travelkeeper.R
import com.github.vipulasri.timelineview.TimelineView
import kotlinx.android.synthetic.main.view_trip_time_line.view.*

class TripTimeLineAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<TripTimeLineAdapter.TripTimeLineViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TripTimeLineAdapter.TripTimeLineViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_trip_time_line, parent, false)
        // set the view's size, margins, paddings and layout parameters

        return TripTimeLineViewHolder(textView, viewType)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: TripTimeLineViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.view.movie_name.text = myDataset[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class TripTimeLineViewHolder(val view: View, viewType: Int) : RecyclerView.ViewHolder(view){
        val timeline = view.trip_timeline

        init {
            timeline.initLine(viewType)
        }
    }
}