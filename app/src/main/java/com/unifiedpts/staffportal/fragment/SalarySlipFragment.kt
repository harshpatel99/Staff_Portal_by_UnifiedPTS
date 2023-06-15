package com.unifiedpts.staffportal.fragment

import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.MonthYearPickerDialog
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.User
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SalarySlipFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalarySlipFragment : Fragment() {

    private var year: Int = 0
    private var month: Int = 0
    private  var docId: String = ""
    private  var fileName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_salary_slip, container, false)

        val profileTextView = view.findViewById<TextView>(R.id.salarySlipEmployeeIDTextView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID.toString()

        val backButton = view.findViewById<ImageView>(R.id.salarySlipBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        val monthAndYearTitleTextView =
            view.findViewById<TextView>(R.id.salarySlipFromTitleTextView)
        val selectButtonTextView = view.findViewById<TextView>(R.id.salarySlipFromSelectTextView)
        val downloadButtonTextView =
            view.findViewById<TextView>(R.id.salarySlipDownloadButtonTextView)

        val pd = MonthYearPickerDialog()
        pd.setListener { _, year, month, _ ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month-1)
            this.year = year
            this.month = month - 1

            val dateFormat = SimpleDateFormat("MM yyyy", Locale.US)
            monthAndYearTitleTextView.text = dateFormat.format(calendar.time)
            fileName = user.empID + " " + dateFormat.format(calendar.time)
            docId = SimpleDateFormat("yyyyMM", Locale.US).format(calendar.time)
        }


        selectButtonTextView.setOnClickListener {
            pd.show(requireFragmentManager(), "MonthYearPickerDialog")
        }

        downloadButtonTextView.setOnClickListener {
            if (monthAndYearTitleTextView.text.toString().compareTo("Month and Year") == 0) {
                Toast.makeText(
                    context,
                    "Please select Month and Year",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Firebase.firestore.collection("users").document(user.uid!!)
                    .collection("salarySlip").document(docId)
                    .get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result.exists()) {
                                if (it.result["url"].toString().isNotEmpty()) {

                                    val url = it.result["url"].toString()
                                    val downloadManager =
                                        requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                    val uri = Uri.parse(url)
                                    val request = DownloadManager.Request(uri)
                                    request.setTitle("$fileName.pdf")
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    request.setDestinationInExternalPublicDir(
                                        Environment.DIRECTORY_DOWNLOADS,
                                        "$fileName.pdf"
                                    )
                                    downloadManager.enqueue(request)
                                    Toast.makeText(
                                        context,
                                        "Download Started: $fileName.pdf",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    Toast.makeText(
                                        context,
                                        "File will be available in your Downloads folder",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Still Pending from HR Department",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            } else {
                                Toast.makeText(
                                    context,
                                    "Still Pending from HR Department",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    }
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
         * @return A new instance of fragment SalarySlipFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SalarySlipFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}