package com.vig.sebastian.snapchat.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
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
    var canClick = true
    lateinit var noUploadsLayout: LinearLayout
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
        val endDateTextView : TextView = root.findViewById(R.id.endDateTextView)
        val durationTextView : TextView = root.findViewById(R.id.durationTextView)
        val locationTextView : TextView = root.findViewById(R.id.locationTextView)
        val startDateTextView : TextView = root.findViewById(R.id.startDateTextView)
        val backBtn : ImageView = root.findViewById(R.id.backBtn)
        noUploadsLayout = root.findViewById(R.id.noUploadsLayout)
        val acceptMeetUpBtn: Button = root.findViewById(R.id.acceptMeetUpBtn)
        val declineMeetUpBtn: Button = root.findViewById(R.id.declineMeetUpBtn)
        val refreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout = root.findViewById(R.id.refreshLayout)

        refreshLayout.setColorSchemeColors(Color.rgb(0, 170, 170))
        refreshLayout.setOnRefreshListener {
            setMeetUpList(root)
            refreshLayout.isRefreshing = false
        }

        backBtn.setOnClickListener {
            canClick = false
            meetUpListView.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
            Global.wait(300) {
                meetUpDetailLayout.visibility = View.GONE
                canClick = true
            }
        }

        acceptMeetUpBtn.setOnClickListener {
            if (acceptMeetUpBtn.text.toString().toLowerCase() != getString(R.string.accepted).toLowerCase()) {
                Database.acceptMeetUp(currentMeetUpKey, currentMeetUpTeamKey)
                declineMeetUpBtn.visibility = View.GONE
                acceptMeetUpBtn.text = getString(R.string.accepted)
            }else {
                AlertDialog.Builder(context).setMessage(getString(R.string.decline_meet_up)).setPositiveButton(getString(R.string.decline)) {_,_->
                    Database.declineMeetUp(currentMeetUpKey, currentMeetUpTeamKey)
                    YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
                    Global.wait(300) {
                        meetUpDetailLayout.visibility = View.GONE
                    }
                }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
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
            val meetUp = meetUpList[position]
            if (canClick) {
                canClick = false
                Database.getAccepted(meetUpList[position].key) {
                    if (it) {
                        declineMeetUpBtn.visibility = View.GONE
                        acceptMeetUpBtn.text = getString(R.string.accepted)
                    }
                }
                try {
                    durationTextView.text = Global.getDiffBetweenDate(Global.getDateFromString(meetUp.startDate, Global.basicFormat),Global.getDateFromString(meetUp.endDate, Global.basicFormat), requireContext())
                    descriptionTextView.text = meetUp.description
                    endDateTextView.text = meetUp.endDate
                    startDateTextView.text = meetUp.startDate
                    locationTextView.text = meetUp.location
                    currentMeetUpTeamKey = meetUp.teamKey
                    currentMeetUpKey = meetUp.key
                    meetUpDetailLayout.visibility = View.VISIBLE
                    YoYo.with(Techniques.SlideInRight).duration(300).playOn(meetUpDetailLayout)
                    Global.wait(300) {
                        meetUpListView.visibility = View.GONE
                    }
                }catch (e: Exception) {}
            }
        }
        setMeetUpList(root)

        return root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMeetUpList(root: View) {
        Database.getUserMeetUps(Global.username) { meetUpList ->
            if (meetUpList.isEmpty()) {
                noUploadsLayout.visibility = View.VISIBLE
            }else noUploadsLayout.visibility = View.GONE
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