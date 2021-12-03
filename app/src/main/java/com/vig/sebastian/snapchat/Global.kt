package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


object Global {
    var username = ""
    var password = ""
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
        return SimpleDateFormat(format).parse(string)
    }
    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getStringFromDate(date: Date, format: String) : String{
        return SimpleDateFormat(format).format(date)
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

    fun getAverage(int1: Int, int2: Int): String {
        val fullAmount = int1 + int2
        if (fullAmount != 0) {
            return ((int1 * 100) / fullAmount).toString()
        }else return "100"
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