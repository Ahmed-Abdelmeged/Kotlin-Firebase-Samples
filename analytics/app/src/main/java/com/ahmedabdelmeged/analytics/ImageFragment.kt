package com.ahmedabdelmeged.analytics

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 *
 * This fragment displays a featured, specified image.
 */
class ImageFragment : Fragment() {

    private var resId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            resId = arguments!!.getInt(ARG_PATTERN)
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, null)
        view.imageView.setImageResource(resId)
        return view
    }

    companion object {
        private const val ARG_PATTERN = "pattern"

        /**
         * Create a [ImageFragment] displaying the given image.
         *
         * @param resId to display as the featured image
         * @return a new instance of [ImageFragment]
         */
        fun newInstance(resId: Int): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putInt(ARG_PATTERN, resId)
            fragment.arguments = args
            return fragment
        }
    }

}
