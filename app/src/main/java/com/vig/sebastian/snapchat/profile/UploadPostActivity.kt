package com.vig.sebastian.snapchat.profile

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.test.database.Database
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import java.util.*

class UploadPostActivity : AppCompatActivity() {
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_post)

        val uri = PostObject.uri!!
        val imageView: ImageView = findViewById(R.id.uploadPostImageView)
        val typeSwitch: Switch = findViewById(R.id.uploadPostTypeSwitch)
        val descriptionEditText: EditText = findViewById(R.id.uploadPostDescriptionEditText)
        val countryEditText: EditText = findViewById(R.id.uploadPostCountryEditText)
        val cityEditText: EditText = findViewById(R.id.uploadPostCityEditText)
        val locationEditText: EditText = findViewById(R.id.uploadPostLocationEditText)
        val uploadBtn: Button = findViewById(R.id.uploadPostBtn)

        imageView.setImageURI(uri)

        typeSwitch.setOnClickListener {
            if (!typeSwitch.isChecked) {
                countryEditText.visibility = View.GONE
                cityEditText.visibility = View.GONE
                locationEditText.visibility = View.GONE
            }else {
                countryEditText.visibility = View.VISIBLE
                cityEditText.visibility = View.VISIBLE
                locationEditText.visibility = View.VISIBLE
            }
        }

        uploadBtn.setOnClickListener {
            if (locationEditText.text.toString().trim() != "") {
                var country = countryEditText.text.toString().trim()
                var city = cityEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val location = locationEditText.text.toString().trim()
                var type = PostType.PICTURE
                val key = UUID.randomUUID().toString()

                if (typeSwitch.isEnabled) type = PostType.PARKOUR_SPOT
                if (country == "") country = Global.country
                if (city == "") city = Global.city
                Database.postImage(UploadPostClass(Global.username, type, key, country, city, location, description), uri, this) {
                    if (type == PostType.PARKOUR_SPOT) {
                        Toast.makeText(this, "Spot successfully uploaded!", Toast.LENGTH_SHORT).show()
                    }else Toast.makeText(this, "Picture successfully uploaded!", Toast.LENGTH_SHORT).show()
                    super.onBackPressed()
                }
            }
        }
    }
}