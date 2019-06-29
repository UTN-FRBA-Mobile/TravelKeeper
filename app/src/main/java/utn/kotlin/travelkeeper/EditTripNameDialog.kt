package utn.kotlin.travelkeeper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import kotlinx.android.synthetic.main.input_photo_title_dialog.*

class EditTripNameDialog : androidx.fragment.app.DialogFragment() {


    internal lateinit var listener: EditTitleDialogListener

    interface EditTitleDialogListener {
        fun onDialogPositiveClick(title: String)
        fun onDialogNegativeClick(dialog: androidx.fragment.app.DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as EditTitleDialogListener
        } catch (e: ClassCastException) {

            throw ClassCastException((context.toString() +
                    " must implement EditTitleDialogListener "))
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val input = EditText(it)
            builder.setView(inflater.inflate(R.layout.input_photo_title_dialog, null))
                .setPositiveButton("OK")
                    { dialog, id ->
                        val text = getDialog().editText.text.toString()
                        listener.onDialogPositiveClick(text)
                    }
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->

                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}