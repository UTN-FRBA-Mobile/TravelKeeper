package utn.kotlin.travelkeeper.utils

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import utn.kotlin.travelkeeper.R

object InternetConnection {

    fun verifyAvailableNetwork(activity: AppCompatActivity):Boolean{
        val connectivityManager= activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo= connectivityManager.activeNetworkInfo
        return  networkInfo != null && networkInfo.isConnected
    }

    fun alertNoInternet(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(R.string.no_internet)
        builder.setPositiveButton(
            R.string.accept
        ) { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}