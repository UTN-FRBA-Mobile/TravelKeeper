package utn.kotlin.travelkeeper.fragments

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import utn.kotlin.travelkeeper.R

class MyTripsFragmet : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_trips, container, false)
        return view
    }


    companion object {
        @JvmStatic
        fun newInstance() = MyTripsFragmet()
    }
}