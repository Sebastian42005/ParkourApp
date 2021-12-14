package com.vig.sebastian.snapchat.profile

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.classes.UploadPostClass
import java.util.*

class UploadPostActivity : AppCompatActivity() {
    lateinit var descriptionEditText: EditText
    lateinit var countryEditText: EditText
    lateinit var cityEditText: EditText
    lateinit var locationEditText: EditText
    var uploadingPic = false
    val uri = PostObject.uri!!
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_post)

        val imageView: ImageView = findViewById(R.id.uploadPostImageView)
        val typeSwitch: Switch = findViewById(R.id.uploadPostTypeSwitch)
        descriptionEditText = findViewById(R.id.uploadPostDescriptionEditText)
        countryEditText = findViewById(R.id.uploadPostCountryEditText)
        cityEditText = findViewById(R.id.uploadPostCityEditText)
        locationEditText = findViewById(R.id.uploadPostLocationEditText)
        val uploadBtn: Button = findViewById(R.id.uploadPostBtn)

        imageView.setImageURI(uri)

        typeSwitch.setOnClickListener {
            if (!typeSwitch.isChecked) {
                countryEditText.visibility = View.GONE
                cityEditText.visibility = View.GONE
                locationEditText.visibility = View.GONE
                uploadBtn.text = "Upload Picture"
            }else {
                countryEditText.visibility = View.VISIBLE
                cityEditText.visibility = View.VISIBLE
                locationEditText.visibility = View.VISIBLE
                uploadBtn.text = "Upload Spot"
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
            Database.postImage(UploadPostClass(Global.username, type, key, country, city, location, description, Global.age), uri, this) {
                Toast.makeText(this, "Spot successfully uploaded!", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
        }else {
            Database.postImage(UploadPostClass(Global.username, type, key, "", "", "", description, Global.age), uri, this) {
                Toast.makeText(this, "Picture successfully uploaded!", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
        }
    }
}