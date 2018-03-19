package com.ahmedabdelmeged.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_firebase_ui.*
import java.util.*

/**
 * Demonstrate authentication using the FirebaseUI-Android library. This activity demonstrates
 * using FirebaseUI for basic email/password sign in.
 *
 * For more information, visit https://github.com/firebase/firebaseui-android
 */
class FirebaseUIActivity : AppCompatActivity(), View.OnClickListener {

    private val RC_SIGN_IN = 9001

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var mStatusView: TextView
    private lateinit var mDetailView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_ui)
        mStatusView = status
        mDetailView = detail

        sign_in_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        updateUI(mAuth.currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                // Sign in succeeded
                updateUI(mAuth.currentUser)
            } else {
                // Sign in failed
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    private fun startSignIn() {
        // Build FirebaseUI sign in intent. For documentation on this operation and all
        // possible customization see: https://github.com/firebase/firebaseui-android
        val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                .setAvailableProviders(Arrays.asList<AuthUI.IdpConfig>(
                        AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                .setLogo(R.mipmap.ic_launcher)
                .build()

        startActivityForResult(intent, RC_SIGN_IN)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // Signed in
            mStatusView.text = getString(R.string.firebaseui_status_fmt, user.email)
            mDetailView.text = getString(R.string.id_fmt, user.uid)

            sign_in_button.visibility = View.GONE
            sign_out_button.visibility = View.VISIBLE
        } else {
            // Signed out
            mStatusView.setText(R.string.signed_out)
            mDetailView.text = null

            sign_in_button.visibility = View.VISIBLE
            sign_out_button.visibility = View.GONE
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        updateUI(null)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.sign_in_button -> startSignIn()
            R.id.sign_out_button -> signOut()
        }
    }

}