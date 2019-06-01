package utn.kotlin.travelkeeper.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_my_trips.*
import kotlinx.android.synthetic.main.fragment_my_trips.view.*
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.adapters.MyTripsAdapter
import utn.kotlin.travelkeeper.models.Trip


class MyTripsFragment : Fragment() {

    private var isOldTrips = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_trips, container, false)

        val fab = view.fab_add_trip
        fab.setOnClickListener {
            //TODO DESCOMENTAR PARA VOLVER AL OTRO COMPORTAMIENTO, Y COMENTAR A PARTIR DE ********
//            val newTripIntent = Intent(activity, NewTripActivity::class.java)
//            startActivity(newTripIntent)

            //*************//
            val newFragment = CreateTripDialogFragment()
            newFragment.show(fragmentManager, "dialog")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(isOldTrips){
            UsuariosService().getOldTrips(
                FirebaseAuth.getInstance().currentUser!!.email!!,
                object : UsuariosService.GOCUsuarioServiceListener {
                    override fun onSuccess(trips: List<Trip>) {
                        if (trips.isNotEmpty()) {
                            val viewManager = LinearLayoutManager(activity)

                            val viewAdapter = MyTripsAdapter(trips)
                            my_trips_recycler.apply {
                                layoutManager = viewManager
                                adapter = viewAdapter
                            }

                            my_trips_recycler.visibility = View.VISIBLE
                            empty_view.visibility = View.GONE
                        } else {
                            my_trips_recycler.visibility = View.GONE
                            empty_view.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(exception: Exception) {
                        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                        my_trips_recycler.visibility = View.GONE
                        empty_view.visibility = View.VISIBLE
                    }
                })
        }else{
            UsuariosService().getOrCreateUser(
                FirebaseAuth.getInstance().currentUser!!.email!!,
                object : UsuariosService.GOCUsuarioServiceListener {
                    override fun onSuccess(trips: List<Trip>) {
                        if (trips.isNotEmpty()) {
                            val viewManager = LinearLayoutManager(activity)

                            val viewAdapter = MyTripsAdapter(trips)
                            my_trips_recycler.apply {
                                layoutManager = viewManager
                                adapter = viewAdapter
                            }

                            my_trips_recycler.visibility = View.VISIBLE
                            empty_view.visibility = View.GONE
                        } else {
                            my_trips_recycler.visibility = View.GONE
                            empty_view.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(exception: Exception) {
                        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                        my_trips_recycler.visibility = View.GONE
                        empty_view.visibility = View.VISIBLE
                    }
                })
        }
    }

    fun setIsOldTrips(oldtrips: Boolean) {
        this.isOldTrips = oldtrips
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyTripsFragment()
    }
}