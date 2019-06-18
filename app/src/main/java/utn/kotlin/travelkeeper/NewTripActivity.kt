package utn.kotlin.travelkeeper

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class NewTripActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        this.supportActionBar!!.setTitle(R.string.new_trip_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        val newDestinationAdapter = NewDestinationListAdapter()

        findViewById<RecyclerView>(R.id.new_destination_recycler_view).apply {
            adapter = newDestinationAdapter
        }

        val button = findViewById<Button>(R.id.add_destination_button)
        button.setOnClickListener {
            newDestinationAdapter.data.add(NewDestination())
            newDestinationAdapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //para back button
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

