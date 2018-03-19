package com.ahmedabdelmeged.auth

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_emailpassword.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
class EmailPasswordActivity : BaseActivity(), View.OnClickListener {

    private val TAG = "EmailPassword"

    private lateinit var mStatusTextView: TextView
    private lateinit var mDetailTextView: TextView
    private lateinit var mEmailField: EditText
    private lateinit var mPasswordField: EditText

    // [START declare_auth]
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    // [END declare_auth]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emailpassword)

        // Views
        mStatusTextView = status
        mDetailTextView = detail
        mEmailField = field_email
        mPasswordField = field_password

        // Buttons
        email_sign_in_button.setOnClickListener(this)
        email_create_account_button.setOnClickListener(this)
        sign_out_button.setOnClickListener(this)
        verify_email_button.setOnClickListener(this)
    }

    // [START on_start_check_user]
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }
    // [END on_start_check_user]

    private fun createAccount(email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@EmailPasswordActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    // [START_EXCLUDE]
                    hideProgressDialog()
                    // [END_EXCLUDE]
                }
        // [END create_user_with_email]
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        showProgressDialog()

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@EmailPasswordActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    // [START_EXCLUDE]
                    if (!task.isSuccessful) {
                        mStatusTextView.setText(R.string.auth_failed)
                    }
                    hideProgressDialog()
                    // [END_EXCLUDE]
                }
        // [END sign_in_with_email]
    }

    private fun signOut() {
        mAuth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        // Disable button
        verify_email_button.isEnabled = false

        // Send verification email
        // [START send_email_verification]
        val user = mAuth.currentUser
        user!!.sendEmailVerification()
                .addOnCompleteListener(this) { task ->
                    // [START_EXCLUDE]
                    // Re-enable button
                    verify_email_button.isEnabled = true

                    if (task.isSuccessful) {
                        Toast.makeText(this@EmailPasswordActivity,
                                "Verification email sent to " + user.email!!,
                                Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e(TAG, "sendEmailVerification", task.exception)
                        Toast.makeText(this@EmailPasswordActivity,
                                "Failed to send verification email.",
                                Toast.LENGTH_SHORT).show()
                    }
                    // [END_EXCLUDE]
                }
        // [END send_email_verification]
    }

    private fun validateForm(): Boolean {
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
        if (user != null) {
            mStatusTextView.text = getString(R.string.emailpassword_status_fmt,
                    user.email, user.isEmailVerified)
            mDetailTextView.text = getString(R.string.firebase_status_fmt, user.uid)

            email_password_buttons.visibility = View.GONE
            email_password_fields.visibility = View.GONE
            signed_in_buttons.visibility = View.VISIBLE

            verify_email_button.isEnabled = !user.isEmailVerified
        } else {
            mStatusTextView.setText(R.string.signed_out)
            mDetailTextView.text = null

            email_password_buttons.visibility = View.VISIBLE
            email_password_fields.visibility = View.VISIBLE
            signed_in_buttons.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.email_create_account_button -> createAccount(mEmailField.text.toString(), mPasswordField.text.toString())
            R.id.email_sign_in_button -> signIn(mEmailField.text.toString(), mPasswordField.text.toString())
            R.id.sign_out_button -> signOut()
            R.id.verify_email_button -> sendEmailVerification()
        }
    }

}
