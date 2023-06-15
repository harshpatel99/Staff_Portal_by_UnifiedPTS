package com.unifiedpts.staffportal.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.unifiedpts.staffportal.GMailSender
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.fragment.SelectAuthTypeFragment
import com.unifiedpts.staffportal.fragment.WaitingForApprovalFragment
import com.unifiedpts.staffportal.model.Admin
import com.unifiedpts.staffportal.model.User
import java.util.Properties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class AuthenticationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_authentication)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val isUserSignedIn = FirebaseAuth.getInstance().currentUser != null
        if (isUserSignedIn) {

            Firebase.firestore.collection("users").document(FirebaseAuth.getInstance().uid!!)
                .get()
                .addOnSuccessListener { result ->
                    val user = result.toObject<User>()

                    if (user != null) {
                        val sp = getSharedPreferences("user", MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putString("user", Gson().toJson(user))
                        editor.apply()

                        if (user.verifiedUser != "true") {
                            openFragment(
                                this,
                                WaitingForApprovalFragment()
                            )
                        } else {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Error retrieving your information. Please login in again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                        openFragment(this, SelectAuthTypeFragment())
                    }
                }
        } else {
            openFragment(this, SelectAuthTypeFragment())
        }

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