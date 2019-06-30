package utn.kotlin.travelkeeper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.fragments.MyTripsFragment
import utn.kotlin.travelkeeper.models.Trip
import utn.kotlin.travelkeeper.ui.login.LoginActivity

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (FirebaseAuth.getInstance().currentUser == null) {
            val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(loginIntent)
            finish()
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        onNavigationItemSelected(nav_view.menu.getItem(0))

        val headerView = nav_view.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.user_name).text = FirebaseAuth.getInstance().currentUser!!.displayName
        headerView.findViewById<TextView>(R.id.user_email).text = FirebaseAuth.getInstance().currentUser!!.email
        val imageView = headerView.findViewById<ImageView>(R.id.user_image)

        val data: Uri? = intent?.data
        if (data != null) {
            ViajesService().getTripDetails(data.pathSegments[0],
                object : ViajesService.GetTripServiceListener {
                    override fun onSuccess(trip: Trip) {
                        val builder = AlertDialog.Builder(this@MainActivity)
                        builder.setMessage(R.string.add_trip_to_library)
                        builder.setPositiveButton(
                            R.string.yes
                        ) { dialog, _ ->
                            dialog.dismiss()
                            UsuariosService().addTripToUser(
                                FirebaseAuth.getInstance().currentUser!!.email!!,
                                trip.id!!,
                                trip.title,
                                trip.startDate,
                                trip.endDate,
                                object : UsuariosService.SimpleServiceListener {
                                    override fun onSuccess() {
                                        Toast.makeText(
                                            this@MainActivity,
                                            R.string.trip_added_to_library,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onNavigationItemSelected(nav_view.menu.getItem(0))
                                    }

                                    override fun onError(exception: Exception) {
                                        Toast.makeText(
                                            this@MainActivity,
                                            R.string.trip_not_added_to_library,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                        builder.setNegativeButton(
                            R.string.no
                        ) { dialog, _ -> dialog.dismiss() }

                        builder.create().show()
                    }

                    override fun onError(exception: Exception) {
                        Toast.makeText(this@MainActivity, R.string.error_fetching_trip, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        Glide.with(this).load(FirebaseAuth.getInstance().currentUser!!.photoUrl)
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private lateinit var airlines_1: List<Airline>

    override fun onStart() {
        super.onStart()

        val service = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // Para parsear automágicamente el json
            .baseUrl("http://demo9783220.mockable.io/")
            .build()
            .create(Airlines::class.java)

        service.getAll().enqueue(object : Callback<AirlinesResponse> {
            override fun onResponse(call: Call<AirlinesResponse>, response: Response<AirlinesResponse>) {
                airlines_1 = response.body()?.airlines!!

//                tweetsAdapter.items = response.body()?.tweets!!
//                tweetsAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<AirlinesResponse>, error: Throwable) {
                Log.e("Sofi", error.message, error)
                Toast.makeText(this@MainActivity, "No tweets founds!", Toast.LENGTH_LONG).show()
            }
        })


    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_my_trips -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_content, MyTripsFragment.newInstance())
                    .commit()
            }

            R.id.nav_old_trips -> {
                val fragment = MyTripsFragment.newInstance()
                fragment.setIsOldTrips(true)
                supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
            }

            R.id.nav_log_out -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.log_out_msg)
                builder.setPositiveButton(
                    R.string.yes
                ) { dialog, _ ->
                    dialog.dismiss()
                    FirebaseAuth.getInstance().signOut()
                    val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                    loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(loginIntent)
                    finish()
                }
                builder.setNegativeButton(
                    R.string.no
                ) { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onNavigationItemSelected(nav_view.menu.getItem(0))
    }
}
