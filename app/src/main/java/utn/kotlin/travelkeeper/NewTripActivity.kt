package utn.kotlin.travelkeeper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class NewTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        this.supportActionBar!!.setTitle(R.string.new_trip_title)

        val button = findViewById<Button>(R.id.button_id)
        button.setOnClickListener {
            Toast.makeText(this@NewTripActivity, "Agregar nuevo destino", Toast.LENGTH_SHORT).show()
        }
    }
}

