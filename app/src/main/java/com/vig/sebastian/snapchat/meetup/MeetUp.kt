package com.vig.sebastian.snapchat.meetup

import android.os.Build
import androidx.annotation.RequiresApi

class MeetUp(val startDate: String, val endDate: String, val location: String, val latitude: Double, val longitude: Double, val description: String, val key: String, val teamKey: String) : Comparable<MeetUp>{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun compareTo(other: MeetUp): Int {
        return startDate.compareTo(other.startDate)
    }
}