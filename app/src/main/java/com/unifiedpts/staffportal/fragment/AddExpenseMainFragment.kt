package com.unifiedpts.staffportal.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.activity.AuthenticationActivity
import com.unifiedpts.staffportal.model.ExpenseDetails


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddExpenseMainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExpenseMainFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var expenseDetails: ExpenseDetails

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
        val view = inflater.inflate(R.layout.fragment_add_expense_main, container, false)

        val auth = FirebaseAuth.getInstance().currentUser

        expenseDetails = ExpenseDetails(auth!!.uid, System.currentTimeMillis())

        val cityList = arrayOf("Pune", "Mumbai", "Ahmedabad")
        val state = arrayOf("Maharashtra", "Gujarat")
        val project = arrayOf("Project 1", "Project 2", "Project 3")
        val floor = arrayOf("Floor 1", "Floor 2", "Floor 3")
        val pour = arrayOf("Pour 1", "Pour 2")
        val work = arrayOf("Installation", "Stressing", "Cable Cutting", "Site Visit", "Marketing")


        val backButton = view.findViewById<ImageView>(R.id.addExpenseMainBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val cityACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.addExpenseMainCityACTView)
        val stateACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.addExpenseMainStateACTView)
        val projectACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.addExpenseMainProjectACTView)
        val floorACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.addExpenseMainFloorACTView)
        val pourACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.addExpenseMainPourACTView)
        val workACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.addExpenseMainWorkACTView)
        val remarkETView =
            view.findViewById<TextInputEditText>(R.id.addExpenseMainRemarksTextInputEditText)

        val fillExpenseButtonCardView =
            view.findViewById<MaterialCardView>(R.id.addExpenseMainFillExpensesButtonCardView)

        var adapter = ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, cityList)
        cityACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, state)
        stateACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, project)
        projectACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, floor)
        floorACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, pour)
        pourACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, work)
        workACTView.setAdapter(adapter)


        cityACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()

            expenseDetails.city = text
        }

        stateACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()

            expenseDetails.state = text
        }

        projectACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()

            expenseDetails.projectName = text
            expenseDetails.projectNumber = text

        }

        floorACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()

            expenseDetails.floor = text

        }

        pourACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()

            expenseDetails.pour = text

        }

        workACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
            Toast.makeText(
                context,
                text,
                Toast.LENGTH_SHORT
            ).show()

            expenseDetails.work = text

        }

        fillExpenseButtonCardView.setOnClickListener {
            expenseDetails.remark = remarkETView.text.toString()

            if (expenseDetails.state == null) {
                Toast.makeText(
                    context,
                    "Please select State",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (expenseDetails.city == null) {
                Toast.makeText(
                    context,
                    "Please select City",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (expenseDetails.projectNumber == null) {
                Toast.makeText(
                    context,
                    "Please select Project",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (expenseDetails.floor == null) {
                Toast.makeText(
                    context,
                    "Please select Floor",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (expenseDetails.pour == null) {
                Toast.makeText(
                    context,
                    "Please select Pour",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val bundle = Bundle()
                bundle.putSerializable("expenseDetails", expenseDetails)

                val fragment = AddExpenseDetailsFragment()
                fragment.arguments = bundle

                MainActivity.openFragment(requireActivity(), fragment)
            }


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
         * @return A new instance of fragment AddExpenseMainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddExpenseMainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}