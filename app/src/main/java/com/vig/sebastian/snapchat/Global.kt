package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.content.ContextCompat.getSystemService
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedProfileObject
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedUserProfileActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startActivity

object Global {
    var username = ""
    var password = ""
    var country = ""
    var description = ""
    var city = ""
    var age = 0

    val basicFormat = "dd-MM-yyyy HH:mm"
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime() : Date {
        return Date()
    }
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateFromString(string: String, format: String) : Date{
        return SimpleDateFormat(format).parse(string)!!
    }
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStringFromDate(date: Date, format: String) : String{
        return SimpleDateFormat(format).format(date)
    }
    fun checkIfDateIsNegative(oldDate: Date) : Boolean{
        val newDate = Date()
        val diff = newDate.time - oldDate.time
        return diff < 0
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDiffBetweenDate(oldDate: Date) : String{
        val newDate = Date()
        val diff = newDate.time - oldDate.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hour = minutes / 60
        val days = hour / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        if (years >= 1) {
            if (years < 2) {
                return "$years Jahr"
            }else return "$years Jahre"
        }
        else if (months >= 1) {
            if(months < 2) {
                return "$months Monat"
            }else return "$months Monate"
        }
        else if (weeks >= 1) {
            if(weeks < 2) {
                return "$weeks Woche"
            }else return "$weeks Wochen"
        }
        else if (days >= 1) {
            if(days < 2) {
                return "$days Tag"
            }else return "$days Tage"
        }
        else if (hour >= 1) {
            if(hour < 2) {
                return "$hour Stunde"
            }else return "$hour Stunden"
        }
        else if (minutes >= 1) {
            if(minutes < 2) {
                return "$minutes Minute"
            }else return "$minutes Minuten"
        }
        else {
            if (seconds < 10) {
                return "Jetzt"
            }else return "$seconds Sekunden"
        }
    }

    fun shareImage(imageUri: Uri?, text: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, imageUri!!)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "image/png"
        context.startActivity(Intent.createChooser(intent, "Share image"))
    }

    fun showProfile(username: String, context: Context?) {
        if (username != Global.username) {
            Database.getUserInfo(username) { user ->
                ClickedProfileObject.user = user
                context?.startActivity(Intent(context, ClickedUserProfileActivity::class.java))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getKey(): String {
        val key =  getStringFromDate(getCurrentTime(), "yyyy-MM-dd | HH:mm:sss")
        return  key + " | " + UUID.randomUUID().toString()
    }

    fun wait(mil: Long, unit: () -> Unit) {
        val cd = object : CountDownTimer(mil + 1, mil){
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                unit()
            }
        }
        cd.start()
    }

    fun checkIfStringsAreEmpty(vararg strings: String) : Boolean {
        var bool = false
        for (string in strings) {
            if (string.trim() == "") {
                bool = true
            }
        }
        return bool
    }
}