package utn.kotlin.travelkeeper

import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_trip_time_line.*
import utn.kotlin.travelkeeper.adapters.TripTimeLineAdapter
import utn.kotlin.travelkeeper.models.TripTimeLineInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TripTimeLineActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_time_line)
        setSupportActionBar(toolbar)

        var myDataset = arrayOf(
            TripTimeLineInfo(LocalDate.parse("2019-08-20", DateTimeFormatter.ISO_DATE),"Barcelona"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-27", DateTimeFormatter.ISO_DATE),"Paris"),
            TripTimeLineInfo(LocalDate.parse("2019-08-31", DateTimeFormatter.ISO_DATE),"Brujas")
        )

        viewManager = LinearLayoutManager(this)
        viewAdapter = TripTimeLineAdapter(myDataset)

        recyclerView = findViewById<RecyclerView>(R.id.trip_timeline_recycler_view).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter

            fab.setOnClickListener { view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }
    }

}
