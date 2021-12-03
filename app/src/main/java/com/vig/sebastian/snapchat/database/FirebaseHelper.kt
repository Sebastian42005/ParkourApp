package com.vig.sebastian.snapchat.database

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class FirebaseHelper private constructor() {

    companion object {
        private val firebaseHelper = FirebaseHelper()
        private var reference: DatabaseReference? = null

        fun getInstance(reference: DatabaseReference): FirebaseHelper {
            this.reference = reference
            return firebaseHelper
        }
    }

    private fun getPath(paths: Array<out String>): String {
        var result = ""
        for (current in paths) {
            result += "$current/"
        }
        return result
    }

    private fun referenceNotNull(): DatabaseReference {
        if(reference != null) {
            return reference!!
        }
        throw FirebaseHelperNoReferenceException()

    }

    fun singleRequest(vararg paths: String, result: (snapshot: DataSnapshot?, error: FirebaseHelperError?) -> Unit) {
        referenceNotNull().child(getPath(paths)).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                result(null, FirebaseHelperError.ERROR_OCCURRED)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                result(snapshot, null)
            }

        })
    }

    fun taskRequest(vararg paths: String, result: (snapshot: DataSnapshot?, error: FirebaseHelperError?) -> Unit) {
        referenceNotNull().child(getPath(paths)).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                result(null, FirebaseHelperError.ERROR_OCCURRED)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                result(snapshot, null)
            }
        })
    }

    fun <T: Any> transformToClassSingleRequest(obj: Class<T>, vararg paths: String, result: (obj: T?, error: FirebaseHelperError?) -> Unit) {
        referenceNotNull().child(getPath(paths)).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                result(null, FirebaseHelperError.ERROR_OCCURRED)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(obj)
                result(value, null)
            }

        })
    }

    fun <T: Any> transformToClassTaskRequest(obj: Class<T>, vararg paths: String, result: (obj: T?, error: FirebaseHelperError?) -> Unit) {
        referenceNotNull().child(getPath(paths)).addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                result(null, FirebaseHelperError.ERROR_OCCURRED)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(obj)
                result(value, null)
            }

        })
    }

    fun <T: Any> transformToClass(obj: Class<T>, snapshot: DataSnapshot): T? {
        return snapshot.getValue(obj)
    }

    fun update(type: String, value: Any, vararg paths: String) {
        referenceNotNull().child(getPath(paths)).child(type).setValue(value)
    }

    fun send(key: String, value: Any, vararg paths: String) {
        val hm = hashMapOf<String, Any?>()
        hm[key] = value
        referenceNotNull().child(getPath(paths)).updateChildren(hm)

    }

    fun remove(vararg paths: String) {
        referenceNotNull().child(getPath(paths)).removeValue()
    }
}

class FirebaseHelperNoReferenceException() : Exception(FirebaseHelperError.NO_REFERENCE_SET.message)

enum class FirebaseHelperError(val message: String) {
    NO_REFERENCE_SET("The reference for the firebase is missing!"),
    ERROR_OCCURRED("An error occurred by request!")
}

