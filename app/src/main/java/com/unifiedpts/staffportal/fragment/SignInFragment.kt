package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.activity.AuthenticationActivity
import com.unifiedpts.staffportal.model.User
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var verificationId: String? = null
    private lateinit var user: User

    private lateinit var otpET: TextInputEditText
    private lateinit var otpInputLayout: TextInputLayout
    private lateinit var progressBar: ProgressBar

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
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)

        val backButton = view.findViewById<ImageView>(R.id.signInBackImageView)
        val signInButton = view.findViewById<MaterialCardView>(R.id.signInButtonCardView)
        val signUpTextView = view.findViewById<TextView>(R.id.signInSignUpTexView)

        val phoneNumberET =
            view.findViewById<TextInputEditText>(R.id.signInPhoneNumberTextInputEditText)
        otpET = view.findViewById(R.id.signInOTPTextInputEditText)
        otpInputLayout = view.findViewById(R.id.signInOTPTextInputLayout)
        progressBar = view.findViewById(R.id.signInProgressBar)


        backButton.setOnClickListener {
            AuthenticationActivity.closeFragment(requireActivity())
        }

        signInButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val phoneNumber = phoneNumberET.text.toString()
            val otp = otpET.text.toString()

            if (phoneNumber.isEmpty()) {
                phoneNumberET.error = "Required"
                Toast.makeText(
                    requireActivity(),
                    "Please enter a valid phone number.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else {

                /*val i = Intent(activity, MainActivity::class.java)
                startActivity(i)
                requireActivity().finish()*/



                if (otpInputLayout.visibility == View.GONE) {
                    otpInputLayout.visibility = View.VISIBLE
                    val phone = "+91$phoneNumber"

                    Firebase.firestore.collection("users").whereEqualTo("phoneNumber", phone).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val listUsers = it.result.toObjects(User::class.java)
                                if (listUsers.isEmpty()) {
                                    checkIfFragmentAttached {
                                        Toast.makeText(
                                            requireActivity(),
                                            "New User? Please sign up first",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    progressBar.visibility = View.GONE
                                    otpInputLayout.visibility = View.GONE
                                } else {
                                    user = listUsers.first()
                                    sendVerificationCode(phone)
                                }

                            } else {
                                checkIfFragmentAttached {
                                    Toast.makeText(
                                        requireActivity(),
                                        "New User? Please sign up first",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                progressBar.visibility = View.GONE
                                otpInputLayout.visibility = View.GONE
                            }
                        }


                } else if (otp.isEmpty()) {
                    otpET.error = "Required"
                    checkIfFragmentAttached {
                        Toast.makeText(
                            requireActivity(),
                            "Please enter OTP.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    progressBar.visibility = View.GONE
                } else if (otp.length < 6) {
                    otpET.error = "Invalid OTP!"
                    progressBar.visibility = View.GONE
                } else if (!isVerificationCodeSent) {
                    checkIfFragmentAttached {
                        Toast.makeText(
                            requireActivity(),
                            "Please Wait! Waiting for OTP to arrive.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    verifyCode(otp)
                }
            }
        }

        signUpTextView.setOnClickListener {
            AuthenticationActivity.openFragment(requireActivity(), SignupFragment())
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignInFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignInFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private val verificationCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
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
                otpInputLayout.visibility = View.GONE
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    progressBar.visibility = View.GONE

                    if (user.verifiedUser!!.compareTo("true") == 0) {
                        val sp =
                            requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putString("userEmployeeID", user.empID)
                        editor.putString("phoneNumber", user.phoneNumber)
                        editor.putString("user", Gson().toJson(user))
                        editor.apply()

                        val i = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(i)
                        requireActivity().finish()
                    } else {

                        val sp =
                            requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sp.edit()
                        editor.putString("userEmployeeID", user.empID)
                        editor.putString("phoneNumber", user.phoneNumber)
                        editor.putString("user", Gson().toJson(user))
                        editor.apply()

                        AuthenticationActivity.openFragment(
                            requireActivity(),
                            WaitingForApprovalFragment()
                        )
                    }


                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireActivity(), task.exception!!.message, Toast.LENGTH_LONG)
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

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

}