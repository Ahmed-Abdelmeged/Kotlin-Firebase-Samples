package com.ahmedabdelmeged.firestore

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import com.ahmedabdelmeged.firestore.model.Restaurant
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog_filters.view.*

/**
 * Created by Ahmed Abd-Elmeged on 3/19/2018.
 *
 * Dialog Fragment containing filter form.
 */
class FilterDialogFragment : DialogFragment() {

    private lateinit var mRootView: View

    private lateinit var mCategorySpinner: Spinner

    private lateinit var mCitySpinner: Spinner

    private lateinit var mSortSpinner: Spinner

    private lateinit var mPriceSpinner: Spinner

    private var mFilterListener: FilterListener? = null

    private fun selectedCategory(): String? {
        val selected = mCategorySpinner.selectedItem as String
        return if (getString(R.string.value_any_category) == selected) {
            null
        } else {
            selected
        }
    }

    private fun selectedCity(): String? {
        val selected = mCitySpinner.selectedItem as String
        return if (getString(R.string.value_any_city) == selected) {
            null
        } else {
            selected
        }
    }

    private fun selectedPrice(): Int {
        val selected = mPriceSpinner.selectedItem as String
        return when (selected) {
            getString(R.string.price_1) -> 1
            getString(R.string.price_2) -> 2
            getString(R.string.price_3) -> 3
            else -> -1
        }
    }

    private fun selectedSortBy(): String? {
        val selected = mSortSpinner.selectedItem as String
        if (getString(R.string.sort_by_rating) == selected) {
            return Restaurant.FIELD_AVG_RATING
        }
        if (getString(R.string.sort_by_price) == selected) {
            return Restaurant.FIELD_PRICE
        }
        return if (getString(R.string.sort_by_popularity) == selected) {
            Restaurant.FIELD_POPULARITY
        } else null

    }

    private fun sortDirection(): Query.Direction? {
        val selected = mSortSpinner.selectedItem as String
        if (getString(R.string.sort_by_rating) == selected) {
            return Query.Direction.DESCENDING
        }
        if (getString(R.string.sort_by_price) == selected) {
            return Query.Direction.ASCENDING
        }
        return if (getString(R.string.sort_by_popularity) == selected) {
            Query.Direction.DESCENDING
        } else null

    }

    fun filters(): Filters {
        val filters = Filters()
        filters.category = selectedCategory()
        filters.city = selectedCity()
        filters.price = selectedPrice()
        filters.sortBy = selectedSortBy()
        filters.sortDirection = sortDirection()
        return filters
    }

    internal interface FilterListener {
        fun onFilter(filters: Filters)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false)
        mCategorySpinner = mRootView.spinner_category
        mCitySpinner = mRootView.spinner_city
        mSortSpinner = mRootView.spinner_sort
        mPriceSpinner = mRootView.spinner_price

        mRootView.button_cancel.setOnClickListener { dismiss() }
        mRootView.button_search.setOnClickListener {
            if (mFilterListener != null) {
                mFilterListener!!.onFilter(filters())
            }

            dismiss()
        }
        return mRootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FilterListener) {
            mFilterListener = context
        }
    }

    override fun onResume() {
        super.onResume()
        dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun resetFilters() {
        mCategorySpinner.setSelection(0)
        mCitySpinner.setSelection(0)
        mPriceSpinner.setSelection(0)
        mSortSpinner.setSelection(0)
    }

    companion object {
        const val TAG = "FilterDialog"
    }

}
