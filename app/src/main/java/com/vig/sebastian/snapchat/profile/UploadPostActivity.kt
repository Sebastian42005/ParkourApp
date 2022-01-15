package com.vig.sebastian.snapchat.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.GoogleMap
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.classes.UploadPostClass
import java.util.*


class UploadPostActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var descriptionEditText: EditText
    lateinit var countryEditText: EditText
    lateinit var cityEditText: EditText
    lateinit var spotTypeSpinner: Spinner
    lateinit var map: GoogleMap
    var latitude: Double? = null
    var longitude: Double? = null
    lateinit var locationEditText: EditText
    lateinit var imageView: ImageView
    var spotType = SpotType.OTHER
    var uploadingPic = false
    val uri = PostObject.uri!!
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseSwitchCompatOrMaterialCode", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_post)
        spotTypeSpinner = findViewById(R.id.uploadPostTypeSpinner)
        imageView = findViewById(R.id.uploadPostImageView)
        val typeSwitch: Switch = findViewById(R.id.uploadPostTypeSwitch)
        descriptionEditText = findViewById(R.id.uploadPostDescriptionEditText)
        countryEditText = findViewById(R.id.uploadPostCountryEditText)
        cityEditText = findViewById(R.id.uploadPostCityEditText)
        locationEditText = findViewById(R.id.uploadPostLocationEditText)
        val uploadBtn: Button = findViewById(R.id.uploadPostBtn)

        val adapter = ArrayAdapter.createFromResource(this, R.array.spot_types, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spotTypeSpinner.adapter = adapter
        spotTypeSpinner.onItemSelectedListener


        imageView.setImageURI(uri)
        Global.getLocation(this, this) { location ->
            countryEditText.setText(location.country)
            cityEditText.setText(location.city)
            locationEditText.setText(location.address)
            latitude = location.latitude
            longitude = location.longitude
        }

        typeSwitch.setOnClickListener {
            if (!typeSwitch.isChecked) {
                countryEditText.visibility = View.GONE
                cityEditText.visibility = View.GONE
                locationEditText.visibility = View.GONE
                spotTypeSpinner.visibility = View.GONE
                uploadBtn.text = getString(R.string.post_picture)
            }else {
                countryEditText.visibility = View.VISIBLE
                cityEditText.visibility = View.VISIBLE
                locationEditText.visibility = View.VISIBLE
                spotTypeSpinner.visibility = View.VISIBLE
                uploadBtn.text = getString(R.string.post_spot)
            }
        }

        uploadBtn.setOnClickListener {
            if (!uploadingPic) {
                var type = PostType.PICTURE
                if (typeSwitch.isChecked) type = PostType.PARKOUR_SPOT
                if (locationEditText.text.toString().trim() != "") {
                    uploadPic(type)
                } else if (type == PostType.PICTURE) {
                    uploadPic(type)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadPic(type: PostType) {
        var country = countryEditText.text.toString().trim()
        var city = cityEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val location = locationEditText.text.toString().trim()
        val key = Global.getKey()

        if (country == "") country = Global.country
        if (city == "") city = Global.city
        uploadingPic = true
        ImageUriListsObject.setPostImageUriHashMap(key, uri)
        if (type == PostType.PARKOUR_SPOT) {
            Database.postImage(UploadPostClass(Global.username, type, key, country, city, location, description, spotType), uri, this) {
                Toast.makeText(this, getString(R.string.spot_uploaded), Toast.LENGTH_SHORT).show()
                if (PostObject.type == PostObjectType.NORMAL) {
                    if (latitude != null && longitude != null) Database.uploadSpot(latitude!!, longitude!!, country, key, description, spotType)
                    super.onBackPressed()
                }else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }else {
            Database.postImage(UploadPostClass(Global.username, type, key, "", "", "", description, spotType), uri, this) {
                Toast.makeText(this, getString(R.string.picture_uploaded), Toast.LENGTH_SHORT).show()
                if (PostObject.type == PostObjectType.NORMAL) {
                    super.onBackPressed()
                }else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.getItemAtPosition(position).toString()) {
            getString(R.string.parkour_park) -> spotType = SpotType.PARKOUR_PARK
            getString(R.string.wall_jumps) -> spotType = SpotType.WALL_JUMPS
            getString(R.string.stairs) -> spotType = SpotType.STAIRS
            getString(R.string.playground) -> spotType = SpotType.PLAYGROUND
            getString(R.string.calisthenics) -> spotType = SpotType.CALISTHENICS
            getString(R.string.big_area) -> spotType = SpotType.BIG_AREA
            getString(R.string.other) -> spotType = SpotType.OTHER
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}