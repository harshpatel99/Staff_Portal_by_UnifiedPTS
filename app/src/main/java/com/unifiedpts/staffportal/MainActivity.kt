package com.unifiedpts.staffportal

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.unifiedpts.staffportal.fragment.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openFragment(this, HomeFragment())
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    companion object {
        fun openFragment(activity: FragmentActivity, fragment: Fragment) {
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerMainActivity, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        fun closeFragment(activity: FragmentActivity) {
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }
}