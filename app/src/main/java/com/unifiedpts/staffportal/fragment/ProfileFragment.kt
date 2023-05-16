package com.unifiedpts.staffportal.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.User


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var user: User
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val backButton = view.findViewById<ImageView>(R.id.profileBackImageView)
        progressBar = view.findViewById(R.id.profileProgressBar)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val profileTextView = view.findViewById<TextView>(R.id.profileIDTextView)
        val profileImageView = view.findViewById<ImageView>(R.id.profileIDImageView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        user = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID.toString()

        val initialsTextView = view.findViewById<TextView>(R.id.profileEmployeeInitialsTextView)
        val nameTextView = view.findViewById<TextView>(R.id.profileEmployeeNameTextView)

        initialsTextView.text = "${user.firstName.toString()[0]}${user.lastName.toString()[0]}"
        nameTextView.text = "${user.firstName} ${user.lastName}"

        val firstNameET =
            view.findViewById<TextInputEditText>(R.id.profileFirstNameTextInputEditText)
        val lastNameET = view.findViewById<TextInputEditText>(R.id.profileLastNameTextInputEditText)


        profileTextView.setOnClickListener {

            val builder = AlertDialog.Builder(view.context)

            builder.setTitle("Log out")
            builder.setMessage("Are you sure you want to log out of the app?")

            builder.setPositiveButton(
                "Yes"
            ) { _, _ ->
                FirebaseAuth.getInstance().signOut()
                requireActivity().finish()
            }

            builder.setNegativeButton(
                "No"
            ) { _, _ ->
            }

            builder.show()
        }

        profileImageView.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)

            builder.setTitle("Log out")
            builder.setMessage("Are you sure you want to log out of the app?")

            builder.setPositiveButton(
                "Yes"
            ) { _, _ ->
                FirebaseAuth.getInstance().signOut()
                requireActivity().finish()
            }

            builder.setNegativeButton(
                "No"
            ) { _, _ ->
            }

            builder.show()
        }

        val updateButton = view.findViewById<MaterialCardView>(R.id.profileUpdateButtonCardView)

        updateButton.setOnClickListener {
            if (firstNameET.text.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Please enter first name",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (lastNameET.text.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Please enter last name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                progressBar.visibility = View.VISIBLE

                Firebase.firestore.collection("users").document(user.uid!!)
                    .update(
                        "firstName",
                        firstNameET.text.toString(),
                        "lastName",
                        lastNameET.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                context,
                                "Name is updated!",
                                Toast.LENGTH_SHORT
                            ).show()

                            val editor = sp.edit()
                            user.firstName = firstNameET.text.toString()
                            user.lastName = lastNameET.text.toString()
                            editor.putString("user", Gson().toJson(user))
                            editor.apply()

                            initialsTextView.text = "${user.firstName.toString()[0]}${user.lastName.toString()[0]}"
                            nameTextView.text = "${user.firstName} ${user.lastName}"
                        }
                        progressBar.visibility = View.GONE
                    }
            }
        }

        return view
    }

}