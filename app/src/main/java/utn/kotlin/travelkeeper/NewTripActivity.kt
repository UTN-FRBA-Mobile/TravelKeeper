package utn.kotlin.travelkeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_trip.*
import utn.kotlin.travelkeeper.view.DestinationView

class NewTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        this.supportActionBar!!.setTitle(R.string.new_trip_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        val button = findViewById<Button>(R.id.button_id)
        button.setOnClickListener { //agregar con reglas - ver de crear una lista
            val destinationView = DestinationView(this)
            root_new_trip.addView(destinationView) //agregar vistas con constraint layout
            Toast.makeText(this@NewTripActivity, "Agregar nuevo destino", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.getItemId() === android.R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
}

