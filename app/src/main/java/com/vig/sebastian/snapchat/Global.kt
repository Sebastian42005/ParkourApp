package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.text.ClipboardManager
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
import com.vig.sebastian.snapchat.explore.FilterType
import java.util.function.BinaryOperator

object Global {
    var username = ""
    var password = ""
    var email = ""
    var country = ""
    var description = ""
    var city = ""
    var age = 0

    val basicFormat = "dd.MM.yyyy HH:mm"
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
    fun checkIfDateIsExpired(oldDate: Date) : Boolean{
        val newDate = Date()
        return oldDate.before(newDate)
    }
    fun checkIfDateIsBefore(oldDate: Date, newDate: Date) : Boolean{
        return oldDate.before(newDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDiffBetweenDate(oldDate: Date, newDate: Date, context: Context) : String{
        val diff = newDate.time - oldDate.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hour = minutes / 60
        val days = hour / 24
        var diffString = ""
        if (hour >= 1) {
            if (hour >= 2) diffString = "$hour " + context.getString(R.string.hours) else diffString = "$hour " + context.getString(R.string.hour)
            val hourMinutes = minutes - (hour * 60)
            if (hourMinutes >= 1) {
                diffString += " " + context.getString(R.string.and) +" "
                if (hourMinutes >= 2) diffString += "$hourMinutes " + context.getString(R.string.minutes) else diffString += "$hourMinutes " + context.getString(R.string.minute)
            }
        }else {
            if (minutes >= 2) diffString += "$minutes " + context.getString(R.string.minutes) else diffString += "$minutes " + context.getString(R.string.minute)
        }
        return diffString
    }

    fun shareImage(imageUri: Uri?, text: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra(Intent.EXTRA_STREAM, imageUri!!)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = "image/png"
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_image)))
    }

    fun setClipboard(context: Context, text: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
    }
    fun getFilterType(filterType: String) : FilterType {
        return when(filterType) {
            "BENUTZERNAME" -> FilterType.USERNAME
            "LAND" -> FilterType.COUNTRY
            "STADT" -> FilterType.CITY
            "ALTER" -> FilterType.AGE
            else -> FilterType.valueOf(filterType)
        }
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
    fun getKeyFromEmail(email: String) : String {
        return email.replace("[", "<")
            .replace("]", ">")
            .replace(".", ";")
            .replace("$", ":")
    }
    fun getEmailFromKey(key: String) : String{
        return email.replace("<", "[")
            .replace(">", "]")
            .replace(";", ".")
            .replace(":", "$")
    }
}