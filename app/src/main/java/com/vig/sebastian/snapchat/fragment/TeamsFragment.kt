package com.vig.sebastian.snapchat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.chat.team.ClickedTeamChatObject
import com.vig.sebastian.snapchat.chat.team.TeamChatActivity
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.team.CreateTeamActivity
import com.vig.sebastian.snapchat.team.DisplayedTeam
import java.lang.Exception

class TeamsFragment : Fragment() {
    val teamsList = ArrayList<DisplayedTeam>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_teams, container, false)
        val teamsListView = root.findViewById<ListView>(R.id.teamsListView)
        val createTeamBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton = root.findViewById(R.id.createTeamBtn)
        val refreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout = root.findViewById(R.id.refreshLayout)

        refreshLayout.setOnRefreshListener {
            setTeamList(root)
            refreshLayout.isRefreshing = false
        }

        setTeamList(root)

        createTeamBtn.setOnClickListener {
            startActivity(Intent(context, CreateTeamActivity::class.java))
        }

        teamsListView.setOnItemClickListener { parent, view, position, id ->
            ClickedTeamChatObject.teamKey = teamsList[position].key
            ClickedTeamChatObject.teamName = teamsList[position].teamName
            Database.getTeamAdmin(ClickedTeamChatObject.teamKey) { admin ->
                ClickedTeamChatObject.admin = admin
                startActivity(Intent(requireContext(), TeamChatActivity::class.java))
            }
        }

        return root
    }
    private fun setTeamList(root: View) {
        Database.getUserTeams(Global.username) { databaseTeamsList ->
            teamsList.clear()
            for (team in databaseTeamsList) {
                teamsList.add(DisplayedTeam(team.key, team.teamName))
            }
            val teamsListView = root.findViewById<ListView>(R.id.teamsListView)
            try {
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, teamsList)
                teamsListView.adapter = adapter
            }catch (e: Exception) {}
        }
    }
}