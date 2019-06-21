package utn.kotlin.travelkeeper.adapters;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.documentation_item.view.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.models.DocumentationInfo

class DocumentationAdapter(
    val documentationList: MutableList<DocumentationInfo>) :
    RecyclerView.Adapter<DocumentationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentationViewHolder {
        val documentItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.documentation_item, parent, false)
        Log.i("[onCreate]", "i")
        return DocumentationViewHolder(documentItem)
    }

    override fun onBindViewHolder(holder: DocumentationViewHolder, position: Int) {
        Log.i("[onBind]", position.toString())
        val documentInfo = documentationList[position]
        holder.view.documentation_file_name.text = documentInfo.fileName
        holder.view.documentation_file_type.text = documentInfo.type

    }

    override fun getItemCount(): Int = documentationList.size
}

class DocumentationViewHolder(val view: View) : RecyclerView.ViewHolder(view)