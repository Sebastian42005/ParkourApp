package com.vig.sebastian.snapchat.database

import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.vig.sebastian.snapchat.classes.Achievement
import com.vig.sebastian.snapchat.team.DisplayedTeam
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.classes.User
import com.vig.sebastian.snapchat.classes.MessageClass
import com.vig.sebastian.snapchat.explore.ExploreSearchClass
import com.vig.sebastian.snapchat.explore.FilterType
import com.vig.sebastian.snapchat.meetup.MeetUp
import com.vig.sebastian.snapchat.profile.classes.PostClass
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.classes.UploadPostClass
import kotlinx.coroutines.runInterruptible
import java.net.URLStreamHandler
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Database {

    /*
  _____        _        _
 |  __ \      | |      | |
 | |  | | __ _| |_ __ _| |__   __ _ ___  ___
 | |  | |/ _` | __/ _` | '_ \ / _` / __|/ _ \
 | |__| | (_| | || (_| | |_) | (_| \__ \  __/
 |_____/ \__,_|\__\__,_|_.__/ \__,_|___/\___|
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
 | |     ___   __ _ _ _ __
 | |    / _ \ / _` | | '_ \
 | |___| (_) | (_| | | | | |
 |______\___/ \__, |_|_| |_|
               __/ |
              |___/
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
  ______    _                _
 |  ____|  (_)              | |
 | |__ _ __ _  ___ _ __   __| |___
 |  __| '__| |/ _ \ '_ \ / _` / __|
 | |  | |  | |  __/ | | | (_| \__ \
 |_|  |_|  |_|\___|_| |_|\__,_|___/
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

    fun getFriendProfilePics(unit: (usernameList: ArrayList<String>, profilePicList: ArrayList<Uri?>) -> Unit) {
        getFriendsList(Global.username) {
            val friendsList = it
            friendsList.add(Global.username)
            var position = 0
            val profilePicList = ArrayList<Uri?>()
            val usernameList = ArrayList<String>()
            for (user in friendsList) {
                getUserProfilePic(user) { uri ->
                    profilePicList.add(uri)
                    usernameList.add(user)
                    if (position == friendsList.size - 1) {
                        unit(usernameList, profilePicList)
                    }
                    position ++
                }
            }
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
    /  \   ___| |__  _  _____   _____ _ __ ___   ___ _ __ | |_ ___
   / /\ \ / __| '_ \| |/ _ \ \ / / _ \ '_ ` _ \ / _ \ '_ \| __/ __|
  / ____ \ (__| | | | |  __/\ V /  __/ | | | | |  __/ | | | |_\__ \
 /_/    \_\___|_| |_|_|\___| \_/ \___|_| |_| |_|\___|_| |_|\__|___/
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
  _______
 |__   __|
    | | ___  __ _ _ __ ___
    | |/ _ \/ _` | '_ ` _ \
    | |  __/ (_| | | | | | |
    |_|\___|\__,_|_| |_| |_|
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createTeam(teamName: String, password: String, unit: (success: Boolean, key: String) -> Unit) {
        val key = Global.getKey()
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

    fun addUsersToTeam(userList: ArrayList<String>, key: String, teamName: String) {
        for (user in userList) {
            reference.child("Teams").child(key).child("members").child(user).setValue(user)
            reference.child(user).child(user).child("Teams").child(key).setValue(teamName)
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
        getDataFromDatabase("User", username, "Teams") {snapshot ->
            val teamsList = ArrayList<DisplayedTeam>()
            for (data in snapshot.children) {
                teamsList.add(DisplayedTeam(data.key.toString(), data.value.toString()))
            }
            unit(teamsList)
        }
    }
    /*
  __  __
 |  \/  |
 | \  / | ___  ___ ___  __ _  __ _  ___
 | |\/| |/ _ \/ __/ __|/ _` |/ _` |/ _ \
 | |  | |  __/\__ \__ \ (_| | (_| |  __/
 |_|  |_|\___||___/___/\__,_|\__, |\___|
                              __/ |
                             |___/
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
        val key = Global.getStringFromDate(Global.getCurrentTime(), "dd-MM-yyyy HH:mm:ss")
        val hm = HashMap<String, Any?>()
        hm[key] = MessageClass(message, Global.username, messageTime, key)
        reference.child("Chats").child(getMessagePath(username)).updateChildren(hm)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageToTeam(teamKey: String, message: String) {
        val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
        val key = Global.getStringFromDate(Global.getCurrentTime(), "dd-MM-yyyy HH:mm:ss")
        val hm = HashMap<String, Any?>()
        hm[key] = MessageClass(message, Global.username, messageTime, key)
        reference.child("Teams").child(teamKey).child("Chat").updateChildren(hm)
    }

    fun getMessagesFromTeam(teamKey: String, unit: (messagesList: ArrayList<MessageClass>) -> Unit) {
        getDataFromDatabase("Teams", teamKey, "Chat") {snapshot ->
            val messageList = ArrayList<MessageClass>()
            for (data in snapshot.children) {
                val message = data.child("message").value.toString()
                val username = data.child("username").value.toString()
                val time = data.child("time").value.toString()
                val key = data.key.toString()
                messageList.add(MessageClass(message, username, time, key))
            }
            unit(messageList)
        }
    }

    fun deleteUserMessage(username: String, key: String) {
        reference.child("Chats").child(getMessagePath(username)).child(key).removeValue()
    }

    fun deleteTeamMessage(teamKey: String, key: String) {
        reference.child("Teams").child(teamKey).child("Chat").child(key).removeValue()
    }

    fun getMessagesFromUser(username: String, unit: (messagesList: ArrayList<MessageClass>) -> Unit) {
        getDataFromDatabase("Chats", getMessagePath(username)) {snapshot ->
            val messageList = ArrayList<MessageClass>()
            for (data in snapshot.children) {
                val message = data.child("message").value.toString()
                val usernameDatabase = data.child("username").value.toString()
                val time = data.child("time").value.toString()
                val key = data.key.toString()
                messageList.add(MessageClass(message, usernameDatabase, time, key))
            }
            unit(messageList)
        }
    }

    /*
  __  __           _   _    _
 |  \/  |         | | | |  | |
 | \  / | ___  ___| |_| |  | |_ __
 | |\/| |/ _ \/ _ \ __| |  | | '_ \
 | |  | |  __/  __/ |_| |__| | |_) |
 |_|  |_|\___|\___|\__|\____/| .__/
                             | |
                             |_|
     */
    fun getMeetUpAcceptedUsers(key: String, teamKey: String, unit: (acceptedUsers: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "meetUps", key, "acceptedUsers") {
            val acceptedUsers = ArrayList<String>()
            for (data in it.children) {
                if (data.value.toString().toBoolean()) {
                    acceptedUsers.add(data.key.toString())
                }
            }
            unit(acceptedUsers)
        }
    }

    fun getMeetUpDeclinedUsers(key: String, teamKey: String, unit: (declinedUsers: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "meetUps", key, "acceptedUsers") {
            val declinedUsers = ArrayList<String>()
            for (data in it.children) {
                if (!data.value.toString().toBoolean()) {
                    declinedUsers.add(data.key.toString())
                }
            }
            unit(declinedUsers)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createMeetUp(meetUp: MeetUp) {
        val hm = HashMap<String, Any?>()
        val key = Global.getKey()
        hm[key] = MeetUp(meetUp.startDate, meetUp.duration, meetUp.location, meetUp.description, key, meetUp.teamKey)
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
                meetUpsList.add(MeetUp(startDate, duration, location, description, key, teamKey))
            }
            unit(meetUpsList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserMeetUps(username: String, unit: (meetUpList: ArrayList<MeetUp>) -> Unit) {
        getDataFromDatabase("User", username, "meetUps") {snapshot ->
            val meetUpsList = ArrayList<MeetUp>()
            for (data in snapshot.children) {
                val startDate = data.child("startDate").value.toString()
                val duration = data.child("duration").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val teamKey = data.child("teamKey").value.toString()
                val key = data.key.toString()
                meetUpsList.add(MeetUp(startDate, duration, location, description, key, teamKey))
            }
            unit(meetUpsList)
        }
    }

    fun acceptMeetUp(key: String, teamKey: String) {
        reference.child("User").child(Global.username).child("meetUps").child(key).child("accepted").setValue(true)
        reference.child("Teams").child(teamKey).child("meetUps").child(key).child("acceptedUsers").child(Global.username).setValue(true)
    }

    fun declineMeetUp(key: String, teamKey: String) {
        reference.child("User").child(Global.username).child("meetUps").child(key).removeValue()
        reference.child("Teams").child(teamKey).child("meetUps").child(key).child("acceptedUsers").child(Global.username).setValue(false)
    }

    /*


     */

    fun updateProfile(user: User) {
        reference.child("User").child(Global.username).child("password").setValue(user.password)
        reference.child("User").child(Global.username).child("description").setValue(user.description)
        reference.child("User").child(Global.username).child("age").setValue(user.age)
        reference.child("User").child(Global.username).child("country").setValue(user.country)
        reference.child("User").child(Global.username).child("city").setValue(user.city)
    }

    fun postImage(uploadPostClass: UploadPostClass, imageUri: Uri, context: Context, unit: () -> Unit) {
        storageReference.child(Global.username).child("Posts").child(uploadPostClass.key).putFile(imageUri).addOnSuccessListener {
            val hm = HashMap<String, Any?>()
            hm[uploadPostClass.key] = uploadPostClass
            reference.child("User").child(Global.username).child("Posts").updateChildren(hm)
            reference.child("Posts").updateChildren(hm)
            unit()
        }.addOnFailureListener {
            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    fun getExplorePostsNoUri(unit: (postList: ArrayList<UploadPostClass>) -> Unit) {
        getSingleDataFromDatabase("Posts") {snapshot ->
            val postList = ArrayList<UploadPostClass>()
            for (data in snapshot.children) {
                val username = data.child("username").value.toString()
                val country = data.child("country").value.toString()
                val key = data.child("key").value.toString()
                val city = data.child("city").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val userAge = data.child("userAge").value.toString().toInt()
                val postType : PostType = PostType.valueOf(data.child("postType").value.toString().trim())
                postList.add(UploadPostClass(username, postType, key, country, city, location, description, userAge))
            }
            postList.shuffle()
            unit(postList)
        }
    }

    fun getExplorePosts(countryFilter: String, cityFilter: String, unit: (postList: ArrayList<UploadPostClass>) -> Unit) {
        getExplorePostsNoUri { postList ->
            val filteredPostList = ArrayList<UploadPostClass>()
            for (post in postList) {
                if (countryFilter == "") {
                    if (cityFilter == "") {
                        filteredPostList.add(post)
                    }else if (post.city.trim().toLowerCase() == cityFilter.trim().toLowerCase()) {
                        filteredPostList.add(post)
                    }
                }else if (post.country.trim().toLowerCase() == countryFilter.trim().toLowerCase()) {
                    if (cityFilter == "") {
                        filteredPostList.add(post)
                    }else if (post.city.trim().toLowerCase() == cityFilter.trim().toLowerCase()) {
                        filteredPostList.add(post)
                    }
                }
            }
            if (filteredPostList.size <  20) {
                for (post in filteredPostList) {
                    getImageUriFromUser(post.username, post.key) { uri ->
                        ImageUriListsObject.setPostImageUriHashMap(post.key, uri)
                        if (post.key == filteredPostList[filteredPostList.size - 1].key) {
                            unit(filteredPostList)
                        }
                    }
                }
            }else {
                for (position in 0..20) {
                    val post = filteredPostList[position]
                    getImageUriFromUser(post.username, post.key) { uri ->
                        ImageUriListsObject.setPostImageUriHashMap(post.key, uri)
                        if (post.key == filteredPostList[filteredPostList.size - 1].key) {
                            val endFilteredList = ArrayList<UploadPostClass>()
                                for (uploadPostPosition in 0..20) {
                                    endFilteredList.add(filteredPostList[uploadPostPosition])
                                }
                            unit(endFilteredList)
                        }
                    }
                }
            }
        }
    }

    fun getPostsFromUser(username: String, unit : (postList: ArrayList<PostClass>) -> Unit) {
        getDataFromDatabase("User", username, "Posts") {snapshot ->
            val list = ArrayList<PostClass>()
            var position = 0
            for (data in snapshot.children) {
                val postType = PostType.valueOf(data.child("postType").value.toString())
                val country = data.child("country").value.toString()
                val city = data.child("city").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val userAge = data.child("userAge").value.toString().toInt()
                list.add(PostClass(UploadPostClass(username, postType, data.key.toString(), country, city, location, description, userAge), position, null, null))
                position ++
            }
            list.reverse()
            unit(list)
        }
    }

    fun getImageUriFromUser(username: String, key: String, unit: (uri: Uri) -> Unit) {
        storageReference.child(username).child("Posts").child(key).downloadUrl.addOnSuccessListener {
            unit(it)
        }
    }
    fun getUserProfilePic(username: String, unit : (uri: Uri?) -> Unit) {
        storageReference.child(username).child("ProfilePic").downloadUrl.addOnSuccessListener {
            unit(it)
        }.addOnFailureListener { unit(null) }
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
    fun getEveryUser(filter: String, filterType: FilterType, unit: (userList: ArrayList<ExploreSearchClass>) -> Unit) {
        getSingleDataFromDatabase("User") {
            val userList = ArrayList<ExploreSearchClass>()
            for (data in it.children) {
                if (data.key.toString() != Global.username) {
                    if (filter == "") {
                        userList.add(ExploreSearchClass(data.key.toString(), getImportance(data), null))
                    }else {
                        if (filterType == FilterType.COUNTRY) {
                            if (checkFilter(data.child("country").value, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data), null))
                            }
                        }else if (filterType == FilterType.CITY) {
                            if (checkFilter(data.child("city").value, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data), null))
                            }
                        }else if (filterType == FilterType.USERNAME) {
                            if (checkFilter(data.key, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data), null))
                            }
                        }else if (filterType == FilterType.AGE) {
                            if (checkFilter(data.child("age").value, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data), null))
                            }
                        }
                    }
                }
            }
            userList.sort()
            if (userList.size != 0) {
                if (userList.size < 10) {
                    for (user in userList) {
                        getUserProfilePic(user.username) { uri ->
                            if (uri != null) ImageUriListsObject.setProfilePicImageUriHashMap(
                                user.username,
                                uri
                            )
                            if (user.username == userList[userList.size - 1].username) {
                                unit(userList)
                            }
                        }
                    }
                } else {
                    val list = ArrayList<ExploreSearchClass>()
                    for (userPosition in 0..10) {
                        val user = userList[userPosition]
                        list.add(user)
                    }
                    for (user in list) {
                        getUserProfilePic(user.username) { uri ->
                            if (uri != null) ImageUriListsObject.setProfilePicImageUriHashMap(
                                user.username,
                                uri
                            )
                            if (user.username == list[list.size - 1].username) {
                                unit(list)
                            }
                        }
                    }
                }
            }else unit(userList)
        }
    }
    fun getEveryUsername(unit: (userList: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("User") {
            val userList = ArrayList<String>()
            for (data in it.children) {
                userList.add(data.key.toString())
            }
            unit(userList)
        }
    }
    private fun checkFilter(string: Any?, filter: String): Boolean {
        val s = string.toString().trim().toLowerCase()
        return s.contains(filter)
    }
    private fun getImportance(data: DataSnapshot) : Int{
        var importance = 0
        if (data.child("country").value.toString().toLowerCase().trim() == Global.country.toLowerCase().trim())
            importance++
        if (data.child("city").value.toString().toLowerCase().trim() == Global.city.toLowerCase().trim())
            importance++
        if (data.child("age").value.toString().toInt() == Global.age)
            importance++

        return importance
    }

    fun getEveryPostFromFriendsNoUri(unit: (postKeyList: ArrayList<PostClass>) -> Unit) {
        getSingleDataFromDatabase("User") { snapshot ->
            val friendsList = ArrayList<String>()
            friendsList.add(Global.username)
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
                    val userAge = data2.child("userAge").value.toString().toInt()
                    postKeyList.add(PostClass(UploadPostClass(
                        friend,
                        postType,
                        data2.key.toString(),
                        country,
                        city,
                        location,
                        description,
                        userAge), 0, null, null))
                }
            }
            unit(postKeyList)
        }
    }

    fun getFirst10PostsFromFriends(unit: (postKeyList: ArrayList<PostClass>) -> Unit) {
        getEveryPostFromFriendsNoUri {
            val postKeyList = it
            postKeyList.sort()
            if (postKeyList.size < 10) {
                for (post in postKeyList) {
                    getImageUriFromUser(post.uploadPostClass.username, post.uploadPostClass.key) { uri ->
                        ImageUriListsObject.setPostImageUriHashMap(post.uploadPostClass.key, uri)
                        if (post.uploadPostClass.key == postKeyList[postKeyList.size - 1].uploadPostClass.key) {
                            unit(postKeyList)
                        }
                    }
                }
            }else {
                for (position in 0..10) {
                    getImageUriFromUser(postKeyList[position].uploadPostClass.username, postKeyList[position].uploadPostClass.key) { uri ->
                        ImageUriListsObject.setPostImageUriHashMap(postKeyList[position].uploadPostClass.key, uri)
                        if (postKeyList[position].uploadPostClass.key == postKeyList[postKeyList.size - 1].uploadPostClass.key) {
                            unit(postKeyList)
                        }
                    }
                }
            }
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

    fun getAccepted(key: String, unit: (accepted: Boolean) -> Unit) {
        getSingleDataFromDatabase("User", Global.username, "meetUps", key, "accepted") {
            if (it.value.toString() == "null") unit(false) else unit(true)
        }
    }
}