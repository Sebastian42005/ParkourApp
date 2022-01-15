package com.vig.sebastian.snapchat.support

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.chat.MessageAdapter
import com.vig.sebastian.snapchat.database.Database

class SupportChatActivity : AppCompatActivity() {
    lateinit var messageListView: ListView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_chat)
        val messageEditText: EditText = findViewById(R.id.messageEditText)
        val sendMessageBtn: ImageView = findViewById(R.id.sendMessageBtn)
        val usernameTextView : TextView = findViewById(R.id.usernameTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        messageListView = findViewById(R.id.chatListView)

        if (SupportObject.clickedUsername != "") {
            usernameTextView.text = SupportObject.clickedUsername
        }

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        sendMessageBtn.setOnClickListener {
            if (SupportObject.clickedUsername == "") {
                Database.sendMessageToSupport(messageEditText.text.toString())
                messageEditText.setText("")
            }else {
                Database.sendSupportMessageToUser(messageEditText.text.toString(), SupportObject.clickedUsername)
                messageEditText.setText("")
            }
        }

        if (SupportObject.clickedUsername == "") {
            Database.getMessageSupportList(Global.username) { messageList ->
                val adapter = MessageAdapter(this, R.layout.message_layout, messageList)
                messageListView.adapter = adapter
            }
        }else {
            Database.getMessageSupportList(SupportObject.clickedUsername) { messageList ->
                val adapter = MessageAdapter(this, R.layout.message_layout, messageList)
                messageListView.adapter = adapter
            }
        }
    }
}