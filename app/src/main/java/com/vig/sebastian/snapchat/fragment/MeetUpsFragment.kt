package com.vig.sebastian.snapchat.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.meetup.MeetUpAdapter
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.meetup.MeetUp
import java.lang.Exception
import kotlin.collections.ArrayList

class MeetUpsFragment : Fragment() {
    var meetUpList = ArrayList<MeetUp>()
    var currentMeetUpTeamKey = ""
    var currentMeetUpKey = ""
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_meet_ups, container, false)
        val meetUpListView = root.findViewById<ListView>(R.id.meetUpListView)
        val meetUpDetailLayout: RelativeLayout = root.findViewById(R.id.meetUpDetailLayout)
        val descriptionTextView : TextView = root.findViewById(R.id.descriptionTextView)
        val durationTextView : TextView = root.findViewById(R.id.durationTextView)
        val locationTextView : TextView = root.findViewById(R.id.locationTextView)
        val startDateTextView : TextView = root.findViewById(R.id.startDateTextView)
        val backBtn : ImageView = root.findViewById(R.id.backBtn)
        val acceptMeetUpBtn: Button = root.findViewById(R.id.acceptMeetUpBtn)
        val declineMeetUpBtn: Button = root.findViewById(R.id.declineMeetUpBtn)

        backBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
            Global.wait(300) {
                meetUpDetailLayout.visibility = View.GONE
            }
        }

        acceptMeetUpBtn.setOnClickListener {
            if (acceptMeetUpBtn.text.toString().toLowerCase() != "accepted") {
                Database.acceptMeetUp(currentMeetUpKey, currentMeetUpTeamKey)
                declineMeetUpBtn.visibility = View.GONE
                acceptMeetUpBtn.text = "Accepted"
            }else {
                AlertDialog.Builder(context).setMessage("Decline this meet up?").setPositiveButton("Decline") {_,_->
                    Database.declineMeetUp(currentMeetUpKey, currentMeetUpTeamKey)
                    YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
                    Global.wait(300) {
                        meetUpDetailLayout.visibility = View.GONE
                    }
                }.setNegativeButton("Cancel") {_,_->}.show()
            }
        }

        declineMeetUpBtn.setOnClickListener {
            Database.declineMeetUp(currentMeetUpKey, currentMeetUpTeamKey)
            YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
            Global.wait(300) {
                meetUpDetailLayout.visibility = View.GONE
            }
        }

        meetUpListView.setOnItemClickListener { parent, view, position, id ->
            Database.getAccepted(meetUpList[position].key) {
                if (it) {
                    declineMeetUpBtn.visibility = View.GONE
                    acceptMeetUpBtn.text = "Accepted"
                }
            }
            descriptionTextView.text = meetUpList[position].description
            durationTextView.text = meetUpList[position].duration
            startDateTextView.text = meetUpList[position].startDate
            locationTextView.text = meetUpList[position].location
            currentMeetUpTeamKey = meetUpList[position].teamKey
            currentMeetUpKey = meetUpList[position].key
            meetUpDetailLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInRight).duration(300).playOn(meetUpDetailLayout)
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