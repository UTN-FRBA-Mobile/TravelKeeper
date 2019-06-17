package utn.kotlin.travelkeeper

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewDestinationListAdapter(private val data: List<String>) : RecyclerView.Adapter<NewDestinationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewDestinationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.borrar_text_view, parent, false)
        return NewDestinationViewHolder(view as TextView)
    }

//    var data = listOf<String>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: NewDestinationViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = item


    }

}

class NewDestinationViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)