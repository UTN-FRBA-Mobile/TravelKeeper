package utn.kotlin.travelkeeper.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {
    val REQUEST_WRITE_EXTERNAL_STORAGE = 1
    val REQUEST_CAMERA = 2

    private fun hasPermissions(activity: Activity, permissionCode: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permissionCode) == PackageManager.PERMISSION_GRANTED
    }

    fun checkForPermissions(activity: Activity, permissionCode: String, reason: String, callback: Callback, permissionIdentificator: Int) {
        if (!hasPermissions(activity, permissionCode)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionCode)) {
                showPermissionExplanation(
                    activity,
                    permissionCode,
                    reason,
                    permissionIdentificator
                )
            } else {
                dispatchPermissionRequest(
                    activity,
                    permissionCode,
                    permissionIdentificator
                )
            }
            callback.onRequestSent()
        } else {
            callback.onPermissionAlreadyGranted()
        }
    }

    private fun dispatchPermissionRequest(activity: Activity, permissionCode: String, permissionIdentificator: Int) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(permissionCode),
            permissionIdentificator
        )
    }

    private fun showPermissionExplanation(activity: Activity, permissionCode: String, reason: String, permissionIdentificator: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Necesitamos tu permiso")
        builder.setCancelable(true)
        builder.setMessage(reason)
        builder.setPositiveButton("Aceptar") { dialogInterface, i ->
            dispatchPermissionRequest(
                activity,
                permissionCode,
                permissionIdentificator
            )
        }
        builder.show()
    }


    interface Callback {
        fun onPermissionAlreadyGranted()
        fun onRequestSent()
    }
}