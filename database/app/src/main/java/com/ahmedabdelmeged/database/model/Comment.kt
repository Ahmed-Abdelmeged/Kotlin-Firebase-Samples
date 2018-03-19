package com.ahmedabdelmeged.database.model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */

@IgnoreExtraProperties
data class Comment(val uid: String = "", val author: String = "", val text: String = "")