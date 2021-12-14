package com.vig.sebastian.snapchat.team

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class AddUserToTeamAdapter(context: Context, private val int: Int, arrayList : ArrayList<AddUserToTeamClass>) : ArrayAdapter<AddUserToTeamClass>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val username = getItem(position)!!.username
        val isChecked = getItem(position)!!.isChecked
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)

        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox)
        val profilePic = view.findViewById<ImageView>(R.id.profilePicImageView)

        if (ImageUriListsObject.getProfilePic(username) != null) {
            Glide.with(context).load(ImageUriListsObject.getProfilePic(username)).into(profilePic)
        }

        checkBox.isChecked = isChecked

        usernameTextView.text = username

        usernameTextView.setOnClickListener {
            checkBox.isChecked = !checkBox.isChecked
            getItem(position)!!.isChecked = checkBox.isChecked
        }

        checkBox.setOnClickListener {
            getItem(position)!!.isChecked = checkBox.isChecked
        }

        return view
    }
}