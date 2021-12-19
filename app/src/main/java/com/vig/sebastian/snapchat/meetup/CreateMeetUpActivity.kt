package com.vig.sebastian.snapchat.meetup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import java.lang.Exception
import android.app.DatePickerDialog
import java.util.*


class CreateMeetUpActivity : AppCompatActivity() {
    var currentDay = 0
    var currentMonth = 0
    var currentYear = 0
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meet_up)
        val locationEditText: EditText = findViewById(R.id.meetUpLocationEditText)
        val dateEditText: EditText = findViewById(R.id.meetUpDateEditText)
        val endDateEditText: EditText = findViewById(R.id.meetUpEndDateEditText)
        val descriptionEditText: EditText = findViewById(R.id.meetUpDescriptionEditText)
        val createMeetUpBtn: Button = findViewById(R.id.createMeetUpBtn)
        val datePickerImageView : ImageView = findViewById(R.id.meetUpDatePickerImageView)
        val endDatePickerImageView : ImageView = findViewById(R.id.meetUpEndDatePickerImageView)
        val backBtn : ImageView = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        val key = intent.getStringExtra("key")!!

        datePickerImageView.setOnClickListener {
            hideKeyboard()
            dateEditText.clearFocus()
            endDateEditText.clearFocus()
            descriptionEditText.clearFocus()
            locationEditText.clearFocus()
            val c: Calendar = Calendar.getInstance()
            if (currentDay == 0) {
                currentYear = c.get(Calendar.YEAR)
                currentMonth = c.get(Calendar.MONTH)
                currentDay = c.get(Calendar.DAY_OF_MONTH)
            }

            val dpd = DatePickerDialog(this, {_, mYear, mMonth,mDay ->
                dateEditText.setText("$mDay.${mMonth + 1}.$mYear 12:00")
                currentDay = mDay
                currentMonth = mMonth
                currentYear = mYear
            },currentYear, currentMonth, currentDay)
            dpd.show()
        }

        endDatePickerImageView.setOnClickListener {
            hideKeyboard()
            dateEditText.clearFocus()
            endDateEditText.clearFocus()
            descriptionEditText.clearFocus()
            locationEditText.clearFocus()
            val c: Calendar = Calendar.getInstance()
            if (currentDay == 0) {
                currentYear = c.get(Calendar.YEAR)
                currentMonth = c.get(Calendar.MONTH)
                currentDay = c.get(Calendar.DAY_OF_MONTH)
            }

            val dpd = DatePickerDialog(this, {_, mYear, mMonth,mDay ->
                endDateEditText.setText("$mDay.${mMonth + 1}.$mYear 13:00")
            },currentYear, currentMonth, currentDay)
            dpd.show()
        }

        createMeetUpBtn.setOnClickListener {
            try {
                Global.getDateFromString(
                    dateEditText.text.toString().trim(),
                    Global.basicFormat)
                try {
                    Global.getDateFromString(
                        endDateEditText.text.toString().trim(),
                        Global.basicFormat
                    )
                    if (locationEditText.length() != 0) {
                        if (!Global.checkIfDateIsExpired(
                                Global.getDateFromString(
                                    dateEditText.text.toString().trim(), Global.basicFormat
                                )
                            )
                        ) {
                            val startDate = dateEditText.text.toString().trim()
                            val endDate = endDateEditText.text.toString().trim()
                            val location = locationEditText.text.toString().trim()
                            val description = descriptionEditText.text.toString().trim()
                            if (!Global.checkIfDateIsBefore(Global.getDateFromString(endDate, Global.basicFormat), Global.getDateFromString(startDate, Global.basicFormat))) {
                                Database.createMeetUp(
                                    MeetUp(
                                        startDate,
                                        endDate,
                                        location,
                                        description,
                                        key, key
                                    )
                                )
                                Toast.makeText(
                                    this,
                                    getString(R.string.meet_up_created),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                super.onBackPressed()
                            }else {
                                YoYo.with(Techniques.Shake).duration(300).playOn(endDateEditText)
                                endDateEditText.error = getString(R.string.error_date_older_than_start_date)
                                endDateEditText.requestFocus()
                            }
                        } else {
                            YoYo.with(Techniques.Shake).duration(300).playOn(dateEditText)
                            dateEditText.error = getString(R.string.error_date_expired)
                            dateEditText.requestFocus()
                        }
                    } else {
                        YoYo.with(Techniques.Shake).duration(300).playOn(locationEditText)
                        locationEditText.error = getString(R.string.error_empty)
                        locationEditText.requestFocus()
                    }
                }catch (e: Exception) {
                    YoYo.with(Techniques.Shake).duration(300).playOn(endDateEditText)
                    endDateEditText.error = "dd.MM.YYYY HH:mm"
                    endDateEditText.requestFocus()
                }
            }catch (e: Exception) {
                YoYo.with(Techniques.Shake).duration(300).playOn(dateEditText)
                dateEditText.error = "dd.MM.YYYY HH:mm"
                dateEditText.requestFocus()
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