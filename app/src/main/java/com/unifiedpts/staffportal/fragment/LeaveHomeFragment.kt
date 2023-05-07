package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.Leave
import com.unifiedpts.staffportal.model.User


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

        Firebase.firestore.collection("leave").whereEqualTo("uid", user.uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    if (it.result.isEmpty) {
                        noApplicationTextView.visibility = View.VISIBLE
                        viewAllApplicationButton.visibility = View.GONE
                    } else {

                        val listOfLeaves = it.result.first()
                        val leave = listOfLeaves.toObject<Leave>()

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
                        }

                        appliedLeaveTypeTextView.text = leave.leaveType
                        appliedDateTextView.text = "${leave.fromDate} - ${leave.toDate}"


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