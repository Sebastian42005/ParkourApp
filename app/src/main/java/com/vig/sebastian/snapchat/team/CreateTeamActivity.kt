package com.vig.sebastian.snapchat.team

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class CreateTeamActivity : AppCompatActivity() {
    var key = ""
    var teamName = ""
    val addUserToTeamList = ArrayList<AddUserToTeamClass>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_team)
        val teamNameEditText: EditText = findViewById(R.id.teamNameEditText)
        val teamPasswordEditText: EditText = findViewById(R.id.teamPasswordEditText)
        val createTeamBtn: Button = findViewById(R.id.createTeamBtn)
        val addUsersToTeamBtn : ImageView = findViewById(R.id.addUsersToTeamBtn)
        val addUsersListView: ListView = findViewById(R.id.addUserToTeamListView)
        val addUserToTeamLayout: RelativeLayout = findViewById(R.id.addUserToTeamLayout)
        val backBtn : ImageView = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        createTeamBtn.setOnClickListener {
            val teamName = teamNameEditText.text.toString()
            val teamPassword = teamPasswordEditText.text.toString()
            Database.createTeam(teamName, teamPassword) {success, key ->
                if (success) {
                    this.teamName = teamName
                    this.key = key
                    Database.getFriendsList(Global.username) {
                        if (it.size != 0) addUserToTeamLayout.visibility = View.VISIBLE else super.onBackPressed()
                    }
                }
            }
        }
        Database.getFriendsList(Global.username) {
            for (user in it) {
                addUserToTeamList.add(AddUserToTeamClass(user, false))
            }
            val adapter = AddUserToTeamAdapter(this, R.layout.add_user_to_team_layout, addUserToTeamList)
            addUsersListView.adapter = adapter
        }

        addUsersToTeamBtn.setOnClickListener {
            val list = ArrayList<String>()
            for (user in addUserToTeamList) {
                if (user.isChecked) list.add(user.username)
            }
            Toast.makeText(this, "Team successfully created!", Toast.LENGTH_SHORT).show()
            Database.addUsersToTeam(list, key, teamName)
            super.onBackPressed()
        }
    }
}