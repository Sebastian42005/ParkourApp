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
        val settingsList = arrayListOf(getString(R.string.join_team), getString(R.string.friends), getString(R.string.support), getString(R.string.logout), getString(R.string.delete_account))
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
                getString(R.string.logout) -> logout()
                getString(R.string.delete_account) -> deleteAccount()
                getString(R.string.friends) -> showFriends()
                getString(R.string.support) -> sendMessageToSupport()
                getString(R.string.join_team) -> joinTeam()
            }
        }

    }

    private fun deleteAccount() {
        AlertDialog.Builder(this).setTitle(getString(R.string.delete_account) + "?").setPositiveButton(getString(R.string.delete)) { _, _->
            Database.deleteAccount {
                editor.clear()
                editor.apply()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
    }

    private fun sendMessageToSupport() {

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
            Database.joinTeam(teamKey, teamPassword, this) {error ->
                when (error) {
                    "" -> {
                        joinTeamLayout.visibility = View.GONE
                        teamKeyEditText.setText("")
                        teamPasswordEditText.setText("")
                    }
                    "password" -> {
                        YoYo.with(Techniques.Shake).duration(300).playOn(teamPasswordEditText)
                        teamPasswordEditText.error = getString(R.string.wrong_password)
                        teamPasswordEditText.requestFocus()
                    }
                    "key" -> {
                        YoYo.with(Techniques.Shake).duration(300).playOn(teamKeyEditText)
                        teamKeyEditText.error = getString(R.string.wrong_key)
                        teamKeyEditText.requestFocus()
                    }
                    "team" -> {
                        Toast.makeText(this, getString(R.string.already_in_team), Toast.LENGTH_SHORT).show()
                        teamKeyEditText.setText("")
                        teamPasswordEditText.setText("")
                    }
                }
            }
        }
    }

    private fun logout() {
        AlertDialog.Builder(this).setTitle(getString(R.string.logout) + "?").setPositiveButton(getString(R.string.logout)) { _, _->
            editor.clear()
            editor.apply()
            startActivity(Intent(this, LoginActivity::class.java))
        }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}