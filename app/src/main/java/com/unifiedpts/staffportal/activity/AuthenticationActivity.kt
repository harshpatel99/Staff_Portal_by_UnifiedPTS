package com.unifiedpts.staffportal.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.unifiedpts.staffportal.fragment.SelectAuthTypeFragment
import com.unifiedpts.staffportal.R


class AuthenticationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        //var fragmentContainerView = findViewById<FragmentContainerView>(R.id.fragmentContainerViewAuth)

        openFragment(this,SelectAuthTypeFragment());


    }

    companion object {
        fun openFragment(activity: FragmentActivity, fragment: Fragment) {
            val fragmentManager: FragmentManager = activity.supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainerViewAuth, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

        fun closeFragment(activity: FragmentActivity) {
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }

}