package utn.kotlin.travelkeeper.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_my_trips.*
import kotlinx.android.synthetic.main.fragment_my_trips.view.*
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.NewTripActivity
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.adapters.MyTripsAdapter
import utn.kotlin.travelkeeper.models.Trip
import java.util.*


class MyTripsFragment : androidx.fragment.app.Fragment() {

    private var isOldTrips = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_trips, container, false)

        val fab = view.fab_add_trip
        fab.setOnClickListener {
            val newTripIntent = Intent(activity, NewTripActivity::class.java)
            startActivityForResult(newTripIntent, 8080)
        }

        return view
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading.visibility = View.VISIBLE
        if (isOldTrips) {
            fab_add_trip.visibility = View.GONE
            UsuariosService().getOldTrips(
                FirebaseAuth.getInstance().currentUser!!.email!!,
                object : UsuariosService.GOCUsuarioServiceListener {
                    override fun onSuccess(trips: ArrayList<Trip>) {
                        loading.visibility = View.GONE
                        if (trips.isNotEmpty()) {
                            val viewManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

                            Collections.sort(trips, object : Comparator<Trip> {
                                override fun compare(o1: Trip, o2: Trip): Int {
                                    if (o1.startDate.before(o2.startDate)) {
                                        return 1
                                    } else if (o1.startDate.after(o2.startDate)) {
                                        return -1
                                    }

                                    if (o1.endDate.before(o2.endDate)) {
                                        return 1
                                    } else if (o1.endDate.after(o2.endDate)) {
                                        return -1
                                    }

                                    return 0
                                }
                            })

                            val viewAdapter = MyTripsAdapter(trips)
                            viewAdapter.isPast = true
                            my_trips_recycler.apply {
                                layoutManager = viewManager
                                adapter = viewAdapter
                            }

                            my_trips_recycler.visibility = View.VISIBLE
                        } else {
                            my_trips_recycler.visibility = View.GONE
                            empty_view.visibility = View.VISIBLE
                        }
                    }

                    override fun onError(exception: Exception) {
                        loading.visibility = View.GONE
                        Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                        my_trips_recycler.visibility = View.GONE
                        empty_view.visibility = View.VISIBLE
                    }
                })
        } else {
            fab_add_trip.visibility = View.VISIBLE
            UsuariosService().getOrCreateUser(
                FirebaseAuth.getInstance().currentUser!!.email!!,
                object : UsuariosService.GOCUsuarioServiceListener {
                    override fun onSuccess(trips: ArrayList<Trip>) {
                        if(loading == null){
                            return
                        }

                        loading.visibility = View.GONE
                        if (trips.isNotEmpty()) {
                            val viewManager = androidx.recyclerview.widget.LinearLayoutManager(activity)

                            Collections.sort(trips, object : Comparator<Trip> {
                                override fun compare(o1: Trip, o2: Trip): Int {
                                    if (o1.startDate.before(o2.startDate)) {
                                        return -1
                                    } else if (o1.startDate.after(o2.startDate)) {
                                        return 1
                                    }

                                    if (o1.endDate.before(o2.endDate)) {
                                        return -1
                                    } else if (o1.endDate.after(o2.endDate)) {
                                        return 1
                                    }

                                    return 0
                                }
                            })

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
                        loading.visibility = View.GONE
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