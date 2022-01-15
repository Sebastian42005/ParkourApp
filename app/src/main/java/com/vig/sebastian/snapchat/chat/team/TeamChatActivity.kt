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
import androidx.core.view.isVisible
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
import com.vig.sebastian.snapchat.team.AddUserToTeamAdapter
import com.vig.sebastian.snapchat.team.AddUserToTeamClass
import com.vig.sebastian.snapchat.team.UsernameProfileAdapter
import java.lang.Exception
import android.app.PendingIntent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.net.Uri
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.shortcuts.Shortcuts

class TeamChatActivity : AppCompatActivity() {
    var teamMembersList = ArrayList<String>()
    var editTeamMemberList = ArrayList<AddUserToTeamClass>()
    lateinit var chatListView : ListView
    val chatList = ArrayList<MessageClass>()
    var meetUpList = ArrayList<MeetUp>()
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_chat)
        //Team

        if (Build.VERSION.SDK_INT >= 25) {
            Shortcuts.createTeamShortcut(this, ClickedTeamChatObject.teamKey, ClickedTeamChatObject.teamName, ClickedTeamChatObject.admin, ClickedTeamChatObject.password)
        }

        val usernameTextView: TextView = findViewById(R.id.profileUsernameTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        chatListView = findViewById(R.id.chatListView)
        val sendMessageBtn: ImageView = findViewById(R.id.sendMessageBtn)
        val messageEditText: EditText = findViewById(R.id.messageEditText)

        usernameTextView.text = ClickedTeamChatObject.teamName

        backBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        chatListView.setOnItemLongClickListener { parent, view, position, id ->
            if (chatList[position].username == Global.username) {
                AlertDialog.Builder(this).setMessage(getString(R.string.delete_message)).setPositiveButton(getString(R.string.delete)) { _, _->
                    Database.deleteTeamMessage(ClickedTeamChatObject.teamKey, chatList[position].key)
                }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
            }
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

        //Meet Up

        val meetUpListView = findViewById<ListView>(R.id.meetUpListView)
        val meetUpDetailLayout: RelativeLayout = findViewById(R.id.meetUpDetailLayout)
        val descriptionTextView : TextView = findViewById(R.id.descriptionTextView)
        val durationTextView : TextView = findViewById(R.id.durationTextView)
        val locationTextView : TextView = findViewById(R.id.locationTextView)
        val startDateTextView : TextView = findViewById(R.id.startDateTextView)
        val endDateTextView : TextView = findViewById(R.id.endDateTextView)
        val meetUpBackBtn : ImageView = findViewById(R.id.meetUpBackBtn)
        val showTeamChatBtn: ImageView = findViewById(R.id.showTeamChatBtn)
        val navigateToMeetUpBtn: Button = findViewById(R.id.navigateToMeetUpBtn)
        val teamMeetUpsLayout: RelativeLayout = findViewById(R.id.teamMeetUpsLayout)
        val showStreetViewBtn: Button = findViewById(R.id.showMeetUpStreetView)
        val createMeetUpBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton = findViewById(R.id.createMeetUpBtn)
        val declinedUsersTextView: TextView = findViewById(R.id.declinedUsersTextView)
        val acceptedUsersTextView: TextView = findViewById(R.id.acceptedUsersTextView)


        showTeamChatBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(teamMeetUpsLayout)
            Global.wait(300) {
                teamMeetUpsLayout.visibility = View.GONE
            }
        }

        meetUpListView.setOnItemClickListener { parent, view, position, id ->
            val meetUp = meetUpList[position]
            navigateToMeetUpBtn.setOnClickListener {
                val gmmIntentUri = Uri.parse("google.navigation:q=${meetUp.latitude},${meetUp.longitude}&mode=b")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            showStreetViewBtn.setOnClickListener {
                val gmmIntentUri = Uri.parse("google.streetview:cbll=${meetUp.latitude},${meetUp.longitude}&cbp=0,30,0,0,-15")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
            Database.getMeetUpAcceptedUsers(meetUp.key, meetUp.teamKey) {
                if (it.isNotEmpty()) {
                    acceptedUsersTextView.setTypeface(null, Typeface.BOLD)
                    acceptedUsersTextView.text = it.toString().replace("]", "").replace("[", "")
                }else acceptedUsersTextView.text = getString(R.string.nobody_accepted)
            }
            Database.getMeetUpDeclinedUsers(meetUp.key, meetUp.teamKey) {
                if (it.isNotEmpty()) {
                    declinedUsersTextView.setTypeface(null, Typeface.BOLD)
                    declinedUsersTextView.text = it.toString().replace("]", "").replace("[", "")
                }else declinedUsersTextView.text = getString(R.string.nobody_declined)
            }
            descriptionTextView.text = meetUp.description
            endDateTextView.text = meetUp.endDate
            durationTextView.text = Global.getDiffBetweenDate(Global.getDateFromString(meetUp.startDate, Global.basicFormat),Global.getDateFromString(meetUp.endDate, Global.basicFormat), this)
            startDateTextView.text = meetUp.startDate
            locationTextView.text = meetUp.location
            meetUpDetailLayout.visibility = View.VISIBLE
            createMeetUpBtn.visibility = View.GONE
            YoYo.with(Techniques.SlideInRight).duration(300).playOn(meetUpDetailLayout)
            Global.wait(300) {
                meetUpListView.visibility = View.GONE
            }
        }

        meetUpListView.setOnItemLongClickListener { parent, view, position, id ->
            if (ClickedTeamChatObject.admin == Global.username) {
                AlertDialog.Builder(this).setMessage(getString(R.string.delete_meet_up)).setPositiveButton(getString(R.string.delete)) {_,_->
                    Database.deleteTeamMeetUp(meetUpList[position].teamKey, meetUpList[position].key)
                }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
            }
            return@setOnItemLongClickListener false
        }

        setMeetUpList()

        createMeetUpBtn.setOnClickListener {
            val intent = Intent(this, CreateMeetUpActivity::class.java)
            intent.putExtra("key", ClickedTeamChatObject.teamKey)
            startActivity(intent)
        }

        meetUpBackBtn.setOnClickListener {
            meetUpListView.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideOutRight).duration(300).playOn(meetUpDetailLayout)
            Global.wait(300) {
                if (ClickedTeamChatObject.admin == Global.username) createMeetUpBtn.visibility = View.VISIBLE
                meetUpDetailLayout.visibility = View.GONE
            }
        }

        //Team Members

        val teamMembersLayout : RelativeLayout = findViewById(R.id.teamMembersLayout)
        val teamMembersListView: ListView = findViewById(R.id.teamMembersListView)
        val editTeamMembersListView: ListView = findViewById(R.id.editTeamMembersListView)
        val teamMembersBackBtn : ImageView = findViewById(R.id.dontShowTeamMembersChatBtn)
        val editTeamMemberListBtn: ImageView = findViewById(R.id.editTeamMembersBtn)
        val saveTeamMemberChangesBtn: Button = findViewById(R.id.saveTeamMemberChangesBtn)

        teamMembersListView.setOnItemClickListener { parent, view, position, id ->
            Global.showProfile(teamMembersList[position], this)
        }

        if (ClickedTeamChatObject.admin != Global.username.trim()) {
            editTeamMemberListBtn.visibility = View.GONE
            createMeetUpBtn.visibility = View.GONE
        }

        editTeamMemberListBtn.setOnClickListener {
            if (!editTeamMembersListView.isVisible) {
                setTeamMembersEditList()
                editTeamMembersListView.visibility = View.VISIBLE
                saveTeamMemberChangesBtn.visibility = View.VISIBLE
                teamMembersListView.visibility = View.GONE
                editTeamMemberListBtn.foreground = getDrawable(R.drawable.ic_baseline_edit_off_24)
            }else {
                saveTeamMemberChangesBtn.visibility = View.GONE
                editTeamMembersListView.visibility = View.GONE
                teamMembersListView.visibility = View.VISIBLE
                editTeamMemberListBtn.foreground = getDrawable(R.drawable.edit)
                setTeamMembersList()
            }
        }

        saveTeamMemberChangesBtn.setOnClickListener {
            saveTeamMemberChangesBtn.visibility = View.GONE
            editTeamMembersListView.visibility = View.GONE
            teamMembersListView.visibility = View.VISIBLE
            editTeamMemberListBtn.foreground = getDrawable(R.drawable.edit)
            setTeamMembersList()
            for (user in editTeamMemberList) {
                if (teamMembersList.contains(user.username)) {
                    if (!user.isChecked) {
                        Database.removeUserFromTeam(ClickedTeamChatObject.teamKey, user.username)
                    }
                }else {
                    val addUsersToTeamList = ArrayList<String>()
                    if (user.isChecked) {
                        addUsersToTeamList.add(user.username)
                    }
                    Database.addUsersToTeam(addUsersToTeamList, ClickedTeamChatObject.teamKey, ClickedTeamChatObject.teamName)
                }
            }
        }

        teamMembersBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(teamMembersLayout)
            Global.wait(300) {
                teamMembersLayout.visibility = View.GONE
            }
        }

        //Options
        val optionsBtn : ImageView = findViewById(R.id.teamOptionsImageView)

        optionsBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, optionsBtn)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.shareTeamItem -> {
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_TEXT, ClickedTeamChatObject.teamKey)
                        intent.type = "text/plain"
                        val intent2 = Intent.createChooser(intent, null)
                        startActivity(intent2)
                        return@OnMenuItemClickListener false
                    }
                    R.id.showTeamMembersItem -> {
                        setTeamMembersList()
                        teamMembersLayout.visibility = View.VISIBLE
                        YoYo.with(Techniques.SlideInUp).duration(300).playOn(teamMembersLayout)
                        return@OnMenuItemClickListener false
                    }
                    R.id.showTeamMeetUpsItem -> {
                        teamMeetUpsLayout.visibility = View.VISIBLE
                        meetUpListView.visibility = View.VISIBLE
                        YoYo.with(Techniques.SlideInUp).duration(300).playOn(teamMeetUpsLayout)
                        return@OnMenuItemClickListener false
                    }
                    R.id.getPasswordItem -> {
                        Global.setClipboard(this, ClickedTeamChatObject.password)
                        Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
                        return@OnMenuItemClickListener false
                    }
                    R.id.leaveTeamItem -> {
                        AlertDialog.Builder(this).setMessage(getString(R.string.leave_team)).setPositiveButton(getString(R.string.leave)) {_,_ ->
                            Database.leaveTeam(ClickedTeamChatObject.teamKey)
                            super.onBackPressed()
                        }.setNegativeButton(getString(R.string.cancel)){_,_->}.show()
                        return@OnMenuItemClickListener false
                    }
                    R.id.createTeamShortcutItem -> {
                        if (Build.VERSION.SDK_INT >= 28) {
                            createTeamShortcut()
                        }
                        return@OnMenuItemClickListener false
                    }
                    R.id.deleteTeamItem -> {
                        AlertDialog.Builder(this).setMessage(getString(R.string.delete_team)).setPositiveButton(getString(R.string.delete)) {_,_ ->
                            Database.deleteTeam(ClickedTeamChatObject.teamKey)
                            super.onBackPressed()
                        }.setNegativeButton(getString(R.string.cancel)){_,_->}.show()
                        return@OnMenuItemClickListener false
                    }
                    else -> return@OnMenuItemClickListener false
                }
            })
            if (ClickedTeamChatObject.admin == Global.username) popupMenu.inflate(R.menu.team_options_admin_menu)
            else popupMenu.inflate(R.menu.team_options_menu)
            popupMenu.show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTeamShortcut() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)

        if (shortcutManager!!.isRequestPinShortcutSupported) {
            val pinShortcutInfo = ShortcutInfo.Builder(this, ClickedTeamChatObject.teamKey).build()
            val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)

            val successCallback = PendingIntent.getBroadcast(this, 0, pinnedShortcutCallbackIntent, 0)

            shortcutManager.requestPinShortcut(pinShortcutInfo, successCallback.intentSender)
        }
    }

    private fun setTeamMembersEditList() {
        val editTeamMembersListView: ListView = findViewById(R.id.editTeamMembersListView)
        val teamMembersList = ArrayList<AddUserToTeamClass>()
        for (user in this.teamMembersList) {
            if (user != Global.username) teamMembersList.add(AddUserToTeamClass(user, true))
        }
        Database.getFriendsList(Global.username) {
            for (user in it) {
                if (!this.teamMembersList.contains(user)) teamMembersList.add(AddUserToTeamClass(user, false))
            }
            this.editTeamMemberList = teamMembersList
            val adapter = AddUserToTeamAdapter(this, R.layout.add_user_to_team_layout, teamMembersList)
            editTeamMembersListView.adapter = adapter
        }
    }

    private fun setTeamMembersList() {

        val teamMemberListView : ListView = findViewById(R.id.teamMembersListView)
        Database.getTeamMembers(ClickedTeamChatObject.teamKey) {memberList ->
            val teamMemberList = ArrayList<String>()
            for (user in memberList) {
                teamMemberList.add(user)
            }
            this.teamMembersList = teamMemberList
            val adapter = UsernameProfileAdapter(this, R.layout.username_profile_layout, teamMemberList)
            teamMemberListView.adapter = adapter
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMeetUpList() {
        Database.getTeamMeetUps(ClickedTeamChatObject.teamKey) { meetUpList ->
            meetUpList.sort()
            this.meetUpList = meetUpList
            val meetUpListView = findViewById<ListView>(R.id.meetUpListView)
            try {
                val adapter = MeetUpAdapter(this, R.layout.meet_up_layout, meetUpList)
                meetUpListView.adapter = adapter
            }catch (e: Exception) {}
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}