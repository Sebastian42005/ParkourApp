package com.vig.sebastian.snapchat.explore

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.test.database.Database
import com.vig.sebastian.snapchat.R

class SearchListAdapter(context: Context, private val int: Int, arrayList : ArrayList<ExploreSearchClass>) : ArrayAdapter<ExploreSearchClass>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val username = getItem(position)!!.username

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val profilePicImageView : ImageView = view.findViewById(R.id.profilePicImageView)

        usernameTextView.text = username
        Database.getUserProfilePic(username) {
            Glide.with(context).load(it).into(profilePicImageView)
        }

        return view
    }
}