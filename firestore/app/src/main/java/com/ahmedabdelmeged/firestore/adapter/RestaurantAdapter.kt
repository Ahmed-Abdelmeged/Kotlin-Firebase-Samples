package com.ahmedabdelmeged.firestore.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ahmedabdelmeged.firestore.R
import com.ahmedabdelmeged.firestore.model.Restaurant
import com.ahmedabdelmeged.firestore.util.RestaurantUtil
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_restaurant.view.*
import me.zhanghai.android.materialratingbar.MaterialRatingBar

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 *
 * RecyclerView adapter for a list of Restaurants.
 */
open class RestaurantAdapter(query: Query, private val mListener: OnRestaurantSelectedListener) :
        FirestoreAdapter<RestaurantAdapter.ViewHolder>(query) {

    interface OnRestaurantSelectedListener {
        fun onRestaurantSelected(restaurant: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), mListener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var imageView: ImageView = itemView.restaurant_item_image

        private var nameView: TextView = itemView.restaurant_item_name

        private var ratingBar: MaterialRatingBar = itemView.restaurant_item_rating

        private var numRatingsView: TextView = itemView.restaurant_item_num_ratings

        private var priceView: TextView = itemView.restaurant_item_price

        private var categoryView: TextView = itemView.restaurant_item_category

        private var cityView: TextView = itemView.restaurant_item_city

        fun bind(snapshot: DocumentSnapshot, listener: OnRestaurantSelectedListener) {
            val restaurant = snapshot.toObject<Restaurant>(Restaurant::class.java)
            val resources = itemView.resources

            // Load image
            Glide.with(imageView.context)
                    .load(restaurant.photo)
                    .into(imageView)

            nameView.text = restaurant.name
            ratingBar.rating = restaurant.avgRating.toFloat()
            cityView.text = restaurant.city
            categoryView.text = restaurant.category
            numRatingsView.text = resources.getString(R.string.fmt_num_ratings,
                    restaurant.numRatings)
            priceView.text = RestaurantUtil.getPriceString(restaurant)

            // Click listener
            itemView.setOnClickListener {
                listener.onRestaurantSelected(snapshot)
            }
        }
    }
}
