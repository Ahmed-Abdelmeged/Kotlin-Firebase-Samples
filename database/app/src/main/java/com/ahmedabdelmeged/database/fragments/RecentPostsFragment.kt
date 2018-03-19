package com.ahmedabdelmeged.database.fragments

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */
class RecentPostsFragment : PostListFragment() {

    override fun getQuery(databaseReference: DatabaseReference): Query = databaseReference.child("posts").limitToFirst(100)

}