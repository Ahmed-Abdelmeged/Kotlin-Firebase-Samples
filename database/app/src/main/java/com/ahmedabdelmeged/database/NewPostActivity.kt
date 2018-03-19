package com.ahmedabdelmeged.database

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.ahmedabdelmeged.database.model.Post
import com.ahmedabdelmeged.database.model.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_new_post.*
import java.util.HashMap

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */
class NewPostActivity : BaseActivity() {

    private val TAG = "NewPostActivity"
    private val REQUIRED = "Required"

    // [START declare_database_ref]
    private val mDatabase: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }
    // [END declare_database_ref]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)
        fab_submit_post.setOnClickListener { submitPost() }
    }

    private fun submitPost() {
        val title = field_title.text.toString()
        val body = field_body.text.toString()

        // Title is required
        if (TextUtils.isEmpty(title)) {
            field_title.error = REQUIRED
            return
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            field_body.error = REQUIRED
            return
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false)
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show()

        // [START single_value_read]
        val userId = uid
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get user value
                        val user = dataSnapshot.getValue<User>(User::class.java)

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User $userId is unexpectedly null")
                            Toast.makeText(this@NewPostActivity,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show()
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body)
                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true)
                        finish()
                        // [END_EXCLUDE]
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                        // [START_EXCLUDE]
                        setEditingEnabled(true)
                        // [END_EXCLUDE]
                    }
                })
        // [END single_value_read]
    }

    private fun setEditingEnabled(enabled: Boolean) {
        field_title.isEnabled = enabled
        field_body.isEnabled = enabled
        fab_submit_post.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    // [START write_fan_out]
    private fun writeNewPost(userId: String, username: String, title: String, body: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = mDatabase.child("posts").push().key
        val post = Post(userId, username, title, body)
        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/posts/$key"] = postValues
        childUpdates["/user-posts/$userId/$key"] = postValues

        mDatabase.updateChildren(childUpdates)
    }
    // [END write_fan_out]

}
