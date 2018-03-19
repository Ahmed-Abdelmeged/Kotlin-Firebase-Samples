package com.ahmedabdelmeged.database.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ahmedabdelmeged.database.PostDetailActivity
import com.ahmedabdelmeged.database.R
import com.ahmedabdelmeged.database.model.Post
import com.ahmedabdelmeged.database.viewholder.PostViewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_all_posts.*

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */
abstract class PostListFragment : Fragment() {

    private val database: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    private lateinit var adapter: FirebaseRecyclerAdapter<Post, PostViewHolder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_all_posts, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(requireActivity())
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        messages_list.layoutManager = layoutManager

        // Set up FirebaseRecyclerAdapter with the Query
        val postsQuery = getQuery(database)

        val options = FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(postsQuery, Post::class.java).build()

        adapter = object : FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false))

            override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
                val postRef = getRef(position)

                // Set click listener for the whole post view
                val postKey = postRef.key
                holder.itemView.setOnClickListener {
                    // Launch PostDetailActivity
                    val intent = Intent(activity, PostDetailActivity::class.java)
                    intent.putExtra(PostDetailActivity.EXTRA_POST_KEY, postKey)
                    startActivity(intent)
                }

                // Determine if the current user has liked this post and set UI accordingly
                if (model.stars.containsKey(uid)) {
                    holder.starView.setImageResource(R.drawable.ic_toggle_star_24)
                } else {
                    holder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24)
                }

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                holder.bindToPost(model, View.OnClickListener {
                    // Need to write to both places the post is stored
                    val globalPostRef = database.child("posts").child(postRef.key)
                    val userPostRef = database.child("user-posts").child(model.uid).child(postRef.key)

                    // Run two transactions
                    onStarClicked(globalPostRef)
                    onStarClicked(userPostRef)
                })
            }

        }
        messages_list.adapter = adapter
    }

    private fun onStarClicked(postRef: DatabaseReference) {
        postRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val p = mutableData.getValue(Post::class.java)
                        ?: return Transaction.success(mutableData)

                if (p.stars.containsKey(uid)) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1
                    p.stars.remove(uid)
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1
                    p.stars[uid] = true
                }

                // Set value and report transaction success
                mutableData.value = p
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError, b: Boolean,
                                    dataSnapshot: DataSnapshot) {
                // Transaction completed
                Log.d("PostListFragment", "postTransaction:onComplete:$databaseError")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    abstract fun getQuery(databaseReference: DatabaseReference): Query

    val uid: String? by lazy { FirebaseAuth.getInstance().currentUser!!.uid }

}