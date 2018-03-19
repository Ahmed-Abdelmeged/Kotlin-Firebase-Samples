package com.ahmedabdelmeged.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_anonymous_auth.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
/**
 * Activity to demonstrate anonymous login and account linking (with an email/password account).
 */
class AnonymousAuthActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "AnonymousAuth"

    // [START declare_auth]
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    // [END declare_auth]

    private lateinit var mEmailField: EditText
    private lateinit var mPasswordField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anonymous_auth)
        // Fields
        mEmailField = field_email
        mPasswordField = field_password

        // Click listeners
        button_anonymous_sign_in.setOnClickListener(this)
        button_anonymous_sign_out.setOnClickListener(this)
        button_link_account.setOnClickListener(this)
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    private fun signInAnonymously() {
        showProgressDialog()
        // [START signin_anonymously]
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInAnonymously:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(this@AnonymousAuthActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                })
        // [END signin_anonymously]
    }

    private fun signOut() {
        mAuth.signOut()
        updateUI(null)
    }

    private fun linkAccount() {
        // Make sure form is valid
        if (!validateLinkForm()) {
            return
        }

        // Get email and password from form
        val email = mEmailField.text.toString()
        val password = mPasswordField.text.toString()

        // Create EmailAuthCredential with email and password
        val credential = EmailAuthProvider.getCredential(email, password)

        // Link the anonymous user to the email credential
        showProgressDialog()

        // [START link_credential]
        mAuth.currentUser!!.linkWithCredential(credential)
                .addOnCompleteListener(this, { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "linkWithCredential:success")
                        val user = task.result.user
                        updateUI(user)
                    } else {
                        Log.w(TAG, "linkWithCredential:failure", task.exception)
                        Toast.makeText(this@AnonymousAuthActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                })
        // [END link_credential]
    }

    private fun validateLinkForm(): Boolean {
        var valid = true

        val email = mEmailField.text.toString()
        if (TextUtils.isEmpty(email)) {
            mEmailField.error = "Required."
            valid = false
        } else {
            mEmailField.error = null
        }

        val password = mPasswordField.text.toString()
        if (TextUtils.isEmpty(password)) {
            mPasswordField.error = "Required."
            valid = false
        } else {
            mPasswordField.error = null
        }

        return valid
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()

        val idView = anonymous_status_id
        val emailView = anonymous_status_email
        val isSignedIn = user != null

        // Status text
        if (isSignedIn) {
            idView.text = getString(R.string.id_fmt, user!!.uid)
            emailView.text = getString(R.string.email_fmt, user.email)
        } else {
            idView.setText(R.string.signed_out)
            emailView.text = null
        }

        // Button visibility
        button_anonymous_sign_in.isEnabled = !isSignedIn
        button_anonymous_sign_out.isEnabled = isSignedIn
        button_link_account.isEnabled = isSignedIn
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_anonymous_sign_in -> signInAnonymously()
            R.id.button_anonymous_sign_out -> signOut()
            R.id.button_link_account -> linkAccount()
        }
    }

}
