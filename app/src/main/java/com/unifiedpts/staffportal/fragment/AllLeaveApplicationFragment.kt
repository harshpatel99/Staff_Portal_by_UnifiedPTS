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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.adapter.AllLeavesRecyclerAdapter
import com.unifiedpts.staffportal.model.Leave
import com.unifiedpts.staffportal.model.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllLeaveApplicationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllLeaveApplicationFragment : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_all_leave_application, container, false)

        val profileTextView = view.findViewById<TextView>(R.id.allLeaveEmployeeIDTextView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID.toString()

        val backButton = view.findViewById<ImageView>(R.id.allLeaveBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val list = ArrayList<Leave>()
        val docIdList = ArrayList<String>()
        val adapter = AllLeavesRecyclerAdapter(list,requireActivity(),user,docIdList)

        Firebase.firestore.collection("leave").whereEqualTo("uid", user.uid)
            .orderBy("appliedDate", Query.Direction.DESCENDING).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (document in it.result.documents) {
                        list.add(document.toObject(Leave::class.java)!!)
                        docIdList.add(document.id)
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "Error Fetching Data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


        val linearLayoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.allLeaveRecyclerView)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllLeaveApplicationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllLeaveApplicationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}