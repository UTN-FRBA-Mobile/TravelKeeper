package utn.kotlin.travelkeeper.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_trips.*
import kotlinx.android.synthetic.main.fragment_my_trips.view.*
import utn.kotlin.travelkeeper.NewTripActivity
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.TripTimeLineActivity
import utn.kotlin.travelkeeper.adapters.MyTripsAdapter
import utn.kotlin.travelkeeper.apis.MyTripsApi

class MyTripsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_trips, container, false)

        val fab = view.fab_add_trip
        fab.setOnClickListener {
            val newTripIntent = Intent(activity, NewTripActivity::class.java)
            startActivity(newTripIntent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tripsDataset = MyTripsApi().getTrips();

        val viewManager = LinearLayoutManager(activity)
        val viewAdapter = MyTripsAdapter(tripsDataset)

        my_trips_recycler.apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyTripsFragment()
    }
}