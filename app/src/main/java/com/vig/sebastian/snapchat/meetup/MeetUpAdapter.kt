package com.vig.sebastian.snapchat.meetup

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.vig.sebastian.snapchat.R

class MeetUpAdapter(context: Context, private val int: Int, arrayList : ArrayList<MeetUp>) : ArrayAdapter<MeetUp>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val date = getItem(position)!!.startDate
        val location = getItem(position)!!.location
        val duration = getItem(position)!!.duration
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val dateTextView = view.findViewById<TextView>(R.id.meetUpDateTextView)
        val locationTextView = view.findViewById<TextView>(R.id.meetUpLocationTextView)
        val durationTextView = view.findViewById<TextView>(R.id.meetUpDurationTextView)
        dateTextView.text = "Date: $date"
        locationTextView.text = location
        durationTextView.text = "Duration: $duration"

        return view
    }
}