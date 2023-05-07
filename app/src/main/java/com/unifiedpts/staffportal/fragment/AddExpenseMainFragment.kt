package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.ExpenseDetails
import com.unifiedpts.staffportal.model.Project


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

    private lateinit var adapter: ArrayAdapter<Any?>

    private lateinit var listOfProjects: List<Project>
    private lateinit var stateList: ArrayList<String>
    private lateinit var cityList: ArrayList<String>
    private lateinit var projectNameList: ArrayList<String>
    private lateinit var projectNumberList: ArrayList<String>
    private lateinit var floorList: ArrayList<String>
    private lateinit var pourList: ArrayList<String>

    private lateinit var expenseDetails: ExpenseDetails

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

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


        //val cityList = arrayOf("Pune", "Mumbai", "Ahmedabad")
        //val state = arrayOf("Maharashtra", "Gujarat")
        //val project = arrayOf("Project 1", "Project 2", "Project 3")
        //val floor = arrayOf("Floor 1", "Floor 2", "Floor 3")
        //val pour = arrayOf("Pour 1", "Pour 2")
        val work = arrayOf("Installation", "Stressing", "Cable Cutting", "Site Visit", "Marketing")


        val backButton = view.findViewById<ImageView>(R.id.addExpenseMainBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val loadingView = view.findViewById<View>(R.id.addExpenseMainLoadingView)

        val stateSpinnerLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainStateSpinnerLayout)
        val citySpinnerLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainCitySpinnerLayout)
        val projectSpinnerLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainProjectSpinnerLayout)
        val floorSpinnerLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainFloorSpinnerLayout)
        val pourSpinnerLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainPourSpinnerLayout)
        val workSpinnerLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainWorkSpinnerLayout)
        val remarkTextInputLayout =
            view.findViewById<TextInputLayout>(R.id.addExpenseMainRemarksTextInputLayout)

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


        val profileTextView = view.findViewById<TextView>(R.id.addExpenseMainEmployeeIDTextView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        profileTextView.text = sp.getString("userEmployeeID", "000")

        Firebase.firestore.collection("projectDetails").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    listOfProjects = it.result.toObjects(Project::class.java)

                    val allStates = ArrayList<String>()

                    for (item in listOfProjects) {
                        allStates.add(item.state!!)
                    }

                    val set: Set<String> = HashSet<String>(allStates)
                    allStates.clear()
                    allStates.addAll(set)

                    stateList = allStates

                    adapter =
                        ArrayAdapter<Any?>(
                            requireContext(), R.layout.item_drop_down,
                            allStates as List<Any?>
                        )
                    stateACTView.setAdapter(adapter)

                    loadingView.visibility = View.GONE

                    stateSpinnerLayout.visibility = View.VISIBLE

                    workSpinnerLayout.visibility = View.VISIBLE
                    remarkTextInputLayout.visibility = View.VISIBLE

                    fillExpenseButtonCardView.visibility = View.VISIBLE
                }
            }

        /*adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, project)
        projectACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, floor)
        floorACTView.setAdapter(adapter)

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, pour)
        pourACTView.setAdapter(adapter)*/

        adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, work)
        workACTView.setAdapter(adapter)

        stateACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selectedState = parent.getItemAtPosition(position).toString()
            expenseDetails.state = selectedState

            val allCities = ArrayList<String>()

            if (listOfProjects.isNotEmpty()) {
                for (item in listOfProjects) {
                    if (item.state!!.compareTo(selectedState) == 0) {
                        allCities.add(item.city!!)
                    }
                }
            }

            val set: Set<String> = HashSet<String>(allCities)
            allCities.clear()
            allCities.addAll(set)

            cityList = allCities

            adapter = ArrayAdapter<Any?>(
                requireContext(), R.layout.item_drop_down,
                cityList as List<Any?>
            )
            cityACTView.setAdapter(adapter)

            citySpinnerLayout.visibility = View.VISIBLE

        }

        cityACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selectedCity = parent.getItemAtPosition(position).toString()
            expenseDetails.city = selectedCity

            val allProjectNames = ArrayList<String>()
            val allProjectNumber = ArrayList<String>()

            if (listOfProjects.isNotEmpty()) {
                for (item in listOfProjects) {
                    if (item.state!!.compareTo(expenseDetails.state!!) == 0 && item.city!!.compareTo(
                            selectedCity
                        ) == 0
                    ) {
                        allProjectNames.add(item.projectName!!)
                        allProjectNumber.add(item.projectNumber!!)
                    }
                }
            }

            val set: Set<String> = HashSet<String>(allProjectNames)
            allProjectNames.clear()
            allProjectNames.addAll(set)

            projectNameList = allProjectNames
            projectNumberList = allProjectNumber

            adapter = ArrayAdapter<Any?>(
                requireContext(), R.layout.item_drop_down,
                projectNameList as List<Any?>
            )
            projectACTView.setAdapter(adapter)

            projectSpinnerLayout.visibility = View.VISIBLE
        }

        projectACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selectedProjectName = parent.getItemAtPosition(position).toString()

            val allFloors = ArrayList<String>()

            expenseDetails.projectName = selectedProjectName
            expenseDetails.projectNumber =
                projectNumberList.elementAt(projectNameList.indexOf(selectedProjectName))

            if (listOfProjects.isNotEmpty()) {
                for (item in listOfProjects) {
                    if (item.state!!.compareTo(expenseDetails.state!!) == 0 &&
                        item.city!!.compareTo(expenseDetails.city!!) == 0 &&
                        item.projectName!!.compareTo(selectedProjectName) == 0
                    ) {
                        allFloors.add(item.floor!!)
                    }
                }
            }

            val set: Set<String> = HashSet<String>(allFloors)
            allFloors.clear()
            allFloors.addAll(set)

            floorList = allFloors

            adapter = ArrayAdapter<Any?>(
                requireContext(), R.layout.item_drop_down,
                floorList as List<Any?>
            )
            floorACTView.setAdapter(adapter)

            floorSpinnerLayout.visibility = View.VISIBLE

        }

        floorACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selectedFloor = parent.getItemAtPosition(position).toString()

            expenseDetails.floor = selectedFloor

            val allPours = ArrayList<String>()

            if (listOfProjects.isNotEmpty()) {
                for (item in listOfProjects) {
                    if (item.state!!.compareTo(expenseDetails.state!!) == 0 &&
                        item.city!!.compareTo(expenseDetails.city!!) == 0 &&
                        item.projectName!!.compareTo(expenseDetails.projectName!!) == 0 &&
                        item.floor!!.compareTo(selectedFloor) == 0
                    ) {
                        allPours.add(item.pour!!)
                    }
                }
            }

            val set: Set<String> = HashSet<String>(allPours)
            allPours.clear()
            allPours.addAll(set)

            pourList = allPours

            adapter = ArrayAdapter<Any?>(
                requireContext(), R.layout.item_drop_down,
                pourList as List<Any?>
            )
            pourACTView.setAdapter(adapter)

            pourSpinnerLayout.visibility = View.VISIBLE

        }

        pourACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val selectedPour = parent.getItemAtPosition(position).toString()

            expenseDetails.pour = selectedPour

        }

        workACTView.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            val text = parent.getItemAtPosition(position).toString()
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
                expenseDetails.attachmentUrls = HashMap()
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