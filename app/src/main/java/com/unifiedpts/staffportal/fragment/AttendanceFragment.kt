package com.unifiedpts.staffportal.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.Attendance
import com.unifiedpts.staffportal.model.AttendanceWorker
import com.unifiedpts.staffportal.model.User
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * A simple [Fragment] subclass.
 * Use the [AttendanceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AttendanceFragment : Fragment() {

    private lateinit var progressBar: ProgressBar

    private lateinit var imageUri: Uri

    private lateinit var attendanceWorker: AttendanceWorker

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
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        val user = gson.fromJson(json, User::class.java)

        val profileTextView = view.findViewById<TextView>(R.id.attendanceEmployeeIDTextView)
        profileTextView.text = user.empID.toString()

        val backButton = view.findViewById<ImageView>(R.id.attendanceBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }


        val checkInButton = view.findViewById<TextView>(R.id.attendanceCheckInCardTitleTextView)
        val checkOutButton =
            view.findViewById<TextView>(R.id.attendanceCheckInCardCheckOutTimeTextView)

        val attachEngineerButton =
            view.findViewById<TextView>(R.id.attendanceAttachEngineerTextView)
        val attachWorkerButton = view.findViewById<TextView>(R.id.attendanceAttachWorkerTextView)

        val workerEditText =
            view.findViewById<TextInputEditText>(R.id.attendanceAttachWorkerTextInputEditText)
        val outsideWorkerEditText =
            view.findViewById<TextInputEditText>(R.id.attendanceAttachOutsideWorkerTextInputEditText)

        val workerTotalTextView =
            view.findViewById<TextView>(R.id.attendanceDetailsWorkerTotalTextView)
        val outsideWorkerTotalTextView =
            view.findViewById<TextView>(R.id.balanceAmountDetailsOutsideWorkerTotalTextView)

        val checkInWorkerButton = view.findViewById<TextView>(R.id.attendanceCheckInWorkerTextView)
        val checkOutWorkerButton =
            view.findViewById<TextView>(R.id.attendanceCheckOutWorkerTextView)

        progressBar = view.findViewById(R.id.attendanceProgressBar)

        progressBar.visibility = View.VISIBLE

        Firebase.firestore.collection("workerAttendance").document("worker").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    workerTotalTextView.text = it.result.data!!["totalWorkers"].toString()
                    outsideWorkerTotalTextView.text =
                        it.result.data!!["totalOutsideWorkers"].toString()
                }

                progressBar.visibility = View.GONE
            }

        if (sp.getString("checkIn", "")!!.compareTo(getDate()) == 0) {
            checkInButton.text = sp.getString("checkInTime", "")
        }

        if (sp.getString("checkOut", "")!!.compareTo(getDate()) == 0) {
            checkInButton.text = sp.getString("checkInTime", "")
        }

        if (sp.getString("checkInWorker", "")!!.compareTo(getDate()) == 0) {
            checkInButton.text = sp.getString("checkInTimeWorker", "")
        }

        if (sp.getString("checkOutWorker", "")!!.compareTo(getDate()) == 0) {
            checkInButton.text = sp.getString("checkOutTimeWorker", "")
        }

        checkInButton.setOnClickListener {

            if (sp.getString("checkIn", "")!!.compareTo(getDate()) == 0) {

                checkInButton.text = sp.getString("checkInTime", "")

                Toast.makeText(
                    requireContext(),
                    "You have already Checked In!",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else {

                val builder = AlertDialog.Builder(view.context)

                val calendarTime = Calendar.getInstance().time

                builder.setTitle("Check In")
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val currentTime: String = formatter.format(calendarTime)

                val formatterDate = SimpleDateFormat("yyyyMMdd", Locale.US)
                val currentDate: String = formatterDate.format(calendarTime)

                builder.setMessage("Time : $currentTime")

                builder.setPositiveButton(
                    "Done"
                ) { _, _ ->

                    progressBar.visibility = View.VISIBLE

                    val db = Firebase.firestore

                    val attendance = Attendance(user.uid, currentTime, "", currentDate)

                    db.collection("users").document(user.uid!!)
                        .collection("attendance").document(currentDate)
                        .set(attendance)
                        .addOnSuccessListener {

                            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

                            val editor = sp.edit()
                            editor.putString("checkIn", currentDate)
                            editor.putString("checkInTime", timeFormatter.format(calendarTime))
                            editor.apply()

                            checkInButton.text = timeFormatter.format(calendarTime)

                            Toast.makeText(
                                requireContext(),
                                "Check In Successful!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            progressBar.visibility = View.GONE

                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                .show()
                            progressBar.visibility = View.GONE
                        }
                }

                //set positive button
                builder.setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                    // User cancelled the dialog
                }
            }
        }

        checkOutButton.setOnClickListener {
            if (sp.getString("checkOut", "")!!.compareTo(getDate()) == 0) {

                checkOutButton.text = sp.getString("checkOutTime", "")

                Toast.makeText(
                    requireContext(),
                    "You have already Checked Out!",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else {

                val builder = AlertDialog.Builder(view.context)

                val calendarTime = Calendar.getInstance().time

                builder.setTitle("Check Out")
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val currentTime: String = formatter.format(calendarTime)

                val formatterDate = SimpleDateFormat("yyyyMMdd", Locale.US)
                val currentDate: String = formatterDate.format(calendarTime)

                builder.setMessage("Time : $currentTime")

                builder.setPositiveButton(
                    "Done"
                ) { _, _ ->

                    progressBar.visibility = View.VISIBLE

                    val db = Firebase.firestore

                    db.collection("users").document(user.uid!!)
                        .collection("attendance").document(currentDate)
                        .update("checkOutTime", currentTime)
                        .addOnSuccessListener {

                            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

                            val editor = sp.edit()
                            editor.putString("checkOut", currentDate)
                            editor.putString("checkOutTime", timeFormatter.format(calendarTime))
                            editor.apply()

                            checkOutButton.text = timeFormatter.format(calendarTime)

                            Toast.makeText(
                                requireContext(),
                                "Check Out Successful!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            progressBar.visibility = View.GONE

                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                .show()
                            progressBar.visibility = View.GONE
                        }
                }

                //set positive button
                builder.setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                    // User cancelled the dialog
                }
            }
        }

        attendanceWorker = AttendanceWorker(user.uid)

        attachWorkerButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            val fileName = "worker.jpg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, fileName)
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera")
            imageUri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )!!
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            attachWorkerResultLauncher.launch(intent)
        }

        attachEngineerButton.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            val fileName = "engineer.jpg"
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, fileName)
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera")
            imageUri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )!!
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            attachEngineerResultLauncher.launch(intent)
        }

        checkInWorkerButton.setOnClickListener {

            if (sp.getString("checkInWorker", "")!!.compareTo(getDate()) == 0) {

                checkInWorkerButton.text = sp.getString("checkInTimeWorker", "")

                Toast.makeText(
                    requireContext(),
                    "You have already Checked In!",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else {

                if (attendanceWorker.workerUrl.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please attach worker's image",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (attendanceWorker.workerUrl.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Please attach engineer's image",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    val builder = AlertDialog.Builder(view.context)

                    val calendarTime = Calendar.getInstance().time

                    builder.setTitle("Check In")
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val currentTime: String = formatter.format(calendarTime)

                    val formatterDate = SimpleDateFormat("yyyyMMdd", Locale.US)
                    val currentDate: String = formatterDate.format(calendarTime)

                    builder.setMessage(
                        "Time : $currentTime" + "\n"
                                + "Workers : " + attendanceWorker.totalWorkers + "\n"
                                + "Outside Workers : " + attendanceWorker.totalOutsideWorkers
                    )

                    builder.setPositiveButton(
                        "Done"
                    ) { _, _ ->

                        progressBar.visibility = View.VISIBLE

                        attendanceWorker.checkInTime = currentTime
                        attendanceWorker.date = getDate()

                        val totalWorker =
                            if (workerEditText.text!!.isEmpty()) 0 else workerEditText.text!!.toString()
                                .toLong()
                        val totalOutsideWorker =
                            if (workerEditText.text!!.isEmpty()) 0 else outsideWorkerEditText.text!!.toString()
                                .toLong()

                        Firebase.firestore.collection("workerAttendance").document("worker")
                            .update(
                                "totalWorkers",
                                FieldValue.increment(totalWorker),
                                "totalOutsideWorkers",
                                FieldValue.increment(totalOutsideWorker)
                            )
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Check In Successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    Firebase.firestore.collection("workerAttendance")
                                        .document(currentDate)
                                        .set(attendanceWorker)
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {

                                                val timeFormatter =
                                                    SimpleDateFormat("hh:mm a", Locale.US)

                                                val editor = sp.edit()
                                                editor.putString("checkInWorker", currentDate)
                                                editor.putString(
                                                    "checkInTimeWorker",
                                                    timeFormatter.format(calendarTime)
                                                )
                                                editor.apply()

                                                checkInWorkerButton.text =
                                                    timeFormatter.format(calendarTime)

                                                Toast.makeText(
                                                    requireContext(),
                                                    "Check In Successful!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            progressBar.visibility = View.GONE
                                        }
                                }
                            }
                    }

                    //set positive button
                    builder.setNegativeButton(
                        "Cancel"
                    ) { _, _ ->
                        // User cancelled the dialog
                    }


                }
            }
        }

        checkOutWorkerButton.setOnClickListener {
            if (sp.getString("checkOutWorker", "")!!.compareTo(getDate()) == 0) {

                checkOutWorkerButton.text = sp.getString("checkOutTimeWorker", "")

                Toast.makeText(
                    requireContext(),
                    "You have already Checked Out!",
                    Toast.LENGTH_SHORT
                )
                    .show()

            } else {
                val builder = AlertDialog.Builder(view.context)

                val calendarTime = Calendar.getInstance().time

                builder.setTitle("Check Out")
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val currentTime: String = formatter.format(calendarTime)

                val formatterDate = SimpleDateFormat("yyyyMMdd", Locale.US)
                val currentDate: String = formatterDate.format(calendarTime)

                builder.setMessage("Time : $currentTime")

                builder.setPositiveButton(
                    "Done"
                ) { _, _ ->

                    progressBar.visibility = View.VISIBLE

                    val db = Firebase.firestore

                    db.collection("workerAttendance").document(currentDate)
                        .update("checkOutTime", currentTime)
                        .addOnSuccessListener {

                            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.US)

                            val editor = sp.edit()
                            editor.putString("checkOutWorker", currentDate)
                            editor.putString(
                                "checkOutTimeWorker",
                                timeFormatter.format(calendarTime)
                            )
                            editor.apply()

                            checkOutWorkerButton.text = timeFormatter.format(calendarTime)

                            Toast.makeText(
                                requireContext(),
                                "Check Out Successful!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            progressBar.visibility = View.GONE

                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                .show()
                            progressBar.visibility = View.GONE
                        }
                }

                //set positive button
                builder.setNegativeButton(
                    "Cancel"
                ) { _, _ ->
                    // User cancelled the dialog
                }
            }
        }


        return view
    }

    private val attachWorkerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == Activity.RESULT_OK) {

                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri);

                val baos = ByteArrayOutputStream()

                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)

                val imageInBytes: ByteArray = baos.toByteArray()

                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
                val currentDate = sdf.format(Date())

                val directoryPath = "attendance/workers/$currentDate/worker"

                Toast.makeText(
                    context, "Uploading an attachment", Toast.LENGTH_SHORT
                ).show()

                val storageRef = Firebase.storage.reference

                storageRef.child(directoryPath).putBytes(imageInBytes).addOnSuccessListener {

                    Toast.makeText(
                        context, "Attachment is Uploaded", Toast.LENGTH_SHORT
                    ).show()

                    attendanceWorker.workerUrl = it.storage.downloadUrl.toString()

                    progressBar.visibility = View.GONE

                }

            }
        }

    private val attachEngineerResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == Activity.RESULT_OK) {

                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri);

                val baos = ByteArrayOutputStream()

                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)

                val imageInBytes: ByteArray = baos.toByteArray()

                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
                val currentDate = sdf.format(Date())

                val directoryPath = "attendance/workers/$currentDate/engineer"

                Toast.makeText(
                    context, "Uploading an attachment", Toast.LENGTH_SHORT
                ).show()

                val storageRef = Firebase.storage.reference

                storageRef.child(directoryPath).putBytes(imageInBytes).addOnSuccessListener {

                    Toast.makeText(
                        context, "Attachment is Uploaded", Toast.LENGTH_SHORT
                    ).show()

                    attendanceWorker.engineerUrl = it.storage.downloadUrl.toString()

                    progressBar.visibility = View.GONE

                }

            }
        }

    private fun getDate(): String {
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
        return formatter.format(Calendar.getInstance().time)
    }

}