package com.ahmedabdelmeged.database

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.Menu
import android.view.MenuItem
import com.ahmedabdelmeged.database.fragments.MyPostsFragment
import com.ahmedabdelmeged.database.fragments.MyTopPostsFragment
import com.ahmedabdelmeged.database.fragments.RecentPostsFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var mPagerAdapter: FragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Create the adapter that will return a fragment for each section
        mPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
            private val mFragments = arrayOf<Fragment>(RecentPostsFragment(), MyPostsFragment(), MyTopPostsFragment())
            private val mFragmentNames = arrayOf(getString(R.string.heading_recent), getString(R.string.heading_my_posts), getString(R.string.heading_my_top_posts))
            override fun getItem(position: Int): Fragment {
                return mFragments[position]
            }

            override fun getCount(): Int {
                return mFragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return mFragmentNames[position]
            }
        }
        // Set up the ViewPager with the sections adapter.
        container.adapter = mPagerAdapter
        tabs.setupWithViewPager(container)

        // Button launches NewPostActivity
        fab_new_post.setOnClickListener({ startActivity(Intent(this@MainActivity, NewPostActivity::class.java)) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        return if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
