package com.ahmedabdelmeged.firestore.model

import android.text.TextUtils
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
data class Rating(var userId: String = "",
                  var userName: String? = "",
                  var rating: Double = 0.0,
                  var text: String = "",
                  @ServerTimestamp var timestamp: Date = Date()) {
    constructor(user: FirebaseUser, rating: Double, text: String) : this() {
        this.userId = user.uid
        this.userName = user.displayName
        if (TextUtils.isEmpty(this.userName)) {
            this.userName = user.email!!
        }
        this.rating = rating
        this.text = text
    }
}