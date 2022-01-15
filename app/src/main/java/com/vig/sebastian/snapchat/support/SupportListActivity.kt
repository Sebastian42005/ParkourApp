package com.vig.sebastian.snapchat.support

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.team.UsernameProfileAdapter

class SupportListActivity : AppCompatActivity() {
    var userList = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_list)
        val supportListView : ListView = findViewById(R.id.supportListView)

        supportListView.setOnItemClickListener { parent, view, position, id ->
            SupportObject.clickedUsername = userList[position]
            startActivity(Intent(this, SupportChatActivity::class.java))
        }

        Database.getNewSupportMessages { userList ->
            this.userList = userList
            val adapter = UsernameProfileAdapter(this, R.layout.username_profile_layout, userList)
            supportListView.adapter = adapter
        }
    }
}