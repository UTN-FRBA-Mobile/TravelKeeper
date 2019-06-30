package utn.kotlin.travelkeeper

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_search_airlines2.*

class AirlineSearchActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_airlines2)

        val airlines = mutableListOf("no tengo idea", "jdkfdsk", "te voy a matar marcos", "holiii")
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, airlines)

        airlines_list_view.adapter = adapter

        airlines_list_view.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val airline = adapter.getItem(position)
                val intent = Intent(this@AirlineSearchActivity, NewFlightActivity::class.java)
                intent.putExtra("FLIGHT_AIRLINE", airline as String)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val search = menu?.findItem(R.id.action_search) ?: return false
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Buscar aerolíneas"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }
}