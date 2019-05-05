package utn.kotlin.travelkeeper

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import utn.kotlin.travelkeeper.fragments.NewTripFragment

class NewTripActivity : AppCompatActivity(), NewTripFragment.OnFragmentInteractionListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_trip)
        this.supportActionBar!!.setTitle(R.string.new_trip_title)
    }

    override fun onFragmentInteraction(uri: Uri) {
    }
}
