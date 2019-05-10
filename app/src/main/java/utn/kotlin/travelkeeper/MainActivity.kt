package utn.kotlin.travelkeeper

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.w3c.dom.Text
import utn.kotlin.travelkeeper.fragments.MyTripsFragment
import utn.kotlin.travelkeeper.ui.login.LoginActivity


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        onNavigationItemSelected(nav_view.menu.getItem(0))

        val header_view = nav_view.getHeaderView(0);
        header_view.findViewById<TextView>(R.id.user_name).text = FirebaseAuth.getInstance().currentUser!!.displayName
        header_view.findViewById<TextView>(R.id.user_email).text = FirebaseAuth.getInstance().currentUser!!.email
        val imageView = header_view.findViewById<ImageView>(R.id.user_image)

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_my_trips -> {
                supportFragmentManager.beginTransaction().replace(R.id.main_content, MyTripsFragment.newInstance())
                    .commit()
            }

            R.id.nav_old_trips -> {
                Toast.makeText(this, "TBD", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_log_out -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.log_out_msg)
                builder.setPositiveButton(R.string.log_out_yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.dismiss()
                        FirebaseAuth.getInstance().signOut()
                        val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(loginIntent)
                        finish()
                    }
                })
                builder.setNegativeButton(R.string.log_out_no, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                        dialog.dismiss()
                    }
                })

                builder.create().show()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
