package utn.kotlin.travelkeeper

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class TripDashboardActivity : AppCompatActivity(), EditTripNameDialog.EditTitleDialogListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip)
        val cityTitle = findViewById<TextView>(R.id.city_title)
        cityTitle.text = (intent.getStringExtra("cityName"))
        val editButton = findViewById<Button>(R.id.edit_name)
        editButton.setOnClickListener {
            val dialog = EditTripNameDialog()
            dialog.show(supportFragmentManager, "DialogTitle")
        }
    }

    override fun onDialogPositiveClick(title:String) {
        findViewById<TextView>(R.id.city_title).text = title

    }

    override fun onDialogNegativeClick(dialog: androidx.fragment.app.DialogFragment) {
        // User touched the dialog's negative button
    }

}