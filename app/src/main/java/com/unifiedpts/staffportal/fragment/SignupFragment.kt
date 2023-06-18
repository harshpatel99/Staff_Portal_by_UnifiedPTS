package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.GMailSender
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.activity.AuthenticationActivity
import com.unifiedpts.staffportal.model.Admin
import com.unifiedpts.staffportal.model.User
import com.unifiedpts.staffportal.model.WorkersAttendance
import java.util.Properties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


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
                    requireActivity(),
                    "Please enter your first name.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else if (lastName.isEmpty()) {
                lastNameET.error = "Required"
                Toast.makeText(
                    requireActivity(),
                    "Please enter your last name.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else if (phoneNumber.isEmpty()) {
                phoneNumberET.error = "Required"
                Toast.makeText(
                    requireActivity(),
                    "Please enter a valid phone number.",
                    Toast.LENGTH_SHORT
                ).show()
                progressBar.visibility = View.GONE
            } else {

                if (otpInputLayout.visibility == View.GONE) {
                    otpInputLayout.visibility = View.VISIBLE
                    val phone = "+91$phoneNumber"
                    user = User(
                        "",
                        firstName,
                        lastName,
                        "+91$phoneNumber",
                        "NewID" + (0..1000).random(),
                        "engineer",
                        "false",
                        System.currentTimeMillis(),
                        0.0, 0.0, 0.0, 0.0,
                        0, 0, 0, 0,
                        "", "", "", "", System.currentTimeMillis()
                    )
                    sendVerificationCode(phone)
                } else if (otp.isEmpty()) {
                    otpET.error = "Required"
                    Toast.makeText(
                        requireActivity(),
                        "Please enter OTP.",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                } else if (otp.length < 6) {
                    otpET.error = "Invalid OTP!"
                    progressBar.visibility = View.GONE
                } else if (!isVerificationCodeSent) {
                    Toast.makeText(
                        requireActivity(),
                        "Please Wait! Waiting for OTP to arrive.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    user = User(
                        "",
                        firstName,
                        lastName,
                        "+91$phoneNumber",
                        "NewID" + (0..1000).random(),
                        "engineer",
                        "false",
                        System.currentTimeMillis(),
                        0.0, 0.0, 0.0, 0.0,
                        0, 0, 0, 0,
                        "", "", "", "", System.currentTimeMillis()
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
                Toast.makeText(requireActivity(), e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    //Save user data and send for Verification

                    val db = Firebase.firestore

                    val uid = FirebaseAuth.getInstance().uid

                    user.uid = uid

                    db.collection("users").document(uid!!).get()
                        .addOnCompleteListener { userTask ->
                            if (userTask.isSuccessful) {
                                if (userTask.result.exists()) {
                                    Toast.makeText(
                                        requireActivity(),
                                        "Welcome Back!",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()

                                    if (user.verifiedUser!!.compareTo("true") == 0) {
                                        val sp = requireActivity().getSharedPreferences(
                                            "user",
                                            Context.MODE_PRIVATE
                                        )
                                        val editor = sp.edit()
                                        editor.putString("userEmployeeID", user.empID)
                                        editor.putString("user", Gson().toJson(user))
                                        editor.apply()

                                        val i = Intent(requireActivity(), MainActivity::class.java)
                                        startActivity(i)
                                        requireActivity().finish()

                                    }

                                    AuthenticationActivity.openFragment(
                                        requireActivity(),
                                        WaitingForApprovalFragment()
                                    )

                                } else {

                                    db.collection("users").document(auth.uid!!)
                                        .set(user)
                                        .addOnSuccessListener {

                                            db.collection("users").document(auth.uid!!)
                                                .collection("workerAttendance").document("worker")
                                                .set(WorkersAttendance(0, 0))
                                                .addOnSuccessListener {

                                                    val sp = requireActivity().getSharedPreferences(
                                                        "user",
                                                        Context.MODE_PRIVATE
                                                    )
                                                    val editor = sp.edit()
                                                    editor.putString("userEmployeeID", user.empID)
                                                    editor.putString("user", Gson().toJson(user))
                                                    editor.apply()

                                                    db.collection("admin").document("email")
                                                        .get().addOnSuccessListener {
                                                            if (it != null) {

                                                                val admin = it.toObject<Admin>()

                                                                val email = admin!!.email
                                                                val password = admin.password
                                                                val recipient = admin.recipientEmail

                                                                val props = Properties()
                                                                props["mail.smtp.auth"] = "true"
                                                                props["mail.smtp.starttls.enable"] =
                                                                    "true"
                                                                props["mail.smtp.host"] =
                                                                    "smtp.gmail.com"
                                                                props["mail.smtp.port"] = "587"

                                                                val session: Session =
                                                                    Session.getInstance(props,
                                                                        object : Authenticator() {
                                                                            override fun getPasswordAuthentication(): PasswordAuthentication {
                                                                                return PasswordAuthentication(
                                                                                    email,
                                                                                    password
                                                                                )
                                                                            }
                                                                        })

                                                                try {
                                                                    val message: Message =
                                                                        MimeMessage(session)
                                                                    message.setFrom(
                                                                        InternetAddress(
                                                                            email
                                                                        )
                                                                    )
                                                                    message.setRecipients(
                                                                        Message.RecipientType.TO,
                                                                        InternetAddress.parse(
                                                                            recipient
                                                                        )
                                                                    )
                                                                    message.subject =
                                                                        "Action Required (UPPTS App) - New User Account Created"
                                                                    message.setText(
                                                                        "New User Account is created through the app. Please verify the user from the Verify User section of the Web Portal\n" + "\n" +
                                                                                "User Account Details:" +
                                                                                "Name: " + user.firstName + " " + user.lastName + "\n" +
                                                                                "Phone Number: " + user.phoneNumber
                                                                    )
                                                                    val executor: ExecutorService =
                                                                        Executors.newSingleThreadExecutor()
                                                                    val handler =
                                                                        Handler(Looper.getMainLooper())

                                                                    executor.execute {
                                                                        Transport.send(message);
                                                                        handler.post(Runnable {
                                                                            //UI Thread work here
                                                                        })
                                                                    }
                                                                } catch (mex: MessagingException) {
                                                                    mex.printStackTrace()
                                                                }

                                                            }
                                                        }

                                                    AuthenticationActivity.openFragment(
                                                        requireActivity(),
                                                        WaitingForApprovalFragment()
                                                    )
                                                }.addOnFailureListener {
                                                    Toast.makeText(
                                                        requireActivity(),
                                                        it.message,
                                                        Toast.LENGTH_LONG
                                                    )
                                                        .show()
                                                }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                requireActivity(),
                                                it.message,
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        }

                                }

                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    userTask.exception!!.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                } else {
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

}