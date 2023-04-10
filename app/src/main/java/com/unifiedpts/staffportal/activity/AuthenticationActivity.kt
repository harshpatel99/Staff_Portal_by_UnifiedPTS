package com.unifiedpts.staffportal.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.unifiedpts.staffportal.fragment.SelectAuthTypeFragment
import com.unifiedpts.staffportal.R


class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        var fragmentContainerView = findViewById<FragmentContainerView>(R.id.fragmentContainerViewAuth)

        openFragment(SelectAuthTypeFragment());


    }

    fun openFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(com.unifiedpts.staffportal.R.id.fragmentContainerViewAuth, fragment)
        fragmentTransaction.commit()
    }
}