package utn.kotlin.travelkeeper.features.trips

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.my_trips_list_item.view.*
import utn.kotlin.travelkeeper.MainActivity
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.database.UsuariosService
import utn.kotlin.travelkeeper.domain.Trip
import utn.kotlin.travelkeeper.features.documents.DocumentationActivity
import utn.kotlin.travelkeeper.utils.InternetConnection
import utn.kotlin.travelkeeper.utils.parserWithFormat

class MyTripsAdapter(private val myDataset: List<Trip>) : RecyclerView.Adapter<MyTripsAdapter.TripsViewHolder>() {
    private lateinit var context: Context
    var isPast = false

    private var firstTitleUsed = false
    private var secondTitleUsed = false

    class TripsViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripsViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_trips_list_item, parent, false)

        return TripsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TripsViewHolder, position: Int) {
        holder.view.setOnClickListener {
            val tripTimeLineIntent = Intent(context, TripTimeLineActivity::class.java)
            tripTimeLineIntent.putExtra("TRIP", myDataset[position])
            context.startActivity(tripTimeLineIntent)
        }

        holder.view.documentation_btn.setOnClickListener {
            val documentationIntent = Intent(context, DocumentationActivity::class.java)
            documentationIntent.putExtra("tripId", myDataset[position].id)
            context.startActivity(documentationIntent)
        }

        holder.view.findViewById<ImageView>(R.id.share_btn).setOnClickListener {
            ShareCompat.IntentBuilder.from(context as Activity?)
                .setText("Te invito a que viajemos juntos! Planifiquemos este viaje a través de TravelKeeper. http://travelkeeper.share/" + myDataset[position].id)
                .setType("text/plain")
                .setChooserTitle("Compartir el viaje en...")
                .startChooser()
        }

        holder.view.findViewById<ImageView>(R.id.delete_btn).setOnClickListener {
            if(InternetConnection.verifyAvailableNetwork(context as AppCompatActivity)) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.remove_trip_from_library)
                builder.setPositiveButton(
                    R.string.yes
                ) { dialog, _ ->
                    UsuariosService().removeTripFromUser(
                        FirebaseAuth.getInstance().currentUser!!.email!!,
                        myDataset[position].id!!,
                        object : UsuariosService.SimpleServiceListener {
                            override fun onSuccess() {
                                Toast.makeText(context, R.string.trip_removed_from_library, Toast.LENGTH_SHORT).show()
                                (context as MainActivity).onNavigationItemSelected(
                                    (context as MainActivity).nav_view.menu.getItem(
                                        0
                                    )
                                )
                            }

                            override fun onError(exception: Exception) {
                                Toast.makeText(context, R.string.trip_not_removed_from_library, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                builder.setNegativeButton(
                    R.string.no
                ) { dialog, _ -> dialog.dismiss() }

                builder.create().show()
                }
            else {
                InternetConnection.alertNoInternet(context)
            }
        }

        holder.view.findViewById<TextView>(R.id.trip_title).text = myDataset[position].title

        val dateFormatter = parserWithFormat(holder.view.context.getString(R.string.my_trips_date_format))

        val datesString = dateFormatter.format(myDataset[position].startDate) + " - " +
                dateFormatter.format(myDataset[position].endDate)

        holder.view.findViewById<TextView>(R.id.trip_dates).text = datesString

        if (myDataset[position].isTripNow()) {
            if (!firstTitleUsed) {
                holder.view.findViewById<TextView>(R.id.section_title).text =
                    holder.view.context.getString(R.string.current)
                holder.view.findViewById<TextView>(R.id.section_title).visibility = View.VISIBLE
                firstTitleUsed = true
            }
        } else {
            if (!secondTitleUsed) {
                if (isPast) {
                    holder.view.findViewById<TextView>(R.id.section_title).text =
                        holder.view.context.getString(R.string.old_trips_menu_title)
                } else {
                    holder.view.findViewById<TextView>(R.id.section_title).text =
                        holder.view.context.getString(R.string.next_trips)
                }
                holder.view.findViewById<TextView>(R.id.section_title).visibility = View.VISIBLE
                secondTitleUsed = true
            }
        }
    }

    override fun getItemCount() = myDataset.size
}