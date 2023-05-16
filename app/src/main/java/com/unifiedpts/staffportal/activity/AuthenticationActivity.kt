package com.unifiedpts.staffportal.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.fragment.SelectAuthTypeFragment
import com.unifiedpts.staffportal.fragment.WaitingForApprovalFragment
import com.unifiedpts.staffportal.model.User


class AuthenticationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isUserSignedIn = FirebaseAuth.getInstance().currentUser != null
        if (isUserSignedIn) {
            Firebase.firestore.collection("users").document(FirebaseAuth.getInstance().uid!!)
                .get()
                .addOnSuccessListener { result ->
                    val user = result.toObject<User>()

                    val sp = getSharedPreferences("user", MODE_PRIVATE)
                    val editor = sp.edit()
                    editor.putString("user",Gson().toJson(user))
                    editor.apply()

                    if (user!!.verifiedUser != "true") {
                        openFragment(
                            this,
                            WaitingForApprovalFragment()
                        )
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
        } else {
            openFragment(this, SelectAuthTypeFragment())
        }

        setContentView(R.layout.activity_authentication)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            finish()
    }


}