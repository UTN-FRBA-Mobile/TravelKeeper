package utn.kotlin.travelkeeper.utils

import android.app.Activity
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

object Keyboard {

    fun hide(view: View, activity: Activity) {
        try {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (ignored: Exception) {
        }
    }

    fun onEnterHideKeyboard(activity: Activity): TextView.OnEditorActionListener {
        return object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (event != null && event.keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    hide(v, activity)
                }
                return false
            }
        }
    }
}