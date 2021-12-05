package com.example.test.database

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.common.internal.GmsLogger
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.vig.sebastian.snapchat.classes.Achievement
import com.vig.sebastian.snapchat.team.DisplayedTeam
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.classes.User
import com.vig.sebastian.snapchat.classes.Message
import com.vig.sebastian.snapchat.database.FirebaseHelper
import com.vig.sebastian.snapchat.explore.ExploreSearchClass
import com.vig.sebastian.snapchat.fragment.CurrentFragmentEnum
import com.vig.sebastian.snapchat.meetup.MeetUp
import com.vig.sebastian.snapchat.profile.classes.PostClass
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.classes.UploadPostClass
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Database {

    /*
  ___        _        _
 |  __ \      | |      | |
 | |  | | _ _| | _ _| |_   _ _ __  _
 | |  | |/ ` | _/ ` | ' \ / ` / _|/ _ \
 | |_| | (| | || (| | |) | (| \_ \  __/
 |__/ \,|\_\,|./ \,|_/\___|
     */

    val reference = FirebaseDatabase.getInstance("https://parkour-b3ba9-default-rtdb.europe-west1.firebasedatabase.app/").reference
    val storageReference = FirebaseStorage.getInstance("gs://parkour-b3ba9.appspot.com/").getReference("profiles")
    val helper = FirebaseHelper.getInstance(reference)

    fun getSingleDataFromDatabase(vararg pathList: String, unit: (snapshot: DataSnapshot) -> Unit) {
        var path = ""
        for (currentPath in pathList) {
            path += "$currentPath/"
        }
        reference.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                unit(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun getDataFromDatabase(vararg pathList: String, unit: (snapshot: DataSnapshot) -> Unit) {
        var path = ""
        for (currentPath in pathList) {
            path += "$currentPath/"
        }
        reference.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                unit(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    /*
  _                 _
 | |               (_)
 | |     _   _ _ _ _ _
 | |    / _ \ / ` | | ' \
 | |_| () | (| | | | | |
 |__\__/ \, ||| ||
               __/ |
              |_/
     */
    fun register(user: User, unit : () -> Unit) {
        getSingleDataFromDatabase("User", user.username) {snapshot ->
            if (snapshot.value == null) {
                val hm = HashMap<String, Any?>()
                hm[user.username] = user
                reference.child("User").updateChildren(hm)
                unit()
            }
        }
    }

    fun login(context: Context, username: String, password: String, unit : (user: User?) -> Unit) {
        getSingleDataFromDatabase("User", username) {snapshot ->
            if (snapshot.value != null) {
                getSingleDataFromDatabase( "User", username) {snapshot1 ->
                    val username1 = snapshot1.child("username").value.toString()
                    val password1 = snapshot1.child("password").value.toString()
                    val country = snapshot1.child("country").value.toString()
                    val city = snapshot1.child("city").value.toString()
                    val age = snapshot1.child("age").value.toString().toInt()
                    val description = snapshot1.child("description").value.toString()
                    val userClass = User(username1, password1, description, country, city, age)
                    if (userClass.password == password.trim()) {
                        unit(userClass)
                    }else {
                        Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show()
                        unit(null)
                    }
                }
            }else {
                Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show()
                unit(null)
            }
        }
    }
    /*
  __    _                _
 |  _|  ()              | |
 | |_ _ _ _  _ _ _   _| |_
 |  _| '| |/ _ \ ' \ / ` / _|
 | |  | |  | |  _/ | | | (| \__ \
 ||  ||  ||\_|| ||\,|_/
     */
    fun getFriendsList(username: String, unit: (friendsList: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("User", username, "friends") {
            val friendsList = ArrayList<String>()
            for (friend in it.children) {
                friendsList.add(friend.value.toString())
            }
            unit(friendsList)
        }
    }

    fun getFriendRequestList(username: String, unit: (friendRequestsList: ArrayList<String>) -> Unit) {
        getDataFromDatabase("User", username, "friendRequests") {
            val friendsList = ArrayList<String>()
            for (friend in it.children) {
                friendsList.add(friend.value.toString())
            }
            unit(friendsList)
        }
    }
    fun acceptFriendRequest(username: String) {
        getFriendRequestList(Global.username) {
            if (it.contains(username)) {
                reference.child("User").child(Global.username).child("friendRequests").child(username).removeValue()
                reference.child("User").child(Global.username).child("friends").child(username).setValue(username)
                reference.child("User").child(username).child("friends").child(Global.username).setValue(Global.username)
            }
        }
    }
    fun removeFriendRequest(username: String) {
        reference.child("User").child(username).child("friendRequests").child(Global.username).removeValue()
    }

    fun sendFriendRequest(username: String) {
        getFriendsList(Global.username) { friendsList ->
            if (!friendsList.contains(username)) {
                reference.child("User").child(username).child("friendRequests").child(Global.username)
                    .setValue(Global.username)
            }
        }
    }
    fun unfollowFriend(username: String) {
        reference.child("User").child(username).child("friends").child(Global.username).removeValue()
        reference.child("User").child(Global.username).child("friends").child(username).removeValue()
    }

    /*
               _     _                                     _
     /\       | |   (_)                                   | |
    /  \   _| |_  _  __   __ _ _ _   _ _ _ | | _
   / /\ \ / _| ' \| |/ _ \ \ / / _ \ '_ ` _ \ / _ \ '_ \| _/ _|
  / __ \ (_| | | | |  _/\ V /  _/ | | | | |  _/ | | | |\_ \
 //    \\__|| |||\__| \/ \__|| || ||\__|| ||\|__/
     */

    fun setAchievement(achievement: Achievement) {
        reference.child("User").child(Global.username).child("achievement").setValue(achievement)
    }
    fun getAchievement(unit: (achievement: Achievement) -> Unit) {
        getSingleDataFromDatabase("User", Global.username, "achievement") { snapshot ->
            if (snapshot.value != null) {
                unit(Achievement.valueOf(snapshot.value.toString()))
            }else unit(Achievement.NOTHING)
        }
    }
    fun addAchievement(achievement: Achievement) {
        reference.child("User").child(Global.username).child("achievements").child(achievement.toString()).setValue(achievement)
    }
    fun getAchievementList(unit : (achievementsList: ArrayList<Achievement>) -> Unit) {
        getSingleDataFromDatabase("User", Global.username, "achievements") {
            val achievementsList = ArrayList<Achievement>()
            for (achievement in it.children) {
                achievementsList.add(Achievement.valueOf(achievement.value.toString()))
            }
            unit(achievementsList)
        }
    }
    /*
  ___
 |_   _|
    | | _  _ _ _ _ _
    | |/ _ \/ ` | ' ` _ \
    | |  _/ (| | | | | | |
    ||\_|\,|| || |_|
     */
    fun createTeam(teamName: String, password: String, unit: (success: Boolean, key: String) -> Unit) {
        val key = UUID.randomUUID().toString()
        if (teamName.trim() != "") {
            if (password.trim() != "") {
                reference.child("Teams").child(key).child("teamName").setValue(teamName.trim())
                reference.child("Teams").child(key).child("password").setValue(password.trim())
                reference.child("User").child(Global.username).child("Teams").child(key).setValue(teamName)
                reference.child("Teams").child(key).child("members").child(Global.username).setValue(Global.username)
                reference.child("Teams").child(key).child("admin").setValue(Global.username).addOnSuccessListener {
                    unit(true, key)
                }
            }else unit(false, "")
        }else unit(false, "")
    }
    fun joinTeam(key: String, password: String, unit: (success: Boolean) -> Unit) {
        getSingleDataFromDatabase("Teams", key) {snapshot ->
            if (snapshot.value != null) {
                if (snapshot.child("password").value.toString().trim() == password.trim()) {
                    getTeamMembers(key) {memberList ->
                        if (!memberList.contains(Global.username)) {
                            reference.child("User").child(Global.username).child("Teams").child(key).setValue(snapshot.child("teamName").value.toString())
                            reference.child("Teams").child(key).child("members").child(Global.username).setValue(Global.username).addOnSuccessListener {
                                unit(true)
                            }
                        }else unit(false)
                    }
                }else unit(false)
            }else unit(false)
        }
    }
    fun getTeamMembers(teamKey: String, unit: (memberList: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "members") {snapshot ->
            val teamMemberList = ArrayList<String>()
            for (member in snapshot.children) {
                teamMemberList.add(member.key.toString())
            }
            unit(teamMemberList)
        }
    }
    fun getUserTeams(username: String, unit: (teamsList: ArrayList<DisplayedTeam>) -> Unit) {
        getSingleDataFromDatabase("User", username, "Teams") {snapshot ->
            val teamsList = ArrayList<DisplayedTeam>()
            for (data in snapshot.children) {
                teamsList.add(DisplayedTeam(data.key.toString(), data.value.toString()))
            }
            unit(teamsList)
        }
    }
    /*
  _  _
 |  \/  |
 | \  / | _  _ _  _ _  _ _  _
 | |\/| |/ _ \/ _/ _|/ _` |/ _` |/ _ \
 | |  | |  _/\_ \__ \ (| | (| |  __/
 ||  ||\__||_/_/\,|\_, |\__|
                              __/ |
                             |_/
     */
    private fun getMessagePath(username: String) : String{
        val list = ArrayList<String>()
        list.add(Global.username)
        list.add(username)
        list.sort()
        return list[0] + "|" + list[1]
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageToUser(username: String, message: String) {
        val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
        val hm = HashMap<String, Any?>()
        hm[messageTime] = Message(message, Global.username, messageTime)
        reference.child("Chats").child(getMessagePath(username)).updateChildren(hm)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageToTeam(teamKey: String, message: String) {
        val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
        val hm = HashMap<String, Any?>()
        hm[messageTime] = Message(message, Global.username, messageTime)
        reference.child("Teams").child(teamKey).child("Chat").updateChildren(hm)
    }
    fun getMessagesFromTeam(teamKey: String, unit: (messagesList: ArrayList<Message>) -> Unit) {
        getDataFromDatabase("Teams", teamKey, "Chat") {snapshot ->
            val messageList = ArrayList<Message>()
            for (data in snapshot.children) {
                val message = data.child("message").value.toString()
                val username = data.child("username").value.toString()
                val time = data.child("time").value.toString()
                messageList.add(Message(message, username, time))
            }
            unit(messageList)
        }
    }
    fun getMessagesFromUser(username: String, unit: (messagesList: ArrayList<Message>) -> Unit) {
        getDataFromDatabase("Chats", getMessagePath(username)) {snapshot ->
            val messageList = ArrayList<Message>()
            for (data in snapshot.children) {
                val message = data.child("message").value.toString()
                val usernameDatabase = data.child("username").value.toString()
                val time = data.child("time").value.toString()
                messageList.add(Message(message, usernameDatabase, time))
            }
            unit(messageList)
        }
    }
    /*
  _  _           _   _    _
 |  \/  |         | | | |  | |
 | \  / | _  _| || |  | | __
 | |\/| |/ _ \/ _ \ _| |  | | ' \
 | |  | |  _/  _/ || || | |) |
 ||  ||\__|\_|\|\_/| .__/
                             | |
                             |_|
     */
    fun createMeetUp(meetUp: MeetUp) {
        val hm = HashMap<String, Any?>()
        val key = UUID.randomUUID().toString()
        hm[key] = MeetUp(meetUp.startDate, meetUp.duration, meetUp.location, meetUp.description, meetUp.key)
        reference.child("Teams").child(meetUp.key).child("meetUps").updateChildren(hm)
        getTeamMembers(meetUp.key) { memberList ->
            for (member in memberList) {
                reference.child("User").child(member).child("meetUps").updateChildren(hm)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamMeetUps(teamKey: String, unit: (meetUpsList: ArrayList<MeetUp>) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "meetUps") {snapshot ->
            val meetUpsList = ArrayList<MeetUp>()
            for (data in snapshot.children) {
                val startDateDatabase = data.child("startDate").value.toString()
                val duration = data.child("duration").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val key = data.key.toString()
                val startDate = startDateDatabase.replace("-", ".").trim().replace(" ", " | ")
                meetUpsList.add(MeetUp(startDate, duration, location, description, key))
            }
            unit(meetUpsList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserMeetUps(username: String, unit: (meetUpList: ArrayList<MeetUp>) -> Unit) {
        getSingleDataFromDatabase("User", username, "meetUps") {snapshot ->
            val meetUpsList = ArrayList<MeetUp>()
            for (data in snapshot.children) {
                val startDate = data.child("startDate").value.toString()
                val duration = data.child("duration").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val key = data.key.toString()
                meetUpsList.add(MeetUp(startDate, duration, location, description, key))
            }
            unit(meetUpsList)
        }
    }
    fun updateProfile(user: User) {
        reference.child("User").child(Global.username).child("password").setValue(user.password)
        reference.child("User").child(Global.username).child("description").setValue(user.description)
        reference.child("User").child(Global.username).child("age").setValue(user.age)
        reference.child("User").child(Global.username).child("country").setValue(user.country)
        reference.child("User").child(Global.username).child("city").setValue(user.city)
    }

    /*

     */

    fun postImage(uploadPostClass: UploadPostClass, imageUri: Uri, context: Context, unit: () -> Unit) {
        val hm = HashMap<String, Any?>()
        hm[uploadPostClass.key] = uploadPostClass
        reference.child("User").child(Global.username).child("Posts").updateChildren(hm)
        reference.child("Posts").updateChildren(hm)
        storageReference.child(Global.username).child("Posts").child(uploadPostClass.key).putFile(imageUri).addOnSuccessListener {
            unit()
        }.addOnFailureListener {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getExplorePosts(unit: (postList: ArrayList<UploadPostClass>) -> Unit) {
        getSingleDataFromDatabase("Posts") {snapshot ->
            val postList = ArrayList<UploadPostClass>()
            for (data in snapshot.children) {
                val username = data.child("username").value.toString()
                val country = data.child("country").value.toString()
                val key = data.child("key").value.toString()
                val city = data.child("city").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val postType : PostType = PostType.valueOf(data.child("postType").value.toString().trim())
                postList.add(UploadPostClass(username, postType, key, country, city, location, description))
            }
            postList.shuffle()
            unit(postList)
        }
    }

    fun getPostsFromUser(username: String, unit : (postList: ArrayList<PostClass>) -> Unit) {
        getSingleDataFromDatabase("User", username, "Posts") {snapshot ->
            val list = ArrayList<PostClass>()
            var position = 0
            for (data in snapshot.children) {
                val postType = PostType.valueOf(data.child("postType").value.toString())
                val country = data.child("country").value.toString()
                val city = data.child("city").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                list.add(PostClass(UploadPostClass(username, postType, data.key.toString(), country, city, location, description), position))
                position ++
            }
            unit(list)
        }
    }

    fun getImageUriFromUser(username: String, key: String, unit: (uri: Uri) -> Unit) {
        storageReference.child(username).child("Posts").child(key).downloadUrl.addOnSuccessListener {
            unit(it)
        }
    }
    fun getUserProfilePic(username: String, unit : (uri: Uri) -> Unit) {
        storageReference.child(username).child("ProfilePic").downloadUrl.addOnSuccessListener {
            unit(it)
        }
    }
    fun getUserInfo(username: String, unit : (user: User) -> Unit) {
        getSingleDataFromDatabase("User", username) {
            val databaseUsername = it.child("username").value.toString()
            val password = it.child("password").value.toString()
            val country = it.child("country").value.toString()
            val city = it.child("city").value.toString()
            val description = it.child("description").value.toString()
            val age = it.child("age").value.toString().toInt()
            unit(User(databaseUsername, password, description, country, city, age))

        }
    }
    fun getEveryUser(unit: (userList: ArrayList<ExploreSearchClass>) -> Unit) {
        getSingleDataFromDatabase("User") {
            val userList = ArrayList<ExploreSearchClass>()
            for (data in it.children) {
                if (data.key.toString() != Global.username) {
                    var importance = 0
                    if (data.child("country").value.toString().toLowerCase().trim() == Global.country.toLowerCase().trim())
                        importance++
                    if (data.child("city").value.toString().toLowerCase().trim() == Global.city.toLowerCase().trim())
                        importance++


                    if (data.child("age").value.toString().toInt() == Global.age)
                        importance++

                    userList.add(ExploreSearchClass(data.key.toString(), importance))
                }
            }
            userList.sort()
            unit(userList)
        }
    }
    fun getEveryPostFromFriends(unit: (postKeyList: ArrayList<PostClass>) -> Unit) {
        getSingleDataFromDatabase("User") { snapshot ->
            val friendsList = ArrayList<String>()
            for (data1 in snapshot.child(Global.username).child("friends").children) {
                friendsList.add(data1.value.toString())
            }
            val postKeyList = ArrayList<PostClass>()
            for (friend in friendsList) {
                for (data2 in snapshot.child(friend).child("Posts").children) {
                    val postType = PostType.valueOf(data2.child("postType").value.toString())
                    val country = data2.child("country").value.toString()
                    val city = data2.child("city").value.toString()
                    val location = data2.child("location").value.toString()
                    val description = data2.child("description").value.toString()
                    postKeyList.add(PostClass(UploadPostClass(friend, postType, data2.key.toString(), country, city, location, description), 0))
                }
            }
            unit(postKeyList)
        }
    }

    fun removeLikeFromPost(key: String) {
        reference.child("Posts").child(key).child("likeList").child(Global.username).removeValue()
    }

    fun likePost(key: String) {
        reference.child("Posts").child(key).child("likeList").child(Global.username).setValue(Global.username)
    }

    fun getPostLikeList(key: String, unit: (likesList: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("Posts", key, "likeList") {snapshot ->
            val likeList = ArrayList<String>()
            for (data in snapshot.children) {
                likeList.add(data.value.toString())
            }
            unit(likeList)
        }
    }
    /*
  ____             _      ____        _   _
 |  _ \           | |    |  _ \      | | | |
 | |_) | __ _  ___| | __ | |_) |_   _| |_| |_ ___  _ __
 |  _ < / _` |/ __| |/ / |  _ <| | | | __| __/ _ \| '_ \
 | |_) | (_| | (__|   <  | |_) | |_| | |_| || (_) | | | |
 |____/ \__,_|\___|_|\_\ |____/ \__,_|\__|\__\___/|_| |_|
     */

    fun pressBackBtn(currentFragmentEnum: CurrentFragmentEnum) {
        reference.child("User").child(Global.username).child("backBtn").setValue(currentFragmentEnum)
    }

    fun backBtnPressed(unit: (currentFragmentEnum: CurrentFragmentEnum) -> Unit) {
        getDataFromDatabase("User", Global.username, "backBtn") {snapshot ->
            val currentFragmentEnum : CurrentFragmentEnum?
            if (snapshot.value.toString() != "null") {
                currentFragmentEnum = CurrentFragmentEnum.valueOf(snapshot.value.toString())
            }else currentFragmentEnum = CurrentFragmentEnum.NOTHING
            unit(currentFragmentEnum)
        }
    }
}