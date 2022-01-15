package com.vig.sebastian.snapchat.shortcuts

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.graphics.drawable.toIcon
import com.vig.sebastian.snapchat.LoadingScreenActivity
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.chat.team.ClickedTeamChatObject
import com.vig.sebastian.snapchat.chat.team.TeamChatActivity


@RequiresApi(Build.VERSION_CODES.N_MR1)
object Shortcuts {
    fun createTeamShortcut(context: Context, teamKey: String, teamName: String, admin: String, password: String) {
        val shortcutManager = getSystemService(context, ShortcutManager::class.java)

        val intent = Intent(context, LoadingScreenActivity::class.java)
        intent.putExtra("key", teamKey)
        intent.putExtra("name", teamName)
        intent.putExtra("admin", admin)
        intent.putExtra("password", password)
        intent.action = Intent.ACTION_VIEW

        val shortcutTeam = ShortcutInfo.Builder(context, teamKey)
            .setShortLabel(teamName)
            .setLongLabel(teamName)
            .setIcon(Icon.createWithResource(context, R.drawable.team_icon))
            .setIntent(intent)
            .build()

        shortcutManager!!.dynamicShortcuts = listOf(shortcutTeam)
    }
}