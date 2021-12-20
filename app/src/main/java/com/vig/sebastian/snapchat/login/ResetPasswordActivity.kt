package com.vig.sebastian.snapchat.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        val emailEditText: EditText = findViewById(R.id.resetEmailEditText)
        val resetPasswordBtn: Button  = findViewById(R.id.resetPasswordBtn)

        resetPasswordBtn.setOnClickListener {
            Toast.makeText(this, getString(R.string.password_reset_check_mails), Toast.LENGTH_SHORT).show()
            Database.resetPassword(emailEditText.text.toString().trim())
        }
    }
}