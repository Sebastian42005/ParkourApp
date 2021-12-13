package com.vig.sebastian.snapchat.settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class SettingsAdapter(context: Context, private val int: Int, arrayList : ArrayList<String>) : ArrayAdapter<String>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val text = getItem(position)!!
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)

        val textView = view.findViewById<TextView>(R.id.settingsTextView)
        textView.text = text

        return view
    }
}