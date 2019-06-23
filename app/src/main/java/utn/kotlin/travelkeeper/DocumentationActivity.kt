package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_documentation.*
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.adapters.DocumentationAdapter
import utn.kotlin.travelkeeper.models.DocumentationInfo
import utn.kotlin.travelkeeper.storage.FileStorageService

class DocumentationActivity : AppCompatActivity() {

    lateinit var tripId: String
    val PICK_FILE = 1
    val PICK_DIRECTORY = 2

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: DocumentationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documentation)
        //setSupportActionBar(doc_toolbar)
        setBackArrow()
        tripId = intent.getStringExtra("tripId")
        ViajesService().getDocumentsFromTrip(tripId, object : ViajesService.GetDocumentationListener {
            override fun onSuccess(fileList: MutableList<DocumentationInfo>) {
                viewAdapter = DocumentationAdapter(fileList, tripId)
                recyclerView = findViewById<RecyclerView>(R.id.recycler_documentation).apply {
                    adapter = viewAdapter
                    //setHasFixedSize(true)
                }
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@DocumentationActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })


        attach_fab.setOnClickListener { view ->
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE)
        }


    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle("Documentos")
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                val fileName = getFileName(data!!.data!!)
                val uri = data.data!!
                FileStorageService().uploadFile(
                    uri,
                    tripId,
                    fileName
                ).addOnSuccessListener {
                    FileStorageService().getFile(tripId, fileName)
                    ViajesService().addDocumentToTrip(tripId, DocumentationInfo(fileName, FileStorageService().getFileExtension(fileName)!!, ""), object : ViajesService.AddDocumentationListener{
                        override fun onSuccess(id: String) {
                            viewAdapter.documentationList.add(DocumentationInfo(fileName, FileStorageService().getFileExtension(fileName)!!, id))
                            viewAdapter.notifyDataSetChanged()
                            Toast.makeText(this@DocumentationActivity, "Guardado Ok", Toast.LENGTH_LONG).show()
                        }

                        override fun onError(exception: Exception) {
                            Toast.makeText(this@DocumentationActivity, exception.message, Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }
    }


    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

}
