package com.ahmedabdelmeged.firestore.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ahmedabdelmeged.firestore.R
import com.ahmedabdelmeged.firestore.model.Rating
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_rating.view.*
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 *
 * RecyclerView adapter for a list of [Rating].
 */
open class RatingAdapter(query: Query) : FirestoreAdapter<RatingAdapter.ViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rating, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position).toObject(Rating::class.java))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var nameView: TextView = itemView.rating_item_name

        private var ratingBar: MaterialRatingBar = itemView.rating_item_rating

        private var textView: TextView = itemView.rating_item_text

        private var dateView: TextView = itemView.rating_item_date

        fun bind(rating: Rating) {
            nameView.text = rating.text
            ratingBar.rating = rating.rating.toFloat()
            textView.text = rating.text
            dateView.text = FORMAT.format(rating.timestamp)
        }

        companion object {
            private val FORMAT = SimpleDateFormat(
                    "MM/dd/yyyy", Locale.US)
        }
    }

}
