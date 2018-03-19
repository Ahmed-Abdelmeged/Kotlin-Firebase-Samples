package com.ahmedabdelmeged.database.model

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */

@IgnoreExtraProperties
data class Post(val uid: String = "",
                val author: String = "",
                val title: String = "",
                val body: String = "",
                var starCount: Int = 0,
                val stars: HashMap<String?, Boolean> = HashMap()) {
    @Exclude
    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result["uid"] = uid
        result["author"] = author
        result["title"] = title
        result["body"] = body
        result["starCount"] = starCount
        result["stars"] = stars
        return result
    }
}