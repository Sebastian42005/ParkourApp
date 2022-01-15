package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.AnyRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.FilterType
import com.vig.sebastian.snapchat.parkour_spots.LocationClass
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.SpotType
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedProfileObject
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedUserProfileActivity
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("StaticFieldLeak")
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

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    fun getLocation(context: Context, activity: Activity, unit: (location: LocationClass) -> Unit){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location != null) {
                    try {
                        val geocoder = Geocoder(context, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val latitude = addresses[0].latitude
                        val longitude = addresses[0].longitude
                        val country = addresses[0].countryName
                        val city = addresses[0].locality
                        val address = addresses[0].getAddressLine(0)
                        unit(LocationClass(latitude, longitude, country, city, address))
                    }catch (e : IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 44)
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
    fun getUriToDrawable(
        context: Context,
        @AnyRes drawableId: Int
    ): Uri {
        return Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context.resources.getResourcePackageName(drawableId)
                    + '/' + context.resources.getResourceTypeName(drawableId)
                    + '/' + context.resources.getResourceEntryName(drawableId)
        )
    }

    fun getFullTime(time: String) : String{
        return if (time.length == 1) {
            "0$time"
        }else time
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
        return key.replace("<", "[")
            .replace(">", "]")
            .replace(";", ".")
            .replace(":", "$")
    }
    fun getStringFromSpotType(context: Context, spotType: SpotType) : String{
        return when(spotType) {
            SpotType.STAIRS -> context.getString(R.string.stairs)
            SpotType.WALL_JUMPS -> context.getString(R.string.wall_jumps)
            SpotType.PARKOUR_PARK -> context.getString(R.string.parkour_park)
            SpotType.CALISTHENICS -> context.getString(R.string.calisthenics)
            SpotType.PLAYGROUND -> context.getString(R.string.playground)
            SpotType.BIG_AREA -> context.getString(R.string.big_area)
            SpotType.OTHER -> context.getString(R.string.other)
        }
    }
}