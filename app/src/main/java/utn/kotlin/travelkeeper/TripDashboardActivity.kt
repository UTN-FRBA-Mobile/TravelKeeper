package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import utn.kotlin.travelkeeper.storage.FileStorageService

class TripDashboardActivity : AppCompatActivity(), EditTripNameDialog.EditTitleDialogListener {

    val PICK_FILE = 1
    val PICK_DIRECTORY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_dashboard)
        val destinationId = intent.getStringExtra("DESTINATION_ID")
        val tripId = intent.getStringExtra("TRIP_ID")

        val cityTitle = findViewById<TextView>(R.id.city_title)
        cityTitle.text = (intent.getStringExtra("cityName"))
        val editButton = findViewById<FloatingActionButton>(R.id.edit_name)
        editButton.setOnClickListener {
            val dialog = EditTripNameDialog()
            dialog.show(supportFragmentManager, "DialogTitle")
        }

        val accommodationButton = findViewById<Button>(R.id.accommodation_button)
        accommodationButton.setOnClickListener{
            val intentAccommodation = Intent(this,AccomodationsListActivity::class.java )
            intentAccommodation.putExtra("DESTINATION_ID", destinationId)
            intentAccommodation.putExtra("TRIP_ID", tripId)
            startActivity(intentAccommodation)
        }

        val destination = findViewById<Button>(R.id.transportation_button)
        destination.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select file"), PICK_FILE)
        }

        val activity = findViewById<Button>(R.id.activity_button)
        activity.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivityForResult(Intent.createChooser(intent, "Choose save directory"), PICK_DIRECTORY)
        }
    }

    override fun onDialogPositiveClick(title: String) {
        findViewById<TextView>(R.id.city_title).text = title

    }

    override fun onDialogNegativeClick(dialog: androidx.fragment.app.DialogFragment) {
        // User touched the dialog's negative button
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                val fileName = getFileName(data!!.data!!)
                FileStorageService().uploadFile(
                    data!!.data!!,
                    "tripId",
                    fileName
                ).addOnSuccessListener { FileStorageService().getFile("tripId", fileName) }
            }
        }
        if (requestCode == PICK_DIRECTORY) {
            if (resultCode == Activity.RESULT_OK) {
                val task = FileStorageService().getFile(
                    "tripId",
                    "aa.jpg"
                )
                if (task != null) {
                    task.addOnSuccessListener {

                    }
                    task.addOnFailureListener {
                        it.message
                    }
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


