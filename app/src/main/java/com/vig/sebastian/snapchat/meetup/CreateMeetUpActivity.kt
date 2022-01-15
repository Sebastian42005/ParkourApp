package com.vig.sebastian.snapchat.meetup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.ClickedPostObject
import com.vig.sebastian.snapchat.explore.ShowPostActivity
import com.vig.sebastian.snapchat.parkour_spots.LocationClass
import com.vig.sebastian.snapchat.parkour_spots.SpotDatabaseClass
import com.vig.sebastian.snapchat.profile.SpotType
import java.util.*


class CreateMeetUpActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var googleMapsLayout: RelativeLayout
    lateinit var googleMap: GoogleMap
    val markerList = ArrayList<Marker>()
    val spotsList = ArrayList<SpotDatabaseClass>()
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var mapLayout: RelativeLayout
    lateinit var searchLayout: RelativeLayout
    lateinit var searchSpotFinishedBtn: ImageView
    lateinit var wallJumpsSpotTypeCheckBox: CheckBox
    lateinit var stairsSpotTypeCheckBox: CheckBox
    lateinit var parkourParkSpotTypeCheckBox: CheckBox
    lateinit var otherSpotTypeCheckBox: CheckBox
    lateinit var goToUserBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    lateinit var useFilterBtn: Button
    lateinit var searchSpotEditText: EditText
    lateinit var locationEditText: EditText
    lateinit var myPosition : LatLng
    lateinit var searchSpotBackBtn: ImageView
    var currentLatitude = 0.0
    var currentLongitude = 0.0
    var currentDay = 0
    var currentMonth = 0
    var currentYear = 0
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meet_up)
        googleMapsLayout = findViewById(R.id.googleMapsLayout)
        locationEditText = findViewById(R.id.meetUpLocationEditText)
        val dateEditText: EditText = findViewById(R.id.meetUpDateEditText)
        wallJumpsSpotTypeCheckBox = findViewById(R.id.wallJumpsSpotTypeCheckBox)
        stairsSpotTypeCheckBox = findViewById(R.id.stairsSpotTypeCheckBox)
        parkourParkSpotTypeCheckBox = findViewById(R.id.parkourParkSpotTypeCheckBox)
        otherSpotTypeCheckBox = findViewById(R.id.otherSpotTypeCheckBox)
        goToUserBtn = findViewById(R.id.goToUserBtn)
        useFilterBtn = findViewById(R.id.useFilterBtn)
        val endDateEditText: EditText = findViewById(R.id.meetUpEndDateEditText)
        val descriptionEditText: EditText = findViewById(R.id.meetUpDescriptionEditText)
        val createMeetUpBtn: Button = findViewById(R.id.createMeetUpBtn)
        val datePickerImageView : ImageView = findViewById(R.id.meetUpDatePickerImageView)
        val endDatePickerImageView : ImageView = findViewById(R.id.meetUpEndDatePickerImageView)
        val backBtn : ImageView = findViewById(R.id.backBtn)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        val googleMapsBackBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton =
            findViewById(R.id.googleMapsBackBtn)
        val searchSpotBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton =
            findViewById(R.id.searchSpotsBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        locationEditText.setOnTouchListener { v, event ->
            googleMapsLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp).duration(400).playOn(googleMapsLayout)
            return@setOnTouchListener false
        }

        val key = intent.getStringExtra("key")!!

        datePickerImageView.setOnClickListener {
            hideKeyboard()
            dateEditText.clearFocus()
            endDateEditText.clearFocus()
            descriptionEditText.clearFocus()
            locationEditText.clearFocus()
            val c: Calendar = Calendar.getInstance()
            if (currentDay == 0) {
                currentYear = c.get(Calendar.YEAR)
                currentMonth = c.get(Calendar.MONTH)
                currentDay = c.get(Calendar.DAY_OF_MONTH)
            }

            val dpd = DatePickerDialog(this, {_, mYear, mMonth,mDay ->
                val tpd = TimePickerDialog(this, { view, hourOfDay, minute ->
                    dateEditText.setText("$mDay.${mMonth + 1}.$mYear ${Global.getFullTime(hourOfDay.toString())}:${Global.getFullTime(minute.toString())}")
                        currentDay = mDay
                        currentMonth = mMonth
                        currentYear = mYear
                    }, 12, 0, true)
                tpd.show()
            },currentYear, currentMonth, currentDay)
            dpd.show()
        }

        endDatePickerImageView.setOnClickListener {
            hideKeyboard()
            dateEditText.clearFocus()
            endDateEditText.clearFocus()
            descriptionEditText.clearFocus()
            locationEditText.clearFocus()
            val c: Calendar = Calendar.getInstance()
            if (currentDay == 0) {
                currentYear = c.get(Calendar.YEAR)
                currentMonth = c.get(Calendar.MONTH)
                currentDay = c.get(Calendar.DAY_OF_MONTH)
            }

            val dpd = DatePickerDialog(this, {_, mYear, mMonth,mDay ->
                val tpd = TimePickerDialog(this, { view, hourOfDay, minute ->
                    endDateEditText.setText("$mDay.${mMonth + 1}.$mYear ${Global.getFullTime(hourOfDay.toString())}:${Global.getFullTime(minute.toString())}")
                    currentDay = mDay
                    currentMonth = mMonth
                    currentYear = mYear
                }, 12, 0, true)
                tpd.show()
            },currentYear, currentMonth, currentDay)
            dpd.show()
        }

        createMeetUpBtn.setOnClickListener {
            try {
                Global.getDateFromString(dateEditText.text.toString().trim(), Global.basicFormat)
                try {
                    Global.getDateFromString(endDateEditText.text.toString().trim(), Global.basicFormat
                    )
                    if (locationEditText.length() != 0) {
                        if (!Global.checkIfDateIsExpired(Global.getDateFromString(dateEditText.text.toString().trim(), Global.basicFormat))
                        ) {
                            val startDate = dateEditText.text.toString().trim()
                            val endDate = endDateEditText.text.toString().trim()
                            val location = locationEditText.text.toString().trim()
                            val description = descriptionEditText.text.toString().trim()
                            if (!Global.checkIfDateIsBefore(Global.getDateFromString(endDate, Global.basicFormat), Global.getDateFromString(startDate, Global.basicFormat))) {
                                Database.createMeetUp(
                                    MeetUp(
                                        startDate,
                                        endDate,
                                        location,
                                        currentLatitude,
                                        currentLongitude,
                                        description,
                                        key, key,
                                    )
                                )
                                Toast.makeText(
                                    this,
                                    getString(R.string.meet_up_created),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                super.onBackPressed()
                            }else {
                                YoYo.with(Techniques.Shake).duration(300).playOn(endDateEditText)
                                endDateEditText.error = getString(R.string.error_date_older_than_start_date)
                                endDateEditText.requestFocus()
                            }
                        } else {
                            YoYo.with(Techniques.Shake).duration(300).playOn(dateEditText)
                            dateEditText.error = getString(R.string.error_date_expired)
                            dateEditText.requestFocus()
                        }
                    } else {
                        YoYo.with(Techniques.Shake).duration(300).playOn(locationEditText)
                        locationEditText.error = getString(R.string.error_empty)
                        locationEditText.requestFocus()
                    }
                }catch (e: Exception) {
                    YoYo.with(Techniques.Shake).duration(300).playOn(endDateEditText)
                    endDateEditText.error = "dd.MM.YYYY HH:mm"
                    endDateEditText.requestFocus()
                }
            }catch (e: Exception) {
                YoYo.with(Techniques.Shake).duration(300).playOn(dateEditText)
                dateEditText.error = "dd.MM.YYYY HH:mm"
                dateEditText.requestFocus()
            }
        }

        /*




         */

        mapLayout = findViewById(R.id.mapLayout)
        searchLayout = findViewById(R.id.searchSpotLayout)
        searchSpotFinishedBtn = findViewById(R.id.searchImage)
        searchSpotEditText = findViewById(R.id.searchEditText)
        searchSpotBackBtn = findViewById(R.id.searchSpotBackBtn)

        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        searchSpotEditText.setOnEditorActionListener(editorListener)

        searchSpotBackBtn.setOnClickListener {
            mapLayout.visibility = View.VISIBLE
            searchLayout.visibility = View.GONE
        }

        searchSpotBtn.setOnClickListener {
            showKeyboard()
            mapLayout.visibility = View.GONE
            searchLayout.visibility = View.VISIBLE
        }

        googleMapsBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutDown).duration(400).playOn(googleMapsLayout)
            Global.wait(400) {
                googleMapsLayout.visibility = View.GONE
            }
        }

    }
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun showKeyboard() {
        searchSpotEditText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchSpotEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        if (!bm.isRecycled) {
            bm.recycle()
        }
        return resizedBitmap
    }

    fun getCroppedBitmap(bitmap: Bitmap): Bitmap? {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(), (
                    bitmap.width / 2).toFloat(), paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output
    }
    fun addCornersToBitmap(bitmap: Bitmap) : Bitmap {
        val w: Int = bitmap.width
        val h: Int = bitmap.height

        val radius = Math.min(h / 2, w / 2)
        val output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888)

        val p = Paint()
        p.isAntiAlias = true

        val c = Canvas(output)
        c.drawARGB(0, 0, 0, 0)
        p.style = Paint.Style.FILL

        c.drawCircle((w / 2 + 4).toFloat(), (h / 2 + 4).toFloat(), radius.toFloat(), p)

        p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        c.drawBitmap(bitmap, 4f, 4f, p)
        p.xfermode = null
        p.style = Paint.Style.STROKE
        p.color = Color.BLACK
        p.strokeWidth = 5f
        c.drawCircle((w / 2 + 4).toFloat(), (h / 2 + 4).toFloat(), radius.toFloat(), p)

        return output
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private val editorListener = TextView.OnEditorActionListener { v, actionId, event ->
        when (actionId) {
            EditorInfo.IME_ACTION_SEARCH -> {
                for (markerPosition in markerList.indices) {
                    markerList[markerPosition].isVisible = spotsList[markerPosition].description.toLowerCase().trim().contains(searchSpotEditText.text.toString().toLowerCase().trim())
                }
                hideKeyboard()
                mapLayout.visibility = View.VISIBLE
                searchLayout.visibility = View.GONE
                if (spotsList.size != 0) {
                    val location = LatLng(spotsList[0].latitude, spotsList[0].longitude)
                    this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                }
            }
        }
        false
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.apply {
            Global.getLocation(this@CreateMeetUpActivity, this@CreateMeetUpActivity) { location ->
                loadUserIntoGoogleMaps(googleMap, location)
            }
            val keyHashMap = HashMap<String, String>()
            Database.getSpotsFromCountry(Global.country) { spotList ->
                var position = 1
                for (spot in spotList) {
                    val location = LatLng(spot.latitude, spot.longitude)
                    val marker = MarkerOptions().position(location).title(Global.getStringFromSpotType(this@CreateMeetUpActivity, spot.spotType)).snippet("Spot $position")
                    when (spot.spotType) {
                        SpotType.STAIRS -> marker.icon(bitmapDescriptorFromVector(this@CreateMeetUpActivity, R.drawable.stairs))
                        SpotType.WALL_JUMPS -> marker.icon(bitmapDescriptorFromVector(this@CreateMeetUpActivity, R.drawable.wall_jumps))
                        SpotType.PARKOUR_PARK -> marker.icon(bitmapDescriptorFromVector(this@CreateMeetUpActivity, R.drawable.parkour_spot))
                    }
                    markerList.add(googleMap.addMarker(marker)!!)
                    spotsList.add(spot)
                    keyHashMap["Spot $position"] = spot.key
                    position++
                }
                if (spotList.size != 0) {
                    val location = LatLng(spotList[0].latitude, spotList[0].longitude)
                    moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                }
            }

            searchSpotFinishedBtn.setOnClickListener {
                val checkBoxList = arrayListOf(wallJumpsSpotTypeCheckBox, stairsSpotTypeCheckBox, parkourParkSpotTypeCheckBox, otherSpotTypeCheckBox)
                val filterList = ArrayList<String>()
                for (filter in checkBoxList) {
                    if (filter.isChecked) filterList.add(filter.text.toString())
                }
                for (markerPosition in markerList.indices) {
                    markerList[markerPosition].isVisible = spotsList[markerPosition].description.toLowerCase().trim().contains(searchSpotEditText.text.toString().toLowerCase().trim()) && filterList.contains(markerList[markerPosition].title)
                }
                hideKeyboard()
                mapLayout.visibility = View.VISIBLE
                searchLayout.visibility = View.GONE
                if (spotsList.size != 0) {
                    val location = LatLng(spotsList[0].latitude, spotsList[0].longitude)
                    moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                }
            }

            useFilterBtn.setOnClickListener {
                val checkBoxList = arrayListOf(wallJumpsSpotTypeCheckBox, stairsSpotTypeCheckBox, parkourParkSpotTypeCheckBox, otherSpotTypeCheckBox)
                val filterList = ArrayList<String>()
                for (filter in checkBoxList) {
                    if (filter.isChecked) filterList.add(filter.text.toString())
                }
                for (markerPosition in markerList.indices) {
                    markerList[markerPosition].isVisible = spotsList[markerPosition].description.toLowerCase().trim().contains(searchSpotEditText.text.toString().toLowerCase().trim()) && filterList.contains(markerList[markerPosition].title)
                }
                hideKeyboard()
                mapLayout.visibility = View.VISIBLE
                searchLayout.visibility = View.GONE
                if (spotsList.size != 0) {
                    val location = LatLng(spotsList[0].latitude, spotsList[0].longitude)
                    moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                }
            }

            googleMap.setOnInfoWindowClickListener { marker ->
                if (marker.snippet != null) {
                    Database.getPostFromKey(keyHashMap[marker.snippet!!]!!) { post ->
                        ClickedPostObject.uploadPostClass = post
                        ClickedPostObject.imageUri = ImageUriListsObject.getPost(post.key)
                        startActivity(Intent(this@CreateMeetUpActivity, ShowPostActivity::class.java))
                    }
                }
            }
            goToUserBtn.setOnClickListener {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 18f))
            }

            this@CreateMeetUpActivity.googleMap = googleMap
        }
    }

    private fun loadUserIntoGoogleMaps(googleMap: GoogleMap, location: LocationClass) {
        val position = LatLng(location.latitude, location.longitude)
        myPosition = position
        val uri = ImageUriListsObject.getProfilePic(Global.username)
        val you = MarkerOptions().position(position).title(getString(R.string.you))
        if (uri != Uri.parse("not_found")) {
            Glide.with(this).load(uri.toString()).asBitmap()
                .listener(object : RequestListener<String, Bitmap> {
                    override fun onException(
                        e: Exception?,
                        model: String?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: String?,
                        target: Target<Bitmap>?,
                        isFromMemoryCache: Boolean,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (resource != null) {
                            you.icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    addCornersToBitmap(getCroppedBitmap(getResizedBitmap(resource, 150, 150)!!)!!)
                                )
                            )
                            googleMap.addMarker(you)
                        }
                        return true
                    }
                })
                .centerCrop()
                .preload()
        }else {
            you.icon(bitmapDescriptorFromVector(this, R.drawable.user))
            googleMap.addMarker(you)
        }
    }
}

/*

googleMap.setOnInfoWindowClickListener { marker ->
                YoYo.with(Techniques.SlideOutDown).duration(400).playOn(googleMapsLayout)
                Global.wait(400) {
                    googleMapsLayout.visibility = View.GONE
                }
                currentLatitude = marker.position.latitude
                currentLongitude = marker.position.longitude
                val geocoder = Geocoder(this@CreateMeetUpActivity, Locale.getDefault())
                val addresses = geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)
                val address = addresses[0].getAddressLine(0)
                locationEditText.setText(address)
            }

 */