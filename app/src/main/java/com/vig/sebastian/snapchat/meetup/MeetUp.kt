package com.vig.sebastian.snapchat.meetup

import android.os.Build
import androidx.annotation.RequiresApi

class MeetUp(val startDate: String, val duration: String, val location: String, val description: String, val key: String) : Comparable<MeetUp>{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun compareTo(other: MeetUp): Int {
        return startDate.compareTo(other.startDate)
    }
}