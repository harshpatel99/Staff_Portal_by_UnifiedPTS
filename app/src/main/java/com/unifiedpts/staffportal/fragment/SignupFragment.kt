package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.activity.AuthenticationActivity
import com.unifiedpts.staffportal.model.User
import java.util.concurrent.TimeUnit

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var verificationId: String? = null

    private lateinit var otpET: TextInputEditText
    private lateinit var progressBar: ProgressBar

    private lateinit var user: User

    private var isVerificationCodeSent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val backButton = view.findViewById<ImageView>(R.id.signUpBackImageView)
        val signUpButton = view.findViewById<MaterialCardView>(R.id.signUpButtonCardView)
        val signInTextView = view.findViewById<TextView>(R.id.signUpSigninTexView)

        progressBar = view.findViewById(R.id.signUpProgressBar)
        val phoneNumberET =
            view.findViewById<TextInputEditText>(R.id.signUpPhoneNumberTextInputEditText)
        val firstNameET =
            view.findViewById<TextInputEditText>(R.id.signUpFirstNameTextInputEditText)
        val lastNameET = view.findViewById<TextInputEditText>(R.id.signUpLastNameTextInputEditText)
        otpET = view.findViewById(R.id.signUpOTPTextInputEditText)
        val otpInputLayout = view.findViewById<TextInputLayout>(R.id.signUpOTPTextInputLayout)



        backButton.setOnClickListener {
            AuthenticationActivity.closeFragment(requireActivity())
        }

        signUpButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val phoneNumber = phoneNumberET.text.toString()
            val firstName = firstNameET.text.toString()
            val lastName = lastNameET.text.toString()
            val otp = otpET.text.toString()

            if (firstName.isEmpty()) {
                firstNameET.error = "Required"
                Toast.makeText(
                    activity,
                    "Please enter your first name.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else if (lastName.isEmpty()) {
                lastNameET.error = "Required"
                Toast.makeText(
                    activity,
                    "Please enter your last name.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else if (phoneNumber.isEmpty()) {
                phoneNumberET.error = "Required"
                Toast.makeText(
                    activity,
                    "Please enter a valid phone number.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else {

                if (otpInputLayout.visibility == View.GONE) {
                    otpInputLayout.visibility = View.VISIBLE
                    val phone = "+91$phoneNumber"
                    sendVerificationCode(phone)
                } else if (otp.isEmpty()) {
                    otpET.error = "Required"
                    Toast.makeText(
                        activity,
                        "Please enter OTP.",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                } else if (otp.length < 6) {
                    otpET.error = "Invalid OTP!"
                    progressBar.visibility = View.GONE
                } else if (!isVerificationCodeSent) {
                    Toast.makeText(
                        activity,
                        "Please Wait! Waiting for OTP to arrive.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    user = User(
                        "",
                        firstName,
                        lastName,
                        "+91$phoneNumber",
                        "",
                        "engineer",
                        "false",
                        System.currentTimeMillis(),
                        0.0, 0.0, 0.0, 0.0,
                        0, 0, 0, 0, ""
                    )
                    verifyCode(otp)
                }
            }

        }

        signInTextView.setOnClickListener {
            AuthenticationActivity.openFragment(requireActivity(), SignInFragment())
        }

        return view
    }

    private val verificationCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                isVerificationCodeSent = true
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                progressBar.visibility = View.GONE
                val code = phoneAuthCredential.smsCode

                if (code != null) {
                    otpET.setText(code)
                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    signInWithCredential(credential)
                }
            }

            // this method is called when firebase doesn't
            // sends our OTP code due to any error or issue.
            override fun onVerificationFailed(e: FirebaseException) {
                progressBar.visibility = View.GONE
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
                Log.w("AppError", e.message.toString())
            }
        }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    //Save user data and send for Verification

                    val db = Firebase.firestore

                    user.uid = auth.uid

                    db.collection("users").document(auth.uid!!).get().addOnCompleteListener {
                        if (it.isSuccessful) {

                            Toast.makeText(activity, "Welcome Back!", Toast.LENGTH_LONG).show()

                            if (user.verifiedUser!!.compareTo("true") == 0) {
                                val sp = requireActivity().getSharedPreferences(
                                    "user",
                                    Context.MODE_PRIVATE
                                )
                                val editor = sp.edit()
                                editor.putString("userEmployeeID", user.empID)
                                editor.putString("user", Gson().toJson(user))
                                editor.apply()

                                val i = Intent(activity, MainActivity::class.java)
                                startActivity(i)
                                requireActivity().finish()
                            } else {

                                val sp = requireActivity().getSharedPreferences(
                                    "user",
                                    Context.MODE_PRIVATE
                                )
                                val editor = sp.edit()
                                editor.putString("userEmployeeID", user.empID)
                                editor.putString("user", Gson().toJson(user))
                                editor.apply()

                                AuthenticationActivity.openFragment(
                                    requireActivity(),
                                    WaitingForApprovalFragment()
                                )
                            }

                            val i = Intent(activity, MainActivity::class.java)
                            startActivity(i)
                            requireActivity().finish()
                        } else {
                            db.collection("users").document(auth.uid!!)
                                .set(user)
                                .addOnSuccessListener {

                                    AuthenticationActivity.openFragment(
                                        requireActivity(),
                                        WaitingForApprovalFragment()
                                    )
                                }
                                .addOnFailureListener {
                                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                        }
                    }

                } else {
                    Toast.makeText(activity, task.exception!!.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
    }


    private fun sendVerificationCode(number: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(verificationCallBack)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        progressBar.visibility = View.GONE
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

}