package com.vig.sebastian.snapchat.team

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class TeamAdapter(context: Context, private val int: Int, arrayList : ArrayList<DisplayedTeam>) : ArrayAdapter<DisplayedTeam>(context, int, arrayList){
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ViewHolder", "SetTextI18n", "UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val teamName = getItem(position)!!.teamName
        val key = getItem(position)!!.key
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)

        val teamNameTextView = view.findViewById<TextView>(R.id.usernameTextView)
        val teamPicImageView = view.findViewById<ImageView>(R.id.profilePicImageView)

        teamNameTextView.text = teamName
        Database.getTeamPic(key) {uri ->
            if (uri != Uri.parse("not_found")) {
                Glide.with(context).load(uri).into(teamPicImageView)
            }else teamPicImageView.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_groups_24))
        }

        return view
    }
}