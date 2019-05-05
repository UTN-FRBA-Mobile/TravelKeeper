package utn.kotlin.travelkeeper.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import utn.kotlin.travelkeeper.R
import kotlinx.android.synthetic.main.fragment_my_trips.*
import kotlinx.android.synthetic.main.fragment_my_trips.view.*

class MyTripsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_trips, container, false)

        val fab = view.fab_add_trip
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyTripsFragment()
    }
}