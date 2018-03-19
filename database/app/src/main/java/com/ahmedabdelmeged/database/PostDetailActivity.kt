package com.ahmedabdelmeged.database

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ahmedabdelmeged.database.model.Comment

import com.ahmedabdelmeged.database.model.Post
import com.ahmedabdelmeged.database.model.User
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.include_post_author.*
import kotlinx.android.synthetic.main.include_post_text.*
import kotlinx.android.synthetic.main.item_comment.view.*

import kotlin.collections.ArrayList

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */
class PostDetailActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mPostReference: DatabaseReference
    private lateinit var mCommentsReference: DatabaseReference
    private var mPostListener: ValueEventListener? = null
    private var mPostKey: String? = null
    private lateinit var mAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        // Get post key from intent
        mPostKey = intent.getStringExtra(EXTRA_POST_KEY)
        if (mPostKey == null) {
            throw IllegalArgumentException("Must pass EXTRA_POST_KEY")
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().reference
                .child("posts").child(mPostKey!!)
        mCommentsReference = FirebaseDatabase.getInstance().reference
                .child("post-comments").child(mPostKey!!)

        button_post_comment.setOnClickListener(this)
        recycler_comments.layoutManager = LinearLayoutManager(this)
    }

    public override fun onStart() {
        super.onStart()
        // Add value event listener to the post
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val post = dataSnapshot.getValue(Post::class.java)
                // [START_EXCLUDE]
                post_author.text = post!!.author
                post_title.text = post.title
                post_body.text = post.body
                // [END_EXCLUDE]
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // [START_EXCLUDE]
                Toast.makeText(this@PostDetailActivity, "Failed to load post.",
                        Toast.LENGTH_SHORT).show()
                // [END_EXCLUDE]
            }
        }
        mPostReference.addValueEventListener(postListener)
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener

        // Listen for comments
        mAdapter = CommentAdapter(this, mCommentsReference)
        recycler_comments.adapter = mAdapter
    }

    public override fun onStop() {
        super.onStop()
        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener!!)
        }

        // Clean up comments listener
        mAdapter.cleanupListener()
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.button_post_comment) {
            postComment()
        }
    }

    private fun postComment() {
        val uid = uid
        FirebaseDatabase.getInstance().reference.child("users").child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get user information
                        val user = dataSnapshot.getValue(User::class.java)
                        val authorName = user!!.username

                        // Create new comment field_comment_text
                        val commentText = field_comment_text.text.toString()
                        val comment = Comment(uid, authorName, commentText)

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment)

                        // Clear the field
                        field_comment_text.text = null
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })
    }

    private class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var authorView: TextView = itemView.comment_author
        var bodyView: TextView = itemView.comment_body
    }

    private class CommentAdapter(private val mContext: Context, private val mDatabaseReference: DatabaseReference)
        : RecyclerView.Adapter<CommentViewHolder>() {
        private val mChildEventListener: ChildEventListener?

        private val mCommentIds = ArrayList<String>()
        private val mComments = ArrayList<Comment>()

        init {
            // Create child event listener
            // [START child_event_listener_recycler]
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.key)

                    // A new comment has been added, add it to the displayed list
                    val comment = dataSnapshot.getValue<Comment>(Comment::class.java)

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.key)
                    mComments.add(comment!!)
                    notifyItemInserted(mComments.size - 1)
                    // [END_EXCLUDE]
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.key)

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    val newComment = dataSnapshot.getValue<Comment>(Comment::class.java)
                    val commentKey = dataSnapshot.key

                    // [START_EXCLUDE]
                    val commentIndex = mCommentIds.indexOf(commentKey)
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments[commentIndex] = newComment!!

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex)
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:$commentKey")
                    }
                    // [END_EXCLUDE]
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.key)

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    val commentKey = dataSnapshot.key

                    // [START_EXCLUDE]
                    val commentIndex = mCommentIds.indexOf(commentKey)
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.removeAt(commentIndex)
                        mComments.removeAt(commentIndex)

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex)
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:$commentKey")
                    }
                    // [END_EXCLUDE]
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.key)

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    val movedComment = dataSnapshot.getValue<Comment>(Comment::class.java)
                    val commentKey = dataSnapshot.key

                    // ...
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException())
                    Toast.makeText(mContext, "Failed to load comments.", Toast.LENGTH_SHORT).show()

                }
            }
            mDatabaseReference.addChildEventListener(childEventListener)
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false))

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            val comment = mComments[position]
            holder.authorView.text = comment.author
            holder.bodyView.text = comment.text
        }

        override fun getItemCount() = mComments.size

        fun cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener)
            }
        }

    }

    companion object {
        private const val TAG = "PostDetailActivity"

        const val EXTRA_POST_KEY = "post_key"
    }

}
