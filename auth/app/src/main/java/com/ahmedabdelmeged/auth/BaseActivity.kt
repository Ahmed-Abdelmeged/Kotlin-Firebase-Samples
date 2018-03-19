package com.ahmedabdelmeged.auth

import android.app.ProgressDialog
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
abstract class BaseActivity : AppCompatActivity() {

    @VisibleForTesting
    private var mProgressDialog: ProgressDialog? = null

    fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    public override fun onStop() {
        super.onStop()
        hideProgressDialog()
    }

}
