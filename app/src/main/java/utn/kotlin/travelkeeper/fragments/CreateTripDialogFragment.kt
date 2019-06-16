package utn.kotlin.travelkeeper.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_dialog_create_trip.*
import utn.kotlin.travelkeeper.DBServices.UsuariosService
import utn.kotlin.travelkeeper.DBServices.ViajesService
import utn.kotlin.travelkeeper.R
import java.text.SimpleDateFormat
import java.util.*


class CreateTripDialogFragment : androidx.fragment.app.DialogFragment() {

    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog_MinWidth)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dialog_create_trip, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        start_date_label.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val myCalendar = Calendar.getInstance()
                if (startDate != null) {
                    myCalendar.time = startDate
                }

                DatePickerDialog(
                    activity!!,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        startDate = myCalendar.time

                        start_date_label.text =
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(startDate)
                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        end_date_label.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val myCalendar = Calendar.getInstance()
                if (endDate != null) {
                    myCalendar.time = endDate
                }

                DatePickerDialog(
                    activity!!,
                    DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                        myCalendar.set(Calendar.YEAR, year)
                        myCalendar.set(Calendar.MONTH, monthOfYear)
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        endDate = myCalendar.time

                        end_date_label.text = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(endDate)
                    },
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        })

        create_button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!(startDate != null && endDate != null && trip_name_text.text.toString().isNotEmpty())) {
                    Toast.makeText(
                        activity,
                        "Por favor completar todo",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                ViajesService().createTrip(
                    trip_name_text.text.toString(),
                    startDate!!,
                    endDate!!,
                    object : ViajesService.CreateTripServiceListener {
                        override fun onSuccess(idCreated: String) {
                            UsuariosService().addTripToUser(
                                FirebaseAuth.getInstance().currentUser!!.email!!,
                                idCreated,
                                trip_name_text.text.toString(),
                                startDate!!,
                                endDate!!,
                                object : UsuariosService.SimpleServiceListener {
                                    override fun onSuccess() {
                                        Toast.makeText(activity, "Insertado, recargar mis viajes", Toast.LENGTH_LONG)
                                            .show()
                                        dialog.dismiss()
                                    }

                                    override fun onError(exception: Exception) {
                                        Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }

                        override fun onError(exception: Exception) {
                            Toast.makeText(activity, exception.message, Toast.LENGTH_LONG).show()
                        }
                    }

                )
            }
        })
    }

}