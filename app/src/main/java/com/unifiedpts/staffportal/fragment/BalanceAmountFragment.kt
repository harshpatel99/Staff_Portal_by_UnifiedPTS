package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
 * Use the [BalanceAmountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BalanceAmountFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_balance_amount, container, false)

        val profileTextView = view.findViewById<TextView>(R.id.balanceAmountEmployeeIDTextView)

        val backButton = view.findViewById<ImageView>(R.id.balanceAmountBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val balancesCardView =
            view.findViewById<MaterialCardView>(R.id.balanceAmountDetailsCardView)
        //val loadingView = view.findViewById<View>(R.id.balanceAmountDetailsLoadingView)

        val againstExpensesTextView =
            view.findViewById<TextView>(R.id.balanceAmountDetailsAgainstTotalTextView)
        val gratuityTextView =
            view.findViewById<TextView>(R.id.balanceAmountDetailsGratuityTotalTextView)
        val bonusTextView = view.findViewById<TextView>(R.id.balanceAmountDetailsBonusTotalTextView)
        val loanTextView = view.findViewById<TextView>(R.id.balanceAmountDetailsLoanTotalTextView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user: User = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID.toString()
        againstExpensesTextView.text = user.expenses.toString()
        gratuityTextView.text = user.gratuity.toString()
        bonusTextView.text = user.bonus.toString()
        loanTextView.text = user.loan.toString()

        /*Firebase.firestore.collection("users").document(user.uid!!)
            .collection("balanceDetails").document("againstExpenses").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    againstExpensesTextView.text = it.result["total"].toString()

                    loadingView.visibility = View.GONE
                    balancesCardView.visibility = View.VISIBLE
                } else {
                    Toast.makeText(
                        activity,
                        "Problem fetching your details, Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }*/



        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BalanceAmountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BalanceAmountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}