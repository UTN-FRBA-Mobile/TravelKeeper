package utn.kotlin.travelkeeper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NewDestinationListAdapter : RecyclerView.Adapter<NewDestinationViewHolder>() {
    val data: MutableList<NewDestination> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDestinationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.new_destination_view, parent, false)
        return NewDestinationViewHolder(view)
    }

//    var data = listOf<String>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: NewDestinationViewHolder, position: Int) {
        val item = data[position]
//        holder.newDestinationView. = item.destination

    }

}

class NewDestinationViewHolder(val newDestinationView: View) : RecyclerView.ViewHolder(newDestinationView) {

}