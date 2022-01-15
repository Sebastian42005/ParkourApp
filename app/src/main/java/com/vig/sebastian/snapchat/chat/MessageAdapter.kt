package com.vig.sebastian.snapchat.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.classes.MessageClass
import com.vig.sebastian.snapchat.database.Database

class MessageAdapter(context: Context, private val int: Int, arrayList : ArrayList<MessageClass>) : ArrayAdapter<MessageClass>(context, int, arrayList){
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val username = getItem(position)!!.username
        val message = getItem(position)!!.message
        val time = getItem(position)!!.time


        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val messageUsernameTextView = view.findViewById<TextView>(R.id.messageUsernameTextView)
        val messageTextView: TextView = view.findViewById(R.id.messageTextView)
        val messageTimeTextView: TextView = view.findViewById(R.id.messageTimeTextView)

        if (username == Global.username) messageUsernameTextView.setTextColor(Color.rgb(0, 0, 115))

        messageUsernameTextView.text = username
        messageTextView.text = message
        messageTimeTextView.text = Global.getStringFromDate(Global.getDateFromString(time, Global.basicFormat), "HH:mm")

        return view
    }
}