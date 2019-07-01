package utn.kotlin.travelkeeper.features.documents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_documentation.*
import utn.kotlin.travelkeeper.InputFileNameDialog
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.database.ViajesService
import utn.kotlin.travelkeeper.domain.DocumentationInfo
import utn.kotlin.travelkeeper.utils.FileCopyHelper
import utn.kotlin.travelkeeper.utils.Permissions
import utn.kotlin.travelkeeper.utils.Permissions.checkForPermissions
import java.io.File
import java.net.URI

class DocumentationActivity : AppCompatActivity() {

    lateinit var tripId: String
    val PICK_FILE = 1
    val REQUEST_IMAGE_CAPTURE = 2
    var pendingShowRecycler = false
    var cameraPhotoPath: Uri? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: DocumentationAdapter
    private lateinit var cameraNewDocumentFilename: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documentation)
        //setSupportActionBar(doc_toolbar)
        setBackArrow()
        tripId = intent.getStringExtra("tripId")


        checkForPermissions(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "Necesitamos guardar los documentos de tu viaje",
            object : Permissions.Callback {
                override fun onRequestSent() {
                    pendingShowRecycler = true
                }

                override fun onPermissionAlreadyGranted() {
                    showRecyclerDocuments()
                }
            }, Permissions.REQUEST_WRITE_EXTERNAL_STORAGE
        )

        attach_fab.setOnClickListener { view ->
            checkForPermissions(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Necesitamos guardar los documentos de tu viaje",
                object : Permissions.Callback {
                    override fun onRequestSent() {
                        Toast.makeText(this@DocumentationActivity, "Requesting permission", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionAlreadyGranted() {
                        startSelectFileIntent()
                    }
                },
                Permissions.REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }

        camera_fab.setOnClickListener {
            checkForPermissions(
                this,
                android.Manifest.permission.CAMERA,
                "Necesitamos acceder a tu camara",
                object : Permissions.Callback {
                    override fun onRequestSent() {
                        Toast.makeText(this@DocumentationActivity, "Requesting permission", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionAlreadyGranted() {
                        startCameraIntent()
                    }
                },
                Permissions.REQUEST_CAMERA
            )
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
                val fileName = getFileName(data!!.data!!).replace(" ", "_")
                val uri = data.data!!
                loading_documentation.visibility = View.VISIBLE
                FileStorageService().uploadFile(
                    uri,
                    tripId,
                    fileName
                )
                copyFileToDirectory(uri, fileName, tripId, this@DocumentationActivity)
                //FileStorageService().getFile(tripId, fileName)
                ViajesService().addDocumentToTrip(
                    tripId,
                    DocumentationInfo(fileName, FileStorageService().getFileExtension(fileName)!!, ""),
                    object : ViajesService.AddDocumentationListener {
                        override fun onSuccess(id: String) {
                            val newDocument = DocumentationInfo(
                                fileName,
                                FileStorageService().getFileExtension(fileName)!!,
                                id
                            )
                            viewAdapter.documentationList.replaceAll {
                                if (it.fileName == fileName) newDocument else it
                            }
                        }

                        override fun onError(exception: Exception) {
                            Toast.makeText(this@DocumentationActivity, exception.message, Toast.LENGTH_LONG).show()
                            loading_documentation.visibility = View.GONE
                        }
                    }
                )
                viewAdapter.documentationList.add(
                    DocumentationInfo(
                        fileName,
                        FileStorageService().getFileExtension(fileName)!!,
                        ""
                    )
                )
                viewAdapter.notifyDataSetChanged()
                if (viewAdapter.documentationList.isEmpty()) {
                    empty_document_textview.visibility = View.VISIBLE
                } else empty_document_textview.visibility = View.GONE
                Toast.makeText(this@DocumentationActivity, "Guardado Ok", Toast.LENGTH_LONG).show()
                loading_documentation.visibility = View.GONE

            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this@DocumentationActivity, cameraNewDocumentFilename, Toast.LENGTH_SHORT)
                loading_documentation.visibility = View.VISIBLE
                FileStorageService().uploadFile(
                    cameraPhotoPath!!,
                    tripId,
                    cameraNewDocumentFilename
                )
                ViajesService().addDocumentToTrip(
                    tripId,
                    DocumentationInfo(
                        cameraNewDocumentFilename,
                        FileStorageService().getFileExtension(cameraNewDocumentFilename)!!,
                        ""
                    ),
                    object : ViajesService.AddDocumentationListener {
                        override fun onSuccess(id: String) {
                            viewAdapter.documentationList.add(
                                DocumentationInfo(
                                    cameraNewDocumentFilename,
                                    FileStorageService().getFileExtension(cameraNewDocumentFilename)!!,
                                    id
                                )
                            )
                            viewAdapter.notifyDataSetChanged()
                            if (viewAdapter.documentationList.isEmpty()) {
                                empty_document_textview.visibility = View.VISIBLE
                            } else empty_document_textview.visibility = View.GONE
                            Toast.makeText(this@DocumentationActivity, "Guardado Ok", Toast.LENGTH_LONG)
                                .show()
                            loading_documentation.visibility = View.GONE
                        }

                        override fun onError(exception: Exception) {
                            Toast.makeText(this@DocumentationActivity, exception.message, Toast.LENGTH_LONG)
                                .show()
                            loading_documentation.visibility = View.GONE
                        }
                    }
                ).addOnCompleteListener {
                    loading_documentation.visibility = View.GONE
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Permissions.REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingShowRecycler) {
                    showRecyclerDocuments()
                } else {
                    startSelectFileIntent()
                }
            }
        }
        if (requestCode == Permissions.REQUEST_CAMERA) {
            if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCameraIntent()
            }
        }
    }

    private fun startSelectFileIntent() {
        val intent = Intent()
        intent.type = "*/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE)
    }

    private fun startCameraIntent() {
        val dialog = InputFileNameDialog(object : InputFileNameDialog.InputFileNameListener {
            override fun onDialogPositiveClick(fileName: String) {
                cameraNewDocumentFilename = fileName.replace(" ", "_")
                if (FileStorageService().getFileExtension(cameraNewDocumentFilename) == null) cameraNewDocumentFilename =
                    cameraNewDocumentFilename + ".jpg"
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        val builder = StrictMode.VmPolicy.Builder()
                        StrictMode.setVmPolicy(builder.build())
                        cameraPhotoPath = FileStorageService().getFileUri(tripId, cameraNewDocumentFilename)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoPath)
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }

            override fun onDialogNegativeClick() {
                Toast.makeText(this@DocumentationActivity, "Foto cancelada", Toast.LENGTH_SHORT)
            }
        })
        dialog.show(supportFragmentManager, "Nuevo documento")
    }


    private fun showRecyclerDocuments() {
        loading_documentation.visibility = View.VISIBLE
        ViajesService().getDocumentsFromTrip(tripId, object : ViajesService.GetDocumentationListener {
            override fun onSuccess(fileList: MutableList<DocumentationInfo>) {
                if (fileList.isEmpty()) {
                    empty_document_textview.visibility = View.VISIBLE
                } else empty_document_textview.visibility = View.GONE
                viewAdapter = DocumentationAdapter(fileList, tripId)
                recyclerView = findViewById<RecyclerView>(R.id.recycler_documentation).apply {
                    adapter = viewAdapter
                    //setHasFixedSize(true)
                }
                loading_documentation.visibility = View.GONE
            }

            override fun onError(exception: Exception) {
                Toast.makeText(this@DocumentationActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun copyFileToDirectory(uri: Uri, fileName: String, tripId: String, context: Context) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = File(URI(FileStorageService().getFileUri(tripId, fileName).toString())).outputStream()
        FileCopyHelper.copyFile(inputStream, outputStream)
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
