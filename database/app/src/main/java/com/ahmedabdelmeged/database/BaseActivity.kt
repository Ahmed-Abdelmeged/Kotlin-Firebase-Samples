package com.ahmedabdelmeged.database

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by Ahmed Abd-Elmeged on 3/18/2018.
 */

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    private val progressDialog: ProgressDialog by lazy {
        val progress = ProgressDialog(this)
        progress.setCancelable(false)
        progress.setMessage("Loading...")
        progress
    }

    fun showProgressDialog() {
        progressDialog.show()
    }

    fun hideProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    val uid: String by lazy { FirebaseAuth.getInstance().currentUser!!.uid }

}