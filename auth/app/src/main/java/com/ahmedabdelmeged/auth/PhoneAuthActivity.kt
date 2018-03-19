package com.ahmedabdelmeged.auth

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_phone_auth.*
import java.util.concurrent.TimeUnit

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 */
class PhoneAuthActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "PhoneAuthActivity"

    private val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"

    private val STATE_INITIALIZED = 1
    private val STATE_CODE_SENT = 2
    private val STATE_VERIFY_FAILED = 3
    private val STATE_VERIFY_SUCCESS = 4
    private val STATE_SIGNIN_FAILED = 5
    private val STATE_SIGNIN_SUCCESS = 6

    // [START declare_auth]
    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    // [END declare_auth]

    private var mVerificationInProgress = false
    private var mVerificationId: String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

    private lateinit var mPhoneNumberViews: ViewGroup
    private lateinit var mSignedInViews: ViewGroup

    private lateinit var mStatusText: TextView
    private lateinit var mDetailText: TextView

    private lateinit var mPhoneNumberField: EditText
    private lateinit var mVerificationField: EditText

    private lateinit var mStartButton: Button
    private lateinit var mVerifyButton: Button
    private lateinit var mResendButton: Button
    private lateinit var mSignOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

        // Assign views
        mPhoneNumberViews = phone_auth_fields
        mSignedInViews = signed_in_buttons

        mStatusText = status
        mDetailText = detail

        mPhoneNumberField = field_phone_number
        mVerificationField = field_verification_code

        mStartButton = button_start_verification
        mVerifyButton = button_verify_phone
        mResendButton = button_resend
        mSignOutButton = sign_out_button

        // Assign click listeners
        mStartButton.setOnClickListener(this)
        mVerifyButton.setOnClickListener(this)
        mResendButton.setOnClickListener(this)
        mSignOutButton.setOnClickListener(this)

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                // [START_EXCLUDE silent]
                mVerificationInProgress = false
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential)
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)
                // [START_EXCLUDE silent]
                mVerificationInProgress = false
                // [END_EXCLUDE]

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.error = "Invalid phone number."
                    // [END_EXCLUDE]
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show()
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED)
                // [END_EXCLUDE]
            }

            override fun onCodeSent(verificationId: String?,
                                    token: PhoneAuthProvider.ForceResendingToken?) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId!!)

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId
                mResendToken = token

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT)
                // [END_EXCLUDE]
            }
        }
        // [END phone_auth_callbacks]
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        updateUI(currentUser)

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.text.toString())
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }


    private fun startPhoneNumberVerification(phoneNumber: String) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks!!)        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        // [START verify_with_code]
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential)
    }

    // [START resend_verification]
    private fun resendVerificationCode(phoneNumber: String,
                                       token: PhoneAuthProvider.ForceResendingToken?) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks!!, // OnVerificationStateChangedCallbacks
                token)             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")

                        val user = task.result.user
                        // [START_EXCLUDE]
                        updateUI(STATE_SIGNIN_SUCCESS, user)
                        // [END_EXCLUDE]
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            // [START_EXCLUDE silent]
                            mVerificationField.error = "Invalid code."
                            // [END_EXCLUDE]
                        }
                        // [START_EXCLUDE silent]
                        // Update UI
                        updateUI(STATE_SIGNIN_FAILED)
                        // [END_EXCLUDE]
                    }
                }
    }
    // [END sign_in_with_phone]

    private fun signOut() {
        mAuth.signOut()
        updateUI(STATE_INITIALIZED)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(uiState: Int, cred: PhoneAuthCredential) {
        updateUI(uiState, null, cred)
    }

    private fun updateUI(uiState: Int, user: FirebaseUser? = mAuth.currentUser, cred: PhoneAuthCredential? = null) {
        when (uiState) {
            STATE_INITIALIZED -> {
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField)
                disableViews(mVerifyButton, mResendButton, mVerificationField)
                mDetailText.text = null
            }
            STATE_CODE_SENT -> {
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField)
                disableViews(mStartButton)
                mDetailText.setText(R.string.status_code_sent)
            }
            STATE_VERIFY_FAILED -> {
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField)
                mDetailText.setText(R.string.status_verification_failed)
            }
            STATE_VERIFY_SUCCESS -> {
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField)
                mDetailText.setText(R.string.status_verification_succeeded)

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.smsCode != null) {
                        mVerificationField.setText(cred.smsCode)
                    } else {
                        mVerificationField.setText(R.string.instant_validation)
                    }
                }
            }
            STATE_SIGNIN_FAILED ->
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed)
            STATE_SIGNIN_SUCCESS -> {
            }
        }// Np-op, handled by sign-in check

        if (user == null) {
            // Signed out
            mPhoneNumberViews.visibility = View.VISIBLE
            mSignedInViews.visibility = View.GONE

            mStatusText.setText(R.string.signed_out)
        } else {
            // Signed in
            mPhoneNumberViews.visibility = View.GONE
            mSignedInViews.visibility = View.VISIBLE

            enableViews(mPhoneNumberField, mVerificationField)
            mPhoneNumberField.text = null
            mVerificationField.text = null

            mStatusText.setText(R.string.signed_in)
            mDetailText.text = getString(R.string.firebase_status_fmt, user.uid)
        }
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = mPhoneNumberField.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.error = "Invalid phone number."
            return false
        }
        return true
    }

    private fun enableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = true
        }
    }

    private fun disableViews(vararg views: View) {
        for (v in views) {
            v.isEnabled = false
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_start_verification -> {
                if (!validatePhoneNumber()) {
                    return
                }
                startPhoneNumberVerification(mPhoneNumberField.text.toString())
            }
            R.id.button_verify_phone -> {
                val code = mVerificationField.text.toString()
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.error = "Cannot be empty."
                    return
                }

                verifyPhoneNumberWithCode(mVerificationId, code)
            }
            R.id.button_resend -> resendVerificationCode(mPhoneNumberField.text.toString(), mResendToken)
            R.id.sign_out_button -> signOut()
        }
    }

}
