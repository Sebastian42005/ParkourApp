package com.vig.sebastian.snapchat.meetup

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.gms.common.internal.GmsLogger
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R

class MeetUpAdapter(context: Context, private val int: Int, arrayList : ArrayList<MeetUp>) : ArrayAdapter<MeetUp>(context, int, arrayList){
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val date = getItem(position)!!.startDate
        val location = getItem(position)!!.location
        val endDate = getItem(position)!!.endDate
        val description = getItem(position)!!.description
        val key = getItem(position)!!.key
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val dateTextView = view.findViewById<TextView>(R.id.meetUpDateTextView)
        val locationTextView = view.findViewById<TextView>(R.id.meetUpLocationTextView)
        val durationTextView = view.findViewById<TextView>(R.id.meetUpDurationTextView)
        dateTextView.text = context.getString(R.string.show_start_date) + " $date"
        locationTextView.text = location
        durationTextView.text = context.getString(R.string.show_duration) + " ${Global.getDiffBetweenDate(Global.getDateFromString(date, Global.basicFormat),Global.getDateFromString(endDate, Global.basicFormat), context)}"

        return view
    }
}