package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.User

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HRDocumentsHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HRDocumentsHomeFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_h_r_documents_home, container, false)

        val backButton = view.findViewById<ImageView>(R.id.hrDocumentHomeBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user: User = gson.fromJson(json, User::class.java)

        val profileTextView = view.findViewById<TextView>(R.id.hrDocumentHomeEmployeeIDTextView)
        profileTextView.text = user.empID.toString()

        val leavePolicyCardView =
            view.findViewById<MaterialCardView>(R.id.hrDocumentMenuLeavePolicyButtonCardView)

        leavePolicyCardView.setOnClickListener {
            MainActivity.openFragment(requireActivity(), LeavePolicyFragment())
        }

        val appointmentLetterCardView =
            view.findViewById<MaterialCardView>(R.id.hrDocumentMenuAppointmentLetterButtonCardView)

        appointmentLetterCardView.setOnClickListener {
            MainActivity.openFragment(requireActivity(), AppointmentLetterFragment())
        }

        val salarySlipCardView =
            view.findViewById<MaterialCardView>(R.id.hrDocumentMenuSalarySlipButtonCardView)

        salarySlipCardView.setOnClickListener {
            MainActivity.openFragment(requireActivity(), UnderDevelopmentFragment())
        }

        val transferLetterCardView =
            view.findViewById<MaterialCardView>(R.id.hrDocumentMenuTransferLetterButtonCardView)

        transferLetterCardView.setOnClickListener {
            MainActivity.openFragment(requireActivity(), TransferLetterFragment())
        }

        val incrementLetterCardView =
            view.findViewById<MaterialCardView>(R.id.hrDocumentMenuIncrementLetterButtonCardView)

        incrementLetterCardView.setOnClickListener {
            MainActivity.openFragment(requireActivity(), IncrementLetterFragment())
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
         * @return A new instance of fragment HRDocumentsHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HRDocumentsHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}