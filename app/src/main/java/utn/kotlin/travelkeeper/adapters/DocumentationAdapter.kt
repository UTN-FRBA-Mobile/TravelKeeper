package utn.kotlin.travelkeeper.adapters;

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.documentation_item.view.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.models.DocumentationInfo
import utn.kotlin.travelkeeper.storage.FileStorageService

class DocumentationAdapter(
    val documentationList: MutableList<DocumentationInfo>,
    val tripId: String
) :
    RecyclerView.Adapter<DocumentationViewHolder>() {
    private lateinit var context: Context
    private var fileStorageService: FileStorageService = FileStorageService()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentationViewHolder {
        val documentItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.documentation_item, parent, false)
        context = parent.context
        return DocumentationViewHolder(documentItem)
    }

    override fun onBindViewHolder(holder: DocumentationViewHolder, position: Int) {
        val documentInfo = documentationList[position]
        holder.view.documentation_file_name.text = documentInfo.fileName
        holder.view.documentation_file_type.text = documentInfo.type

        holder.view.findViewById<TextView>(R.id.documentation_file_name).setOnClickListener {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            val uri = fileStorageService.getFileUri(tripId, documentInfo.fileName)
            val mimeTypeMap = MimeTypeMap.getSingleton()
            val openFileIntent = Intent(Intent.ACTION_VIEW)
            val mimeType = mimeTypeMap.getMimeTypeFromExtension(fileStorageService.getFileExtension(uri.path!!))
            openFileIntent.setDataAndType(uri.normalizeScheme(), mimeType);
            openFileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            try {
                context.startActivity(openFileIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No handler for this type of file.", Toast.LENGTH_LONG).show();
            }
        }
        holder.view.findViewById<ImageView>(R.id.delete_document_btn).setOnClickListener {
            fileStorageService.deleteFile(tripId, documentInfo.fileName).addOnCompleteListener {
                ViajesService().deleteDocumentFromTrip(tripId, documentInfo).addOnSuccessListener {
                    fileStorageService.deleteFileFromLocalStorage(tripId, documentInfo.fileName)
                    documentationList.removeAt(position)
                    this.notifyDataSetChanged()
                    Toast.makeText(context, "Archivo borrado", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    override fun getItemCount(): Int = documentationList.size
}

class DocumentationViewHolder(val view: View) : RecyclerView.ViewHolder(view)