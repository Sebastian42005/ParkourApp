package com.vig.sebastian.snapchat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.test.database.Database
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.team.DisplayedTeam
import java.lang.Exception

class TeamsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_teams, container, false)
        setTeamList(root)

        return root
    }
    private fun setTeamList(root: View) {
        val teamsList = ArrayList<DisplayedTeam>()
        Database.getUserTeams(Global.username) {databaseTeamsList ->
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