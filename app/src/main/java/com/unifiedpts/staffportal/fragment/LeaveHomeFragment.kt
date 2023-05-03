package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leave_home, container, false)

        val profileTextView = view.findViewById<TextView>(R.id.leaveHomeEmployeeIDTextView)
        val sickLeaveTextView =
            view.findViewById<TextView>(R.id.leaveHomeDetailsSickLeaveTotalTextView)
        val casualLeaveTextView =
            view.findViewById<TextView>(R.id.leaveHomeDetailsCasualLeaveTotalTextView)
        val privilegeLeaveTextView =
            view.findViewById<TextView>(R.id.leaveHomeDetailsPrivilegeLeaveTotalTextView)
        val blLeaveTextView = view.findViewById<TextView>(R.id.leaveHomeDetailsBLLeaveTotalTextView)

        val appleForLeaveButton = view.findViewById<MaterialCardView>(R.id.leaveHomeApplyLeaveButtonCardView)

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