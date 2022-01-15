package com.vig.sebastian.snapchat.database

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.classes.Achievement
import com.vig.sebastian.snapchat.classes.MessageClass
import com.vig.sebastian.snapchat.classes.User
import com.vig.sebastian.snapchat.explore.ExploreSearchClass
import com.vig.sebastian.snapchat.explore.FilterType
import com.vig.sebastian.snapchat.meetup.MeetUp
import com.vig.sebastian.snapchat.parkour_spots.SpotDatabaseClass
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.SpotType
import com.vig.sebastian.snapchat.profile.classes.PostClass
import com.vig.sebastian.snapchat.profile.classes.UploadPostClass
import com.vig.sebastian.snapchat.team.DisplayedTeam


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
    val referenceSupport = FirebaseDatabase.getInstance("https://parkoursupport-default-rtdb.europe-west1.firebasedatabase.app/").reference
    val storageReference = FirebaseStorage.getInstance().getReference("profiles")
    val teamStorageReference = FirebaseStorage.getInstance().getReference("teams")
    val mAuth = FirebaseAuth.getInstance(reference.database.app)
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
    fun register(user: User, password: String, unit : (exception: String) -> Unit) {
        if (password.trim().length >= 6) {
            mAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener {
                    try {
                        it.result.user!!.sendEmailVerification()
                        val hm = HashMap<String, Any?>()
                        hm[user.username] = user
                        reference.child("User").updateChildren(hm)
                        reference.child("Emails").child(Global.getKeyFromEmail(user.email)).setValue(user.username)
                        unit("")
                    }catch (e: Exception) {
                        unit("email")
                    }
                }
        }else unit("6")
    }

    fun resetPassword(email: String) {
        if (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mAuth.sendPasswordResetEmail(email)
        }
    }

    fun login(editor: SharedPreferences.Editor, context: Context, email: String, password: String, unit : (user: User?) -> Unit) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                if(it.result.user!!.isEmailVerified) {
                    getUsername(email) { username ->
                        getUserInfo(username) { user ->
                            unit(user)
                        }
                    }
                }else {
                    it.result.user!!.sendEmailVerification();
                    Toast.makeText(context, context.getString(R.string.email_must_be_verified), Toast.LENGTH_LONG).show()
                }
            }else {
                unit( null)
                editor.clear()
                editor.apply()
                Toast.makeText(context, context.getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getUsername(email: String, unit: (username: String) -> Unit) {
        getSingleDataFromDatabase("Emails", Global.getKeyFromEmail(email)) {
            unit(it.value.toString())
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

    fun getFriendProfilePics(unit: (usernameList: ArrayList<String>, profilePicList: ArrayList<Uri>) -> Unit) {
        getFriendsList(Global.username) {
            val friendsList = it
            friendsList.add(Global.username)
            var position = 0
            val profilePicList = ArrayList<Uri>()
            val usernameList = ArrayList<String>()
            for (user in friendsList) {
                getUserProfilePic(user) { uri ->
                    profilePicList.add(uri)
                    usernameList.add(user)
                    if (position == friendsList.size - 1) {
                        unit(usernameList, profilePicList)
                    }
                    position++
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
    fun declineFriendRequest(username: String) {
        reference.child("User").child(Global.username).child("friendRequests").child(username).removeValue()
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
            }else unit(false, "password")
        }else unit(false, "teamName")
    }
    fun getTeamName(teamKey: String, unit: (teamName: String) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "teamName") {
            unit(it.value.toString())
        }
    }

    fun joinTeam(teamKey: String, password: String, context: Context, unit: (error : String) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey) {
            if (it.value.toString() != "null") {
                if (it.child("password").value.toString() == password) {
                    getTeamMembers(teamKey) { memberList ->
                        if (!memberList.contains(Global.username)) {
                            getTeamName(teamKey) { teamName ->
                                reference.child("User").child(Global.username).child("Teams")
                                    .child(teamKey).setValue(teamName)
                                reference.child("Teams").child(teamKey).child("members")
                                    .child(Global.username).setValue(Global.username)
                                Toast.makeText(
                                    context,
                                    "Successfully joined team $teamName",
                                    Toast.LENGTH_SHORT
                                ).show()
                                unit("")
                            }
                        } else unit("team")
                    }
                } else unit("password")
            }else unit("key")
        }
    }

    fun removeUserFromTeam(teamKey: String, username: String) {
        reference.child("Teams").child(teamKey).child("members").child(username).removeValue()
        reference.child("User").child(username).child("Teams").child(teamKey).removeValue()
    }

    fun addUsersToTeam(userList: ArrayList<String>, key: String, teamName: String) {
        for (user in userList) {
            reference.child("Teams").child(key).child("members").child(user).setValue(user)
            reference.child("User").child(user).child("Teams").child(key).setValue(teamName)
        }
    }

    fun getTeamAdmin(teamKey: String, unit: (admin: String) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "admin") {
            unit(it.value.toString().trim())
        }
    }
    fun getTeamPassword(teamKey: String, unit: (password: String) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "password") {
            unit(it.value.toString())
        }
    }

    fun getTeamMembers(teamKey: String, unit: (memberList: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("Teams", teamKey, "members") {snapshot ->
            val teamMemberList = ArrayList<String>()
            val memberList = ArrayList<String>()
            for (member in snapshot.children) {
                memberList.add(member.value.toString())
            }
            for (member in memberList) {
                teamMemberList.add(member)
                if (!ImageUriListsObject.profilePicsList.contains(member)) {
                    getUserProfilePic(member) {
                        ImageUriListsObject.setProfilePicImageUriHashMap(member, it)
                        if (member == memberList[memberList.size - 1]) {
                            unit(teamMemberList)
                        }
                    }
                }else if (member == memberList[memberList.size - 1]) {
                    unit(teamMemberList)
                }
            }
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
        if (message.trim() != "") {
            val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
            val key = Global.getKey()
            val hm = HashMap<String, Any?>()
            hm[key] = MessageClass(message, Global.username, messageTime, key)
            reference.child("Chats").child(getMessagePath(username)).updateChildren(hm)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageToTeam(teamKey: String, message: String) {
        if (message.trim() != "") {
            val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
            val key = Global.getKey()
            val hm = HashMap<String, Any?>()
            hm[key] = MessageClass(message, Global.username, messageTime, key)
            reference.child("Teams").child(teamKey).child("Chat").updateChildren(hm)
        }
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
        hm[key] = MeetUp(meetUp.startDate, meetUp.endDate, meetUp.location, meetUp.latitude, meetUp.longitude, meetUp.description, key, meetUp.teamKey)
        reference.child("Teams").child(meetUp.key).child("meetUps").updateChildren(hm)
        getTeamMembers(meetUp.key) { memberList ->
            for (member in memberList) {
                reference.child("User").child(member).child("meetUps").updateChildren(hm)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamMeetUps(teamKey: String, unit: (meetUpsList: ArrayList<MeetUp>) -> Unit) {
        getDataFromDatabase("Teams", teamKey, "meetUps") {snapshot ->
            val meetUpsList = ArrayList<MeetUp>()
            for (data in snapshot.children) {
                val startDate = data.child("startDate").value.toString()
                val endDate = data.child("endDate").value.toString()
                val location = data.child("location").value.toString()
                val latitude = data.child("latitude").value.toString().toDouble()
                val longitude = data.child("longitude").value.toString().toDouble()
                val description = data.child("description").value.toString()
                val key = data.key.toString()
                if (!Global.checkIfDateIsExpired(Global.getDateFromString(endDate.trim(), Global.basicFormat))) {
                    meetUpsList.add(MeetUp(startDate, endDate, location, latitude, longitude, description, key, teamKey))
                }else deleteTeamMeetUp(teamKey, key)
            }
            unit(meetUpsList)
        }
    }

    fun deleteTeamMeetUp(teamKey: String, key: String) {
        reference.child("Teams").child(teamKey).child("meetUps").child(key).removeValue()
        getTeamMembers(teamKey) {
            for (member in it) {
                deleteMeetUp(member, key)
            }
        }
    }

    fun deleteTeam(teamKey: String) {
        getTeamMembers(teamKey) {
            val memberList = it
            for (member in memberList) {
                reference.child("User").child(member).child("Teams").child(teamKey).removeValue()
            }
            reference.child("Teams").child(teamKey).removeValue()
        }
    }

    fun leaveTeam(teamKey: String) {
        getTeamAdmin(teamKey) {admin ->
            getTeamMembers(teamKey) {
                val memberList = it
                memberList.remove(Global.username)
                if (memberList.size != 0) {
                    if (admin == Global.username) {
                        val randomInt = memberList.indices.random()
                        reference.child("Teams").child(teamKey).child("admin")
                            .setValue(memberList[randomInt])
                    }
                    reference.child("Teams").child(teamKey).child("members").child(Global.username)
                        .removeValue()
                }else reference.child("Teams").child(teamKey).removeValue()
                reference.child("User").child(Global.username).child("Teams").child(teamKey).removeValue()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUserMeetUps(username: String, unit: (meetUpList: ArrayList<MeetUp>) -> Unit) {
        getDataFromDatabase("User", username, "meetUps") {snapshot ->
            val meetUpsList = ArrayList<MeetUp>()
            for (data in snapshot.children) {
                val startDate = data.child("startDate").value.toString()
                val endDate = data.child("endDate").value.toString()
                val location = data.child("location").value.toString()
                val latitude = data.child("latitude").value.toString().toDouble()
                val longitude = data.child("longitude").value.toString().toDouble()
                val description = data.child("description").value.toString()
                val teamKey = data.child("teamKey").value.toString()
                val key = data.key.toString()
                if (!Global.checkIfDateIsExpired(Global.getDateFromString(endDate.trim(), Global.basicFormat))) {
                    meetUpsList.add(MeetUp(startDate, endDate, location, latitude, longitude, description, key, teamKey))
                }else deleteMeetUp(username, key)
            }
            unit(meetUpsList)
        }
    }

    fun deleteMeetUp(username: String, meetUpKey: String) {
        reference.child("User").child(username).child("meetUps").child(meetUpKey).removeValue()
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
        Global.username = user.username
        Global.city = user.city
        Global.country = user.country
        Global.age = user.age
        reference.child("User").child(Global.email).child("description").setValue(user.description)
        reference.child("User").child(Global.email).child("username").setValue(user.username)
        reference.child("User").child(Global.email).child("age").setValue(user.age)
        reference.child("User").child(Global.email).child("country").setValue(user.country)
        reference.child("User").child(Global.email).child("city").setValue(user.city)
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

    fun getExplorePostsNoUri(onlySpots: Boolean, unit: (postList: ArrayList<UploadPostClass>) -> Unit) {
        getSingleDataFromDatabase("Posts") {snapshot ->
            val postList = ArrayList<UploadPostClass>()
            for (data in snapshot.children) {
                val postType : PostType = PostType.valueOf(data.child("postType").value.toString().trim())
                val username = data.child("username").value.toString()
                val country = data.child("country").value.toString()
                val key = data.child("key").value.toString()
                val city = data.child("city").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val spotType = SpotType.valueOf(data.child("spotType").value.toString())
                if (onlySpots) {
                    if (postType == PostType.PARKOUR_SPOT) {
                        postList.add(UploadPostClass(username, postType, key, country, city, location, description, spotType))
                    }
                }else postList.add(UploadPostClass(username, postType, key, country, city, location, description, spotType))
            }
            postList.shuffle()
            unit(postList)
        }
    }

    fun getExplorePosts(onlySpots: Boolean, countryFilter: String, cityFilter: String, unit: (postList: ArrayList<UploadPostClass>) -> Unit) {
        getExplorePostsNoUri(onlySpots) { postList ->
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
            if (filteredPostList.size == 0) {
                unit(filteredPostList)
            }
            if (filteredPostList.size <  15) {
                for (post in filteredPostList) {
                    if (!ImageUriListsObject.postsList.contains(post.key)) {
                        getImageUriFromUser(post.username, post.key) { uri ->
                            ImageUriListsObject.setPostImageUriHashMap(post.key, uri)
                            if (post.key == filteredPostList[filteredPostList.size - 1].key) {
                                unit(filteredPostList)
                            }
                        }
                    }else if (post.key == filteredPostList[filteredPostList.size - 1].key) {
                        unit(filteredPostList)
                    }
                }
            }else {
                for (position in 0..14) {
                    val post = filteredPostList[position]
                    if (!ImageUriListsObject.postsList.contains(post.key)) {
                        getImageUriFromUser(post.username, post.key) { uri ->
                            ImageUriListsObject.setPostImageUriHashMap(post.key, uri)
                            if (post.key == filteredPostList[filteredPostList.size - 1].key) {
                                val endFilteredList = ArrayList<UploadPostClass>()
                                for (uploadPostPosition in 0..15) {
                                    endFilteredList.add(filteredPostList[uploadPostPosition])
                                }
                                unit(endFilteredList)
                            }
                        }
                    }else if (post.key == filteredPostList[filteredPostList.size - 1].key) {
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

    fun getPostsFromUser(username: String, unit : (postList: ArrayList<PostClass>) -> Unit) {
        getDataFromDatabase("User", username, "Posts") {snapshot ->
            val list = ArrayList<PostClass>()
            var position = 0
            val keyList = ArrayList<String>()
            for (data in snapshot.children) {
                keyList.add(data.key.toString())
            }
            if (keyList.isEmpty()) unit(arrayListOf())
            for (data in snapshot.children) {
                val postType = PostType.valueOf(data.child("postType").value.toString())
                val country = data.child("country").value.toString()
                val city = data.child("city").value.toString()
                val location = data.child("location").value.toString()
                val description = data.child("description").value.toString()
                val spotType = SpotType.valueOf(data.child("spotType").value.toString())
                getPostLikeList(data.key.toString()) { likeList ->
                    if (!ImageUriListsObject.postsList.contains(data.key.toString())) {
                        getImageUriFromUser(username, data.key.toString()) {uri ->
                        ImageUriListsObject.setPostImageUriHashMap(data.key.toString(), uri)
                            list.add(PostClass(UploadPostClass(username, postType, data.key.toString(), country, city, location, description, spotType),likeList , null, null))
                            position ++
                            if (data.key.toString() == keyList[keyList.size - 1]) {
                                list.sort()
                                println("ListSIze1: " + list.size)
                                unit(list)
                            }
                        }
                    }else {
                        list.add(PostClass(UploadPostClass(username, postType, data.key.toString(), country, city, location, description, spotType), likeList, null, null))
                        position ++
                        if (data.key.toString() == keyList[keyList.size - 1]) {
                            list.sort()
                            println("ListSIze2: " + list.size)
                            unit(list)
                        }
                    }
                }
            }
        }
    }

    fun getImageUriFromUser(username: String, key: String, unit: (uri: Uri) -> Unit) {
        storageReference.child(username).child("Posts").child(key).downloadUrl.addOnSuccessListener {
            unit(it)
        }.addOnFailureListener {
            val uri = Uri.parse("not_found")
            unit(uri)
        }
    }

    fun getUserProfilePic(username: String, unit : (uri: Uri) -> Unit) {
        if (!ImageUriListsObject.profilePicsList.contains(username)) {
            storageReference.child(username).child("ProfilePic").downloadUrl.addOnSuccessListener {
                unit(it)
                ImageUriListsObject.setProfilePicImageUriHashMap(username, it)
            }.addOnFailureListener {
                val uri = Uri.parse("not_found")
                ImageUriListsObject.setProfilePicImageUriHashMap(username, uri)
                unit(uri)
            }
        }else if (username == Global.username) {
            storageReference.child(username).child("ProfilePic").downloadUrl.addOnSuccessListener {
                unit(it)
                ImageUriListsObject.setProfilePicImageUriHashMap(username, it)
            }.addOnFailureListener {
                val uri = Uri.parse("not_found")
                ImageUriListsObject.setProfilePicImageUriHashMap(username, uri)
                unit(uri)
            }
        }
    }

    fun getUserInfo(username: String, unit : (user: User) -> Unit) {
        getSingleDataFromDatabase("User", username) {
            val databaseUsername = it.child("username").value.toString()
            val country = it.child("country").value.toString()
            val city = it.child("city").value.toString()
            val description = it.child("description").value.toString()
            val age = it.child("age").value.toString().toInt()
            val email = it.child("email").value.toString()
            unit(User(databaseUsername, email, description, country, city, age))

        }
    }
    fun getEveryUser(filter: String, filterType: FilterType, unit: (userList: ArrayList<ExploreSearchClass>) -> Unit) {
        getSingleDataFromDatabase("User") {
            val userList = ArrayList<ExploreSearchClass>()
            for (data in it.children) {
                if (data.key.toString() != Global.username) {
                    if (filter == "") {
                        userList.add(ExploreSearchClass(data.key.toString(), getImportance(data)))
                    }else {
                        if (filterType == FilterType.COUNTRY) {
                            if (checkFilter(data.child("country").value, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data)))
                            }
                        }else if (filterType == FilterType.CITY) {
                            if (checkFilter(data.child("city").value, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data)))
                            }
                        }else if (filterType == FilterType.USERNAME) {
                            if (checkFilter(data.key, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data)))
                            }
                        }else if (filterType == FilterType.AGE) {
                            if (checkFilter(data.child("age").value, filter)) {
                                userList.add(ExploreSearchClass(data.key.toString(), getImportance(data)))
                            }
                        }
                    }
                }
            }
            userList.sort()
            if (userList.size != 0) {
                if (userList.size < 10) {
                    for (user in userList) {
                        if (!ImageUriListsObject.profilePicsList.contains(user.username)) {
                            getUserProfilePic(user.username) { uri ->
                                ImageUriListsObject.setProfilePicImageUriHashMap(user.username, uri)
                                if (user.username == userList[userList.size - 1].username) {
                                    unit(userList)
                                }
                            }
                        }else if (user.username == userList[userList.size - 1].username) unit(userList)
                    }
                } else {
                    val list = ArrayList<ExploreSearchClass>()
                    for (userPosition in 0..9) {
                        val user = userList[userPosition]
                        list.add(user)
                    }
                    for (user in list) {
                        if (!ImageUriListsObject.profilePicsList.contains(user.username)) {
                            getUserProfilePic(user.username) { uri ->
                                ImageUriListsObject.setProfilePicImageUriHashMap(user.username, uri)
                                if (user.username == list[list.size - 1].username) unit(list)
                            }
                        }else if (user.username == list[list.size - 1].username) unit(list)
                    }
                }
            }else unit(userList)
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
            if (friendsList.isEmpty()) {
                unit(postKeyList)
            }
            for (friend in friendsList) {
                val keyList = ArrayList<String>()
                for (data2 in snapshot.child(friend).child("Posts").children) {
                    keyList.add(data2.key.toString())
                }
                if (friend == friendsList[friendsList.size - 1]) {
                    if (keyList.isEmpty()) {
                        unit(postKeyList)
                    }
                }
                for (data2 in snapshot.child(friend).child("Posts").children) {
                    val postType = PostType.valueOf(data2.child("postType").value.toString())
                    val spotType = SpotType.valueOf(data2.child("spotType").value.toString())
                    val country = data2.child("country").value.toString()
                    val city = data2.child("city").value.toString()
                    val location = data2.child("location").value.toString()
                    val description = data2.child("description").value.toString()
                    getPostLikeList(data2.key.toString()) { likeList ->
                        postKeyList.add(PostClass(UploadPostClass(
                                    friend,
                                    postType,
                                    data2.key.toString(),
                                    country,
                                    city,
                                    location,
                                    description,
                                    spotType), likeList, null, null))
                        if (friend == friendsList[friendsList.size - 1]) {
                            if (keyList[keyList.size - 1] == data2.key.toString()) {
                                unit(postKeyList)
                            }
                        }
                    }
                }
            }
        }
    }
    fun deletePost(key: String) {
        reference.child("User").child(Global.username).child("Posts").child(key).removeValue()
        reference.child("Posts").child(key).removeValue()
        storageReference.child(Global.username).child("Posts").child(key).delete()
    }

    fun getFirst10PostsFromFriends(unit: (postKeyList: ArrayList<PostClass>) -> Unit) {
        getEveryPostFromFriendsNoUri {
            val postKeyList = it
            postKeyList.sort()
            if (postKeyList.size == 0) unit(postKeyList)
            if (postKeyList.size < 10) {
                for (post in postKeyList) {
                    if (!ImageUriListsObject.postsList.contains(post.uploadPostClass.key)) {
                        getImageUriFromUser(post.uploadPostClass.username, post.uploadPostClass.key) { uri ->
                            ImageUriListsObject.setPostImageUriHashMap(post.uploadPostClass.key, uri)
                            if (post.uploadPostClass.key == postKeyList[postKeyList.size - 1].uploadPostClass.key) {
                                unit(postKeyList)
                            }
                        }
                    }else if (post.uploadPostClass.key == postKeyList[postKeyList.size - 1].uploadPostClass.key) {
                        unit(postKeyList)
                    }
                }
            }else {
                for (position in 0..9) {
                    if (!ImageUriListsObject.postsList.contains(postKeyList[position].uploadPostClass.key)) {
                        getImageUriFromUser(postKeyList[position].uploadPostClass.username, postKeyList[position].uploadPostClass.key) { uri ->
                            ImageUriListsObject.setPostImageUriHashMap(postKeyList[position].uploadPostClass.key, uri)
                            if (postKeyList[position].uploadPostClass.key == postKeyList[9].uploadPostClass.key) {
                                unit(postKeyList)
                            }
                        }
                    }else if (postKeyList[position].uploadPostClass.key == postKeyList[9].uploadPostClass.key) {
                        unit(postKeyList)
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
    fun getUserPostKeys(username: String, unit: (keyList: ArrayList<String>) -> Unit) {
        val keyList = ArrayList<String>()
        getSingleDataFromDatabase("User", username, "Posts") {
            for (data in it.children) {
                keyList.add(data.key.toString())
            }
            unit(keyList)
        }
    }

    fun deleteAccount(unit: () -> Unit) {
        reference.child("Emails").child(Global.getKeyFromEmail(Global.email)).removeValue()
        reference.child("User").child(Global.username).removeValue()
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
        storageReference.child(Global.username).child("ProfilePic").delete()
        getUserPostKeys(Global.username) {
            for (key in it) {
                reference.child("Posts").child(key).removeValue()
                storageReference.child(Global.username).child("Posts").child(key).delete()
            }
            unit()
        }
    }

    /*
    Support
     */

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessageToSupport(message: String) {
        if (message.trim() != "") {
            val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
            val key = Global.getKey()
            val hm = HashMap<String, Any?>()
            hm[key] = MessageClass(message, Global.username, messageTime, key)
            reference.child("Support").child("Messages").child(Global.username).updateChildren(hm)
            reference.child("Support").child("New").child(Global.username).setValue(Global.username)
        }
    }

    fun getMessageSupportList(username: String, unit: (messageList: ArrayList<MessageClass>) -> Unit) {
        getDataFromDatabase("Support", "Messages", username) {snapshot ->
            val messageList = ArrayList<MessageClass>()
            for (data in snapshot.children) {
                val message = data.child("message").value.toString()
                val usernameDatabase = data.child("username").value.toString()
                val time = data.child("time").value.toString()
                val key = data.child("key").value.toString()
                messageList.add(MessageClass(message, usernameDatabase, time, key))
            }
            unit(messageList)
        }
    }

    fun getNewSupportMessages(unit: (userList: ArrayList<String>) -> Unit) {
        getSingleDataFromDatabase("Support", "New") {snapshot ->
            val userList = ArrayList<String>()
            for (data in snapshot.children) {
                userList.add(data.value.toString())
            }
            unit(userList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendSupportMessageToUser(message: String, username: String) {
        if (message.trim() != "") {
            val messageTime = Global.getStringFromDate(Global.getCurrentTime(), Global.basicFormat)
            val key = Global.getKey()
            val hm = HashMap<String, Any?>()
            hm[key] = MessageClass(message, "Support", messageTime, key)
            reference.child("Support").child("Messages").child(username).updateChildren(hm)
        }
    }

    fun uploadSpot(latitude: Double, longitude: Double, country: String, key: String, description: String, spotType: SpotType) {
        reference.child("Spots").child(country).child(key).child("latitude").setValue(latitude)
        reference.child("Spots").child(country).child(key).child("longitude").setValue(longitude)
        reference.child("Spots").child(country).child(key).child("description").setValue(description)
        reference.child("Spots").child(country).child(key).child("spotType").setValue(spotType)
    }
    fun getSpotsFromCountry(country: String, unit: (spotList: ArrayList<SpotDatabaseClass>) -> Unit) {
        getSingleDataFromDatabase("Spots", country) {
            val spotList = ArrayList<SpotDatabaseClass>()
            for (data in it.children) {
                val description = data.child("description").value.toString()
                val latitude = data.child("latitude").value.toString().toDouble()
                val longitude = data.child("longitude").value.toString().toDouble()
                val spotType = SpotType.valueOf(data.child("spotType").value.toString())
                val key = data.key.toString()
                spotList.add(SpotDatabaseClass(latitude, longitude, key, description, spotType))
            }
            unit(spotList)
        }
    }
    fun getPostFromKey(key: String, unit: (post: UploadPostClass) -> Unit) {
        getSingleDataFromDatabase("Posts", key) { data ->
            val username = data.child("username").value.toString()
            val postType = PostType.valueOf(data.child("postType").value.toString())
            val country = data.child("country").value.toString()
            val city = data.child("city").value.toString()
            val location = data.child("location").value.toString()
            val description = data.child("description").value.toString()
            val spotType = SpotType.valueOf(data.child("spotType").value.toString())
            if (!ImageUriListsObject.postsList.contains(key)) {
                getImageUriFromUser(username, key) {uri ->
                    ImageUriListsObject.setPostImageUriHashMap(key, uri)
                    unit(UploadPostClass(username, postType, key, country, city, location, description, spotType))
                }
            }else unit(UploadPostClass(username, postType, key, country, city, location, description, spotType))
        }
    }
    fun getLocationFromKey(country: String, key: String, unit: (latitude: Double, longitude: Double) -> Unit) {
        getSingleDataFromDatabase("Spots", country, key) {snapshot ->
            unit(snapshot.child("latitude").value.toString().toDouble(), snapshot.child("longitude").value.toString().toDouble())
        }
    }

    fun getTeamPic(key: String, unit: (uri : Uri) -> Unit) {
        if (ImageUriListsObject.getTeamPic(key) == null) {
            teamStorageReference.child(key).downloadUrl.addOnSuccessListener {
                ImageUriListsObject.setTeamPicImageUriHashMap(key, it)
                unit(it)
            }.addOnFailureListener {
                ImageUriListsObject.setTeamPicImageUriHashMap(key, Uri.parse("not_found"))
                unit(Uri.parse("not_found"))
            }
        }else unit(ImageUriListsObject.getTeamPic(key)!!)
    }
}