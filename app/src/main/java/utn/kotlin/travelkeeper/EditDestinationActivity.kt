package utn.kotlin.travelkeeper

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class EditDestinationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_destination)

        setBackArrow()
    }

    private fun setBackArrow() {
        this.supportActionBar!!.setTitle(R.string.edit_destination_title)
        this.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.supportActionBar!!.setDisplayShowHomeEnabled(true)
    }
}
