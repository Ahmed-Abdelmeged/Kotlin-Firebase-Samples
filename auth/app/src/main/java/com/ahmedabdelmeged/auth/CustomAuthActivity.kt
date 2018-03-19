package com.ahmedabdelmeged.auth

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_custom.*

/**
 * Demonstrate Firebase Authentication using a custom minted token. For more information, see:
 * https://firebase.google.com/docs/auth/android/custom-auth
 */
class CustomAuthActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "CustomAuthActivity"

    // [START declare_auth]
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    // [END declare_auth]

    private var mCustomToken: String? = null
    private var mTokenReceiver: TokenBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        // Button click listeners
        button_sign_in.setOnClickListener(this)

        // Create token receiver (for demo purposes only)
        mTokenReceiver = object : TokenBroadcastReceiver() {
            override fun onNewToken(token: String?) {
                Log.d(TAG, "onNewToken:$token")
                setCustomToken(token)
            }
        }
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    override fun onResume() {
        super.onResume()
        registerReceiver(mTokenReceiver, TokenBroadcastReceiver.filter())
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mTokenReceiver)
    }

    private fun startSignIn() {
        // Initiate sign in with custom token
        // [START sign_in_custom]
        mAuth.signInWithCustomToken(mCustomToken!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCustomToken:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCustomToken:failure", task.exception)
                        Toast.makeText(this@CustomAuthActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        // [END sign_in_custom]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            text_sign_in_status.text = "User ID: " + user.uid
        } else {
            text_sign_in_status.text = "Error: sign in failed."
        }
    }

    private fun setCustomToken(token: String?) {
        mCustomToken = token

        val status: String = if (mCustomToken != null) {
            "Token:" + mCustomToken!!
        } else {
            "Token: null"
        }

        // Enable/disable sign-in button and show the token
        button_sign_in.isEnabled = mCustomToken != null
        text_token_status.text = status
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_sign_in) {
            startSignIn()
        }
    }

}
