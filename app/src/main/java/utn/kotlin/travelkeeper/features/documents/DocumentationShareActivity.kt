package utn.kotlin.travelkeeper.features.documents

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_documentation_share.*
import utn.kotlin.travelkeeper.R
import utn.kotlin.travelkeeper.database.UsuariosService
import utn.kotlin.travelkeeper.database.ViajesService
import utn.kotlin.travelkeeper.domain.DocumentationInfo
import utn.kotlin.travelkeeper.domain.Trip
import utn.kotlin.travelkeeper.features.login.LoginActivity
import utn.kotlin.travelkeeper.utils.Permissions
import utn.kotlin.travelkeeper.utils.Permissions.checkForPermissions
import java.util.*
import kotlin.collections.ArrayList


class DocumentationShareActivity : AppCompatActivity() {

    private var userTrips: ArrayList<Trip>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_documentation_share)

        if (FirebaseAuth.getInstance().currentUser == null) {
            val loginIntent = Intent(this, LoginActivity::class.java)
            loginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(loginIntent)
            finish()
        }

        setBackArrow()

        checkForPermissions(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            "Necesitamos su permiso para guardar los documentos del viaje",
            object : Permissions.Callback {
                override fun onRequestSent() {
                }

                override fun onPermissionAlreadyGranted() {
                }
            }, Permissions.REQUEST_WRITE_EXTERNAL_STORAGE
        )

        cancel_action.setOnClickListener {
            finish()
        }

        add_action.setOnClickListener {
            if (spinner.selectedItemPosition < 0 || userTrips == null || userTrips?.size!! < 1) {
                Toast.makeText(
                    this@DocumentationShareActivity,
                    "Debe seleccionar un viaje para adjuntar el archivo",
                    Toast.LENGTH_LONG
                ).show()
            }

            uploadFile(userTrips!![spinner.selectedItemPosition].id!!)
        }

        loading.visibility = View.VISIBLE
        cancel_action.isEnabled = false
        add_action.isEnabled = false
        loading.bringToFront()
        UsuariosService().getOrCreateUser(
            FirebaseAuth.getInstance().currentUser!!.email!!,
            object : UsuariosService.GOCUsuarioServiceListener {
                override fun onSuccess(trips: ArrayList<Trip>) {
                    loading.visibility = View.GONE
                    if (trips.isNotEmpty()) {
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
                        userTrips = trips

                        val nameList = ArrayList<String>()
                        trips.forEach {
                            nameList.add(it.title)
                        }

                        val dataAdapter = ArrayAdapter(
                            this@DocumentationShareActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            nameList
                        )

                        spinner.adapter = dataAdapter
                    }
                    
                    cancel_action.isEnabled = true
                    add_action.isEnabled = true
                }

                override fun onError(exception: Exception) {
                    loading.visibility = View.GONE
                    Toast.makeText(this@DocumentationShareActivity, exception.message, Toast.LENGTH_SHORT).show()
                    cancel_action.isEnabled = true
                    add_action.isEnabled = true
                }
            })

        val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        fileName.text = getFileName(uri!!)
    }


    private fun setBackArrow() {
        this.supportActionBar!!.title = "Documentos"
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun uploadFile(tripId: String) {
        loading.visibility = View.VISIBLE
        cancel_action.isEnabled = false
        add_action.isEnabled = false
        val uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        val fileName = getFileName(uri!!)
        FileStorageService().uploadFile(
            uri,
            tripId,
            fileName
        ).addOnSuccessListener {
            FileStorageService().getFile(tripId, fileName)
            ViajesService().addDocumentToTrip(
                tripId,
                DocumentationInfo(fileName, FileStorageService().getFileExtension(fileName)!!, ""),
                object : ViajesService.AddDocumentationListener {
                    override fun onSuccess(id: String) {
                        Toast.makeText(
                            this@DocumentationShareActivity,
                            "El documento se guardó correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }

                    override fun onError(exception: Exception) {
                        Toast.makeText(this@DocumentationShareActivity, exception.message, Toast.LENGTH_LONG)
                            .show()
                        loading.visibility = View.GONE
                        cancel_action.isEnabled = true
                        add_action.isEnabled = true
                    }
                })
        }.addOnFailureListener {
            Toast.makeText(this@DocumentationShareActivity, it.message, Toast.LENGTH_LONG)
                .show()
            loading.visibility = View.GONE
            cancel_action.isEnabled = true
            add_action.isEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Permissions.REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.count() > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish()
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}
