package utn.kotlin.travelkeeper

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.inputmethod.InputMethodManager


class NewTripActivity : AppCompatActivity() {

    private lateinit var tripName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)

        this.supportActionBar!!.setTitle(R.string.new_trip_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)

        val newDestinationAdapter = NewDestinationListAdapter()

        tripName = findViewById(R.id.enter_trip_name_edit)

        findViewById<RecyclerView>(R.id.new_destination_recycler_view).apply {
            adapter = newDestinationAdapter
        }

        val button = findViewById<Button>(R.id.add_destination_button)
        button.setOnClickListener {
            newDestinationAdapter.data.add(NewDestination())
            newDestinationAdapter.notifyDataSetChanged()
            hideKeyboard(it)
        }
    }

    private fun hideKeyboard(view: View) {
        try {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (ignored: Exception) {
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

