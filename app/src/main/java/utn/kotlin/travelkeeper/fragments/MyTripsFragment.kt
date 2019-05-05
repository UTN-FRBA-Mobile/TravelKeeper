package utn.kotlin.travelkeeper.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_my_trips.view.*
import utn.kotlin.travelkeeper.NewTripActivity
import utn.kotlin.travelkeeper.R




class MyTripsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_trips, container, false)

        val fab = view.fab_add_trip
        fab.setOnClickListener { view ->
            val newTripIntent = Intent(activity, NewTripActivity::class.java)
            startActivity(newTripIntent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyTripsFragment()
    }
}