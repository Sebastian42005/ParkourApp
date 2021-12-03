package com.vig.sebastian.snapchat.meetup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.test.database.Database
import com.vig.sebastian.snapchat.R

class MeetUpActivity : AppCompatActivity() {
    lateinit var meetUpList : ArrayList<MeetUp>
    lateinit var meetUpAdapter : MeetUpAdapter
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_up)

        val meetUpListView = findViewById<ListView>(R.id.meetUpListView)
        meetUpList = ArrayList()
        meetUpAdapter = MeetUpAdapter(this, R.layout.meet_up_layout, meetUpList)
        meetUpListView.adapter = meetUpAdapter
        meetUpListView.setOnItemClickListener { parent, view, position, id ->
            MeetUpObject.description = meetUpList[position].description
            MeetUpObject.startDate = meetUpList[position].startDate
            MeetUpObject.duration = meetUpList[position].duration
            MeetUpObject.location = meetUpList[position].location
            startActivity(Intent(this, MeetUpDetailsActivity::class.java))
        }
        setMeetUpList()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMeetUpList() {
        Database.getTeamMeetUps("c6812a3d-3a9e-45e1-a72c-dc0bc7147c19") {meetUpsList ->
            println(meetUpsList)
            for (meetUp in meetUpsList) {
                meetUpList.add(meetUp)
            }
            meetUpAdapter.notifyDataSetChanged()
        }
    }
}