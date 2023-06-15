package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.Admin
import com.unifiedpts.staffportal.model.Leave
import com.unifiedpts.staffportal.model.User
import java.util.Calendar
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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LeaveHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LeaveHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var appliedLeaveTypeTextView: TextView
    lateinit var appliedDateTextView: TextView
    lateinit var remindTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leave_home, container, false)

        val backButton = view.findViewById<ImageView>(R.id.leaveHomeBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val profileTextView = view.findViewById<TextView>(R.id.leaveHomeEmployeeIDTextView)
        val sickLeaveTextView =
            view.findViewById<TextView>(R.id.leaveHomeDetailsSickLeaveTotalTextView)
        val casualLeaveTextView =
            view.findViewById<TextView>(R.id.leaveHomeDetailsCasualLeaveTotalTextView)
        val privilegeLeaveTextView =
            view.findViewById<TextView>(R.id.leaveHomeDetailsPrivilegeLeaveTotalTextView)
        val blLeaveTextView = view.findViewById<TextView>(R.id.leaveHomeDetailsBLLeaveTotalTextView)

        val appleForLeaveButton =
            view.findViewById<MaterialCardView>(R.id.leaveHomeApplyLeaveButtonCardView)
        val viewAllApplicationButton =
            view.findViewById<TextView>(R.id.leaveHomePreviousApplicationButton)
        val noApplicationTextView =
            view.findViewById<TextView>(R.id.leaveHomePreviousNoApplicationsTextView)

        val leaveApplicationApprovedView =
            view.findViewById<View>(R.id.leaveHomePreviousApplicationsLayoutApproved)
        val leaveApplicationDeniedView =
            view.findViewById<View>(R.id.leaveHomePreviousApplicationsLayoutDenied)
        val leaveApplicationPendingView =
            view.findViewById<View>(R.id.leaveHomePreviousApplicationsLayoutPending)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user: User = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID.toString()

        sickLeaveTextView.text = user.sickLeave.toString()
        casualLeaveTextView.text = user.casualLeave.toString()
        privilegeLeaveTextView.text = user.privilegeLeave.toString()
        blLeaveTextView.text = user.blLeave.toString()

        appleForLeaveButton.setOnClickListener {
            MainActivity.openFragment(requireActivity(), ApplyForLeaveFragment())
        }

        val document = Firebase.firestore.collection("leave").whereEqualTo("uid", user.uid)
            .orderBy("appliedDate", Query.Direction.DESCENDING).limit(1)

        document.get().addOnCompleteListener {
                if (it.isSuccessful) {

                    if (it.result.isEmpty) {
                        noApplicationTextView.visibility = View.VISIBLE
                        viewAllApplicationButton.visibility = View.GONE
                    } else {

                        val listOfLeaves = it.result.first()
                        val leave = listOfLeaves.toObject<Leave>()

                        val documentID = it.result.first().id

                        if (leave.status!!.compareTo(Leave.STATUS_APPROVED) == 0) {
                            leaveApplicationApprovedView.visibility = View.VISIBLE
                            appliedLeaveTypeTextView =
                                leaveApplicationApprovedView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
                            appliedDateTextView =
                                leaveApplicationApprovedView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
                        } else if (leave.status!!.compareTo(Leave.STATUS_DENIED) == 0) {
                            leaveApplicationDeniedView.visibility = View.VISIBLE
                            appliedLeaveTypeTextView =
                                leaveApplicationDeniedView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
                            appliedDateTextView =
                                leaveApplicationDeniedView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
                        } else {
                            leaveApplicationPendingView.visibility = View.VISIBLE
                            appliedLeaveTypeTextView =
                                leaveApplicationPendingView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
                            appliedDateTextView =
                                leaveApplicationPendingView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
                            remindTextView =
                                leaveApplicationPendingView.findViewById(R.id.leaveHomePreviousApplicationRemindTextView)
                        }

                        appliedLeaveTypeTextView.text = leave.leaveType
                        appliedDateTextView.text = "${leave.fromDate} - ${leave.toDate}"

                        remindTextView.setOnClickListener {
                            val dayDifference =
                                TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - leave.lastReminded!!)
                                    .toInt()

                            if (dayDifference != 0) {

                                Toast.makeText(
                                    activity,
                                    "Sending Reminder, Please Wait!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                Firebase.firestore.collection("admin").document("email")
                                    .get().addOnSuccessListener {
                                        if (it != null) {

                                            val admin = it.toObject<Admin>()

                                            val email = admin!!.email
                                            val password = admin.password
                                            val recipient = user.superiorEmail

                                            val props = Properties()
                                            props["mail.smtp.auth"] = "true"
                                            props["mail.smtp.starttls.enable"] = "true"
                                            props["mail.smtp.host"] = "smtp.gmail.com"
                                            props["mail.smtp.port"] = "587"

                                            val session: Session = Session.getInstance(props,
                                                object : Authenticator() {
                                                    override fun getPasswordAuthentication(): PasswordAuthentication {
                                                        return PasswordAuthentication(
                                                            email,
                                                            password
                                                        )
                                                    }
                                                })

                                            try {
                                                val message: Message = MimeMessage(session)
                                                message.setFrom(InternetAddress(email))
                                                message.setRecipients(
                                                    Message.RecipientType.TO,
                                                    InternetAddress.parse(recipient)
                                                )
                                                message.subject =
                                                    "Reminder: Action Required (UPPTS App) - New Leave Application Created"
                                                message.setText(
                                                    "Leave application is created by the user with below details:\n" +
                                                            "Name: " + user.firstName + " " + user.lastName + "\n" +
                                                            "Phone Number: " + user.phoneNumber + "\n\n" +
                                                            "Leave Details:\n" +
                                                            "From Date: " + leave.fromDate + "\n" +
                                                            "Till Date: " + leave.toDate + "\n" +
                                                            "Type: " + leave.leaveType + "\n" +
                                                            "Reason: " + leave.reason + "\n" +
                                                            "Attachment URL: " + (if (leave.attachmentUrl.isNullOrEmpty()) "No Documents Attached" else leave.attachmentUrl) + "\n" +
                                                            "Click the link below to verify it." + " \n" +
                                                            "https://staffportal.unifiedpts.com/js/leave-approval.html?uid=" + user.uid + "&docId=" + documentID,
                                                )
                                                val executor: ExecutorService =
                                                    Executors.newSingleThreadExecutor()
                                                val handler = Handler(Looper.getMainLooper())

                                                executor.execute {
                                                    Transport.send(message);
                                                    handler.post {
                                                        Toast.makeText(
                                                            activity,
                                                            "Reminder Sent!",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()

                                                        Firebase.firestore.collection("leave")
                                                            .document(documentID)
                                                            .update(
                                                                "lastReminded",
                                                                System.currentTimeMillis()
                                                            )
                                                        leave.lastReminded = System.currentTimeMillis()
                                                    }
                                                }
                                            } catch (mex: MessagingException) {
                                                mex.printStackTrace()
                                            }

                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    activity,
                                    "You can send only one reminder in 24 hours",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }


                    }
                }
            }

        viewAllApplicationButton.setOnClickListener {
            MainActivity.openFragment(requireActivity(), AllLeaveApplicationFragment())
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
         * @return A new instance of fragment LeaveHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LeaveHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}