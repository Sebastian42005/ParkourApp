package com.vig.sebastian.snapchat.meetup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import java.lang.Exception

class CreateMeetUpActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meet_up)
        val locationEditText: EditText = findViewById(R.id.meetUpLocationEditText)
        val dateEditText: EditText = findViewById(R.id.meetUpDateEditText)
        val durationEditText: EditText = findViewById(R.id.meetUpDurationEditText)
        val descriptionEditText: EditText = findViewById(R.id.meetUpDescriptionEditText)
        val createMeetUpBtn: Button = findViewById(R.id.createMeetUpBtn)
        val datePickerImageView : ImageView = findViewById(R.id.meetUpDatePickerImageView)
        val saveDateBtn: Button  = findViewById(R.id.meetUpSaveDateTimeButton)
        val grayBackgroundView: View = findViewById(R.id.meetUpShowDateTimeLayout)
        val datePicker: DatePicker = findViewById(R.id.meetUpDatePicker)
        val backBtn : ImageView = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        val key = intent.getStringExtra("key")!!

        datePickerImageView.setOnClickListener {
            hideKeyboard()
            grayBackgroundView.visibility = View.VISIBLE
            saveDateBtn.visibility = View.VISIBLE
            datePicker.visibility = View.VISIBLE
        }

        saveDateBtn.setOnClickListener {
            dateEditText.setText("${datePicker.dayOfMonth}.${datePicker.month}.${datePicker.year} 12:00")
            grayBackgroundView.visibility = View.GONE
            saveDateBtn.visibility = View.GONE
            datePicker.visibility = View.GONE
        }

        createMeetUpBtn.setOnClickListener {
            try {
                Global.getDateFromString(
                    dateEditText.text.toString().replace(".", "-").trim(),
                    Global.basicFormat)
                if (durationEditText.length() != 0) {
                    if (locationEditText.length() != 0) {
                        Database.createMeetUp(MeetUp(
                            dateEditText.text.toString().trim(),
                            durationEditText.text.toString().trim(),
                            locationEditText.text.toString().trim(),
                            descriptionEditText.text.toString().trim(),
                            key, key))
                        Toast.makeText(this, "Meet Up successfully created", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                    } else locationEditText.error = "Location can't be empty"
                } else durationEditText.error = "Duration can't be empty"
            }catch (e: Exception) {
                dateEditText.error = "dd.MM.YYYY HH:mm"
            }
        }
    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}