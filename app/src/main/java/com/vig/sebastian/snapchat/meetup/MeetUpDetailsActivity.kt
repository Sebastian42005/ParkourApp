package com.vig.sebastian.snapchat.meetup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.vig.sebastian.snapchat.R

class MeetUpDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_up_details)
        val descriptionTextView : TextView = findViewById(R.id.descriptionTextView)
        val durationTextView : TextView = findViewById(R.id.durationTextView)
        val locationTextView : TextView = findViewById(R.id.locationTextView)
        val startDateTextView : TextView = findViewById(R.id.startDateTextView)
        val backBtn : ImageView = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        descriptionTextView.text = MeetUpObject.description
        durationTextView.text = MeetUpObject.duration
        startDateTextView.text = MeetUpObject.startDate
        locationTextView.text = MeetUpObject.location
    }
}