package com.ahmedabdelmeged.firestore.viewmodel

import android.arch.lifecycle.ViewModel
import com.ahmedabdelmeged.firestore.Filters

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
class MainActivityViewModel : ViewModel() {

    var isSigningIn: Boolean = false
    var filters: Filters

    init {
        isSigningIn = false
        filters = Filters.default()
    }

}