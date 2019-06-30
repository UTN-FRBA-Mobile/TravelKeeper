package utn.kotlin.travelkeeper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.airline_search_item_view.view.*

class AirlinesAdapter(private val data: MutableList<String>) : RecyclerView.Adapter<AirlineViewHolder>() {

//    private
//    override fun getFilter(): Filter {
//
//        return object : Filter() {
//
//        }
//
//    }
//    private class ItemFilter : Filter() {
//        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
//
//            val filterString = constraint.toString().toLowerCase()
//
//            val results = Filter.FilterResults()
//
//            val list = originalData
//
//            val count = list.size
//            val nlist = ArrayList<String>(count)
//
//            var filterableString: String
//
//            for (i in 0 until count) {
//                filterableString = list.get(i)
//                if (filterableString.toLowerCase().contains(filterString)) {
//                    nlist.add(filterableString)
//                }
//            }
//
//            results.values = nlist
//            results.count = nlist.size()
//
//            return results
//        }
//
//        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
//            filteredData = results.values as ArrayList<String>
//            notifyDataSetChanged()
//        }
//
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AirlineViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.airline_search_item_view, parent, false)

//        context = parent.context
        return AirlineViewHolder(item)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: AirlineViewHolder, position: Int) {
        holder.view.airline_desc_id.text = data[position]
    }

}

class AirlineViewHolder(val view: View) : RecyclerView.ViewHolder(view)
