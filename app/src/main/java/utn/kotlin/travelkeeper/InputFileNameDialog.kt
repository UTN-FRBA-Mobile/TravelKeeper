package utn.kotlin.travelkeeper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.input_photo_title_dialog.*

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
                    val text = getDialog().editText.text.toString()
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