package com.ahmedabdelmeged.database.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.ahmedabdelmeged.database.model.Post
import kotlinx.android.synthetic.main.include_post_author.view.*
import kotlinx.android.synthetic.main.include_post_text.view.*
import kotlinx.android.synthetic.main.item_post.view.*

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */
class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleView = itemView.post_title
    private val authorView = itemView.post_author
    val starView: ImageView = itemView.star
    private val numStarsView = itemView.post_num_stars
    private val bodyView = itemView.post_body

    fun bindToPost(post: Post, starClickListener: View.OnClickListener) {
        titleView.text = post.title
        authorView.text = post.author
        numStarsView.text = post.starCount.toString()
        bodyView.text = post.body

        starView.setOnClickListener(starClickListener)
    }

}