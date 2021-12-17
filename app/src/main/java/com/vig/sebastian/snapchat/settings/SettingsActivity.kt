package com.vig.sebastian.snapchat.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.login.LoginActivity
import com.vig.sebastian.snapchat.team.UsernameProfileAdapter

class SettingsActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var showFriendsListView: ListView
    lateinit var showFriendsLayout: RelativeLayout
    lateinit var adapter: UsernameProfileAdapter
    var showFriendsList = ArrayList<String>()
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsListView = findViewById<ListView>(R.id.settingsListView)
        val settingsList = arrayListOf("Join Team", "Friends", "Logout")
        val adapter = SettingsAdapter(this, R.layout.settings_layout, settingsList)
        settingsListView.adapter = adapter
        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val showFriendsBackBtn: ImageView = findViewById(R.id.showFriendsBackBtn)
        showFriendsLayout = findViewById(R.id.showFriendsLayout)
        showFriendsListView = findViewById(R.id.showFriendsListView)

        showFriendsBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutRight).duration(300).playOn(showFriendsLayout)
            Global.wait(300) {
                showFriendsLayout.visibility = View.GONE
            }
        }

        showFriendsListView.setOnItemClickListener { parent, view, position, id ->
            Global.showProfile(showFriendsList[position], this)
        }
        
        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        settingsListView.setOnItemClickListener { parent, view, position, id ->
            when (settingsList[position]) {
                "Logout" -> logout()
                "Friends" -> showFriends()
                "Join Team" -> joinTeam()
            }
        }

    }

    private fun showFriends() {
        Database.getFriendsList(Global.username) {
            showFriendsLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInRight).duration(300).playOn(showFriendsLayout)
            showFriendsList = it

            adapter = UsernameProfileAdapter(this, R.layout.username_profile_layout, showFriendsList)
            showFriendsListView.adapter = adapter
        }
    }

    private fun joinTeam() {
        val joinTeamLayout = findViewById<RelativeLayout>(R.id.joinTeamLayout)
        val teamKeyEditText = findViewById<EditText>(R.id.teamKeyEditText)
        val teamPasswordEditText = findViewById<EditText>(R.id.teamPasswordEditText)
        val joinTeamBtn = findViewById<Button>(R.id.joinTeamBtn)
        val pasteBtn = findViewById<ImageView>(R.id.pasteBtn)

        pasteBtn.setOnClickListener {
            val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = clipboardManager.primaryClip
            val item = clipData?.getItemAt(0)
            teamKeyEditText.setText(item?.text.toString())
        }

        joinTeamLayout.visibility = View.VISIBLE
        joinTeamLayout.setOnClickListener {
            hideKeyboard()
            teamKeyEditText.setText("")
            teamPasswordEditText.setText("")
            joinTeamLayout.visibility = View.GONE
        }

        joinTeamBtn.setOnClickListener {
            val teamKey = teamKeyEditText.text.toString().trim()
            val teamPassword = teamPasswordEditText.text.toString().trim()
            Database.joinTeam(teamKey, teamPassword, this) {isSuccess ->
                when (isSuccess) {
                    "" -> {
                        joinTeamLayout.visibility = View.GONE
                        teamKeyEditText.setText("")
                        teamPasswordEditText.setText("")
                    }
                    "password" -> teamPasswordEditText.error = "Wrong password!"
                    "key" -> teamKeyEditText.error = "Wrong key!"
                    "team" -> {
                        Toast.makeText(this, "You are already in this team!", Toast.LENGTH_SHORT).show()
                        teamKeyEditText.setText("")
                        teamPasswordEditText.setText("")
                    }
                }
            }
        }
    }

    private fun logout() {
        AlertDialog.Builder(this).setTitle("Logout?").setPositiveButton("Logout") { _, _->
            editor.clear()
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
        }.setNegativeButton("Cancel") {_,_->}.show()
    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}