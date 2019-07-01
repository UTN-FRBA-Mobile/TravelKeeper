package utn.kotlin.travelkeeper.features.documents

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import kotlinx.android.synthetic.main.input_photo_title_dialog.*
import utn.kotlin.travelkeeper.R

class InputFileNameDialog(var listener: InputFileNameListener) : androidx.fragment.app.DialogFragment() {

    interface InputFileNameListener {
        fun onDialogPositiveClick(fileName: String)
        fun onDialogNegativeClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.input_photo_title_dialog, null))
                .setPositiveButton("OK")
                { dialog, id ->
                    val text = getDialog().input_photo_edit.text.toString()
                    //if(text.isEmpty() || text.isBlank()) Toast.makeText(context, "Por favor ingrese un nombre de archivo", Toast.LENGTH_SHORT).show()
                    listener.onDialogPositiveClick(text)
                }
                .setNegativeButton("Cancelar")
                { dialog, id ->
                    listener.onDialogNegativeClick()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}