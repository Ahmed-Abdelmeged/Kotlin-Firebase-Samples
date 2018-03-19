package com.ahmedabdelmeged.firestore

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.ahmedabdelmeged.firestore.model.Rating
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_rating.view.*
import me.zhanghai.android.materialratingbar.MaterialRatingBar

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 *
 * Dialog Fragment containing rating form.
 */
class RatingDialogFragment : DialogFragment() {

    private lateinit var mRatingBar: MaterialRatingBar

    private lateinit var mRatingText: EditText

    private var mRatingListener: RatingListener? = null

    internal interface RatingListener {
        fun onRating(rating: Rating)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_rating, container, false)
        mRatingBar = v.restaurant_form_rating
        mRatingText = v.restaurant_form_text

        v.restaurant_form_cancel.setOnClickListener { dismiss() }
        v.restaurant_form_button.setOnClickListener {
            val rating = Rating(
                    FirebaseAuth.getInstance().currentUser!!,
                    mRatingBar.rating.toDouble(),
                    mRatingText.text.toString())

            if (mRatingListener != null) {
                mRatingListener!!.onRating(rating)
            }

            dismiss()
        }
        return v
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is RatingListener) {
            mRatingListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        const val TAG = "RatingDialog"
    }

}
