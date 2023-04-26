package com.unifiedpts.staffportal.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val leaveApplicationButtonCard = view.findViewById<MaterialCardView>(R.id.homeMenuLeaveApplicationButtonCardView)
        val attendanceButtonCard = view.findViewById<MaterialCardView>(R.id.homeMenuAttendanceButtonCardView)
        val balanceButtonCard = view.findViewById<MaterialCardView>(R.id.homeMenuBalanceButtonCardView)
        val expenseButtonCard = view.findViewById<MaterialCardView>(R.id.homeMenuExpenseButtonCardView)
        val hrDocumentsButtonCard = view.findViewById<MaterialCardView>(R.id.homeMenuHRDocumentsButtonCardView)
        val homeProfileImageView = view.findViewById<ImageView>(R.id.homeProfileImageView)

        homeProfileImageView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
        }

        leaveApplicationButtonCard.setOnClickListener {
            MainActivity.openFragment(requireActivity(),LeaveHomeFragment())
        }

        attendanceButtonCard.setOnClickListener {
            MainActivity.openFragment(requireActivity(),AttendanceFragment())
        }

        balanceButtonCard.setOnClickListener {
            MainActivity.openFragment(requireActivity(),BalanceAmountFragment())
        }

        expenseButtonCard.setOnClickListener {
            //MainActivity.openFragment(requireActivity(),())
            MainActivity.openFragment(requireActivity(),AddExpenseMainFragment())
        }

        hrDocumentsButtonCard.setOnClickListener {
            MainActivity.openFragment(requireActivity(),HRDocumentsHomeFragment())
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}