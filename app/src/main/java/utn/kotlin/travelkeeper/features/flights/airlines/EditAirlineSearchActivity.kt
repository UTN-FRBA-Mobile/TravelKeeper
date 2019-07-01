package utn.kotlin.travelkeeper.features.flights.airlines

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_search_airlines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.features.flights.EditFlightActivity

class EditAirlineSearchActivity : AppCompatActivity() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var airlines: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_airlines)

        val service = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://5d193ee5930bbf0014ee8ea0.mockapi.io/")
            .build()
            .create(Airlines::class.java)

        service.getAll().enqueue(object : Callback<AirlinesResponse> {
            override fun onResponse(call: Call<AirlinesResponse>, response: Response<AirlinesResponse>) {
                airlines = response.body()?.airlines!!.map { it.name }
                configureListView()
            }

            override fun onFailure(call: Call<AirlinesResponse>, error: Throwable) {
                Log.e("error", "Error al cargar las aerolíneas", error)
                Toast.makeText(
                    this@EditAirlineSearchActivity, "Error al cargar las aerolíneas",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        setBackArrow()
    }

    private fun configureListView() {
        adapter = ArrayAdapter(this@EditAirlineSearchActivity, android.R.layout.simple_list_item_1, airlines)

        airlines_list_view.adapter = adapter

        airlines_list_view.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val airline = adapter.getItem(position)
                val intent = Intent(this@EditAirlineSearchActivity, EditFlightActivity::class.java)
                intent.putExtra("FLIGHT_AIRLINE", airline as String)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle("Aerolíneas")
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
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

