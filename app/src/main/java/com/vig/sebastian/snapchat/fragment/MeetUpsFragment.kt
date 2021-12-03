package com.vig.sebastian.snapchat.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.test.database.Database
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.meetup.MeetUpAdapter
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.meetup.MeetUp
import com.vig.sebastian.snapchat.meetup.MeetUpDetailsActivity
import com.vig.sebastian.snapchat.meetup.MeetUpObject
import java.lang.Exception
import kotlin.collections.ArrayList

class MeetUpsFragment : Fragment() {
    var meetUpList = ArrayList<MeetUp>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_meet_ups, container, false)
        val meetUpListView = root.findViewById<ListView>(R.id.meetUpListView)
        meetUpListView.setOnItemClickListener { parent, view, position, id ->
            MeetUpObject.startDate = meetUpList[position].startDate
            MeetUpObject.duration = meetUpList[position].duration
            MeetUpObject.description = meetUpList[position].description
            MeetUpObject.location = meetUpList[position].location
            MeetUpObject.key = meetUpList[position].key
            try {
                startActivity(Intent(requireContext(), MeetUpDetailsActivity::class.java))
            } catch (e: Exception) {}
        }
        setMeetUpList(root)

        return root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMeetUpList(root: View) {
        Database.getUserMeetUps(Global.username) { meetUpList ->
            meetUpList.sortWith(compareBy{it.startDate})
            meetUpList.sort()
            this.meetUpList = meetUpList
            val meetUpListView = root.findViewById<ListView>(R.id.meetUpListView)
            try {
                val adapter = MeetUpAdapter(requireContext(), R.layout.meet_up_layout, meetUpList)
                meetUpListView.adapter = adapter
            }catch (e: Exception) {}
        }
    }
}