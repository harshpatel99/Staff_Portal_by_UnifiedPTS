package com.unifiedpts.staffportal.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
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


class AllLeavesRecyclerAdapter(
    private val list: List<Leave>,
    private val activity: FragmentActivity,
    private val user: User,
    private val docIdList: List<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View
        when (viewType) {
            Leave.APPROVED -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_previous_application_approved, parent, false)
                return ApprovedViewHolder(view)
            }

            Leave.DENIED -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_previous_application_denied, parent, false)
                return DeniedViewHolder(view)
            }

            else -> {
                view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_item_previous_application_pending, parent, false)
                return PendingViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Leave = list[position]
        val docId = docIdList[position]
        when (item.status) {
            Leave.STATUS_APPROVED -> {
                (holder as ApprovedViewHolder).leaveTypeTextView.text = item.leaveType
                holder.leaveDateTextView.text = item.fromDate + " - " + item.toDate
            }

            Leave.STATUS_DENIED -> {
                (holder as DeniedViewHolder).leaveTypeTextView.text = item.leaveType
                holder.leaveDateTextView.text = item.fromDate + " - " + item.toDate
            }

            Leave.STATUS_PENDING -> {
                (holder as PendingViewHolder).leaveTypeTextView.text = item.leaveType
                holder.leaveDateTextView.text = item.fromDate + " - " + item.toDate
                holder.remindTextView.setOnClickListener {
                    val dayDifference =
                        TimeUnit.MILLISECONDS.toDays(Calendar.getInstance().timeInMillis - item.lastReminded!!)
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
                                                    "From Date: " + item.fromDate + "\n" +
                                                    "Till Date: " + item.toDate + "\n" +
                                                    "Type: " + item.leaveType + "\n" +
                                                    "Reason: " + item.reason + "\n" +
                                                    "Attachment URL: " + (if (item.attachmentUrl.isNullOrEmpty()) "No Documents Attached" else item.attachmentUrl) + "\n" +
                                                    "Click the link below to verify it." + " \n" +
                                                    "https://staffportal.unifiedpts.com/js/leave-approval.html?uid=" + user.uid + "&docId=" + docId,
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
                                                    .document(docId)
                                                    .update(
                                                        "lastReminded",
                                                        System.currentTimeMillis()
                                                    )
                                                item.lastReminded = System.currentTimeMillis()
                                                notifyDataSetChanged()
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

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position].status) {
            Leave.STATUS_APPROVED -> Leave.APPROVED
            Leave.STATUS_DENIED -> Leave.DENIED
            Leave.STATUS_PENDING -> Leave.PENDING
            else -> -1
        }
    }

    class ApprovedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leaveTypeTextView: TextView
        var leaveDateTextView: TextView

        init {
            leaveTypeTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
            leaveDateTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
        }
    }

    class DeniedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leaveTypeTextView: TextView
        var leaveDateTextView: TextView

        init {
            leaveTypeTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
            leaveDateTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
        }
    }

    class PendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leaveTypeTextView: TextView
        var leaveDateTextView: TextView
        var remindTextView: TextView

        init {
            leaveTypeTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationTypeTextView)
            leaveDateTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationDateTextView)
            remindTextView = itemView.findViewById(R.id.leaveHomePreviousApplicationRemindTextView)
        }
    }
}