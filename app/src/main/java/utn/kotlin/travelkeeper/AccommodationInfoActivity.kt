package utn.kotlin.travelkeeper

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import utn.kotlin.travelkeeper.DBServices.AccommodationService
import utn.kotlin.travelkeeper.models.Accommodation

class AccommodationInfoActivity : AppCompatActivity() {

    private lateinit var accommodationService: AccommodationService
    private lateinit var accommodation: Accommodation

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        accommodationService = ServiceProvider.accommodationService
        val accommodationId = intent.getStringExtra("ACCOMMODATION_ID")
        val tripId = intent.getStringExtra("TRIP_ID")
        val destinationId = intent.getStringExtra("DESTINATION_ID")
        //accommodationService.getAccomodation(tripId, destinationId, accommodationId, null)


    }
}