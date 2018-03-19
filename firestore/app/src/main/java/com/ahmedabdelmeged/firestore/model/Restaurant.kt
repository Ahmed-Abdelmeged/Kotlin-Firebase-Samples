package com.ahmedabdelmeged.firestore.model

import com.google.firebase.firestore.IgnoreExtraProperties

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
@IgnoreExtraProperties
data class Restaurant(var name: String = "",
                      var city: String = "",
                      var category: String = "",
                      var photo: String = "",
                      var price: Int = 0,
                      var numRatings: Int = 0,
                      var avgRating: Double = 0.0) {
    companion object {
        const val FIELD_CITY = "city"
        const val FIELD_CATEGORY = "category"
        const val FIELD_PRICE = "price"
        const val FIELD_POPULARITY = "numRatings"
        const val FIELD_AVG_RATING = "avgRating"
    }
}