package com.vig.sebastian.snapchat.chat.team

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.chat.MessageAdapter
import com.vig.sebastian.snapchat.classes.MessageClass
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.meetup.CreateMeetUpActivity
import com.vig.sebastian.snapchat.meetup.MeetUp
import com.vig.sebastian.snapchat.meetup.MeetUpAdapter
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedProfileObject
import java.lang.Exception

class TeamChatActivity : AppCompatActivity() {
    lateinit var chatListView : ListView
    val chatList = ArrayList<MessageClass>()
    var meetUpList = ArrayList<MeetUp>()
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_chat)

        val meetUpListView = findViewById<ListView>(R.id.meetUpListView)
        val meetUpDetailLayout: RelativeLayout = findViewById(R.id.meetUpDetailLayout)
        val descriptionTextView : TextView = findViewById(R.id.descriptionTextView)
        val durationTextView : TextView = findViewById(R.id.durationTextView)
        val locationTextView : TextView = findViewById(R.id.locationTextView)
        val startDateTextView : TextView = findViewById(R.id.startDateTextView)
        val meetUpBackBtn : ImageView = findViewById(R.id.meetUpBackBtn)
        val showTeamChatBtn: ImageView = findViewById(R.id.showTeamChatBtn)
        val teamMeetUpsLayout: RelativeLayout = findViewById(R.id.teamMeetUpsLayout)
        val showMeetUpsBtn: ImageView = findViewById(R.id.showMeetUpListBtn)
        val createMeetUpBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton = findViewById(R.id.createMeetUpBtn)
        val declinedUsersTextView: TextView = findViewById(R.id.declinedUsersTextView)
        val acceptedUsersTextView: TextView = findViewById(R.id.acceptedUsersTextView)

        val usernameTextView: TextView = findViewById(R.id.profileUsernameTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        chatListView = findViewById(R.id.chatListView)
        val sendMessageBtn: ImageView = findViewById(R.id.sendMessageBtn)
        val messageEditText: EditText = findViewById(R.id.messageEditText)

        usernameTextView.text = ClickedTeamChatObject.teamName

        showTeamChatBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(teamMeetUpsLayout)
            Global.wait(300) {
                teamMeetUpsLayout.visibility = View.GONE
            }
        }

        showMeetUpsBtn.setOnClickListener {
            teamMeetUpsLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp).duration(300).playOn(teamMeetUpsLayout)
        }

        meetUpBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
            Global.wait(300) {
                meetUpDetailLayout.visibility = View.GONE
            }
        }

        meetUpListView.setOnItemClickListener { parent, view, position, id ->
            Database.getMeetUpAcceptedUsers(meetUpList[position].key, meetUpList[position].teamKey) {
                if (it.isNotEmpty()) {
                    acceptedUsersTextView.setTypeface(null, Typeface.BOLD)
                    acceptedUsersTextView.text = it.toString().replace("]", "").replace("[", "")
                }else acceptedUsersTextView.text = "Nobody has accepted yet"
            }
            Database.getMeetUpDeclinedUsers(meetUpList[position].key, meetUpList[position].teamKey) {
                if (it.isNotEmpty()) {
                    declinedUsersTextView.setTypeface(null, Typeface.BOLD)
                    declinedUsersTextView.text = it.toString().replace("]", "").replace("[", "")
                }else declinedUsersTextView.text = "Nobody has declined yet"
            }
            descriptionTextView.text = meetUpList[position].description
            durationTextView.text = meetUpList[position].duration
            startDateTextView.text = meetUpList[position].startDate
            locationTextView.text = meetUpList[position].location
            meetUpDetailLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInRight).duration(300).playOn(meetUpDetailLayout)
        }
        setMeetUpList()

        createMeetUpBtn.setOnClickListener {
            val intent = Intent(this, CreateMeetUpActivity::class.java)
            intent.putExtra("key", ClickedTeamChatObject.teamKey)
            startActivity(intent)
        }

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        chatListView.setOnItemLongClickListener { parent, view, position, id ->
            AlertDialog.Builder(this).setMessage("Delete message?").setPositiveButton("Delete") {_,_->
                Database.deleteTeamMessage(ClickedTeamChatObject.teamKey, chatList[position].key)
            }.setNegativeButton("Cancel") {_,_->}.show()
            return@setOnItemLongClickListener false
        }

        sendMessageBtn.setOnClickListener {
            Database.sendMessageToTeam(ClickedTeamChatObject.teamKey, messageEditText.text.toString())
            messageEditText.setText("")
        }

        Database.getMessagesFromTeam(ClickedTeamChatObject.teamKey) { messagesList ->
            for (message in messagesList) {
                chatList.add(message)
            }
            val adapter = MessageAdapter(this, R.layout.message_layout, messagesList)
            chatListView.adapter = adapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMeetUpList() {
        Database.getTeamMeetUps(ClickedTeamChatObject.teamKey) { meetUpList ->
            meetUpList.sortWith(compareBy{it.startDate})
            meetUpList.sort()
            this.meetUpList = meetUpList
            val meetUpListView = findViewById<ListView>(R.id.meetUpListView)
            try {
                val adapter = MeetUpAdapter(this, R.layout.meet_up_layout, meetUpList)
                meetUpListView.adapter = adapter
            }catch (e: Exception) {}
        }
    }
}