package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WaitingForApprovalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WaitingForApprovalFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_waiting_for_approval, container, false)

        val profileTextView = view.findViewById<TextView>(R.id.waitingForApprovalEmployeeIDTextView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user: User = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID

        val sendReminderCard =
            view.findViewById<MaterialCardView>(R.id.waitingForApprovalRemindCardView)

        sendReminderCard.setOnClickListener {
            /*BackgroundMail.newBuilder(this)
                .withUsername("Your Email")
                .withPassword("Your App Password")
                .withSenderName("Sender Name")
                .withMailTo("mail-to@gmail.com")
                .withMailCc("mail-cc@gmail.com")
                .withMailBcc("mail-bcc@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Email Subject")
                .withBody("Email Body")
                .withSendingMessage(R.string.sending_email)
                .withOnSuccessCallback(object : OnSendingCallback() {
                    fun onSuccess() {
                        Toast.makeText(getApplicationContext(), "Email Sent", Toast.LENGTH_SHORT)
                            .show()
                    }

                    fun onFail(e: Exception?) {
                        Toast.makeText(
                            getApplicationContext(),
                            "Email Not Sent",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                .send()*/
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
         * @return A new instance of fragment WaitingForApprovalFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WaitingForApprovalFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}