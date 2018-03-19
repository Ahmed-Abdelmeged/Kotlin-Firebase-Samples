package com.ahmedabdelmeged.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.TwitterAuthProvider
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_twitter.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
class TwitterLoginActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "TwitterLogin"

    private lateinit var mStatusTextView: TextView
    private lateinit var mDetailTextView: TextView

    // [START declare_auth]
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    // [END declare_auth]

    private lateinit var mLoginButton: TwitterLoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Twitter SDK
        val authConfig = TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret))

        val twitterConfig = TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build()

        Twitter.initialize(twitterConfig)

        // Inflate layout (must be done after Twitter is configured)
        setContentView(R.layout.activity_twitter)

        // Views
        mStatusTextView = status
        mDetailTextView = detail
        button_twitter_signout.setOnClickListener(this)

        // [START initialize_twitter_login]
        mLoginButton = button_twitter_login
        mLoginButton.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d(TAG, "twitterLogin:success$result")
                handleTwitterSession(result.data)
            }

            override fun failure(exception: TwitterException) {
                Log.w(TAG, "twitterLogin:failure", exception)
                updateUI(null)
            }
        }
        // [END initialize_twitter_login]
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result to the Twitter login button.
        mLoginButton.onActivityResult(requestCode, resultCode, data)
    }
    // [END on_activity_result]

    // [START auth_with_twitter]
    private fun handleTwitterSession(session: TwitterSession) {
        Log.d(TAG, "handleTwitterSession:$session")
        // [START_EXCLUDE silent]
        showProgressDialog()
        // [END_EXCLUDE]

        val credential = TwitterAuthProvider.getCredential(
                session.authToken.token,
                session.authToken.secret)

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@TwitterLoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }

                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                }
    }
    // [END auth_with_twitter]

    private fun signOut() {
        mAuth.signOut()
        TwitterCore.getInstance().sessionManager.clearActiveSession()

        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            mStatusTextView.text = getString(R.string.twitter_status_fmt, user.displayName)
            mDetailTextView.text = getString(R.string.firebase_status_fmt, user.uid)

            button_twitter_login.visibility = View.GONE
            button_twitter_signout.visibility = View.VISIBLE
        } else {
            mStatusTextView.setText(R.string.signed_out)
            mDetailTextView.text = null

            button_twitter_login.visibility = View.VISIBLE
            button_twitter_signout.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_twitter_signout) {
            signOut()
        }
    }

}
