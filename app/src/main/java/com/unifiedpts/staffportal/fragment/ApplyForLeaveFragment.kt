package com.unifiedpts.staffportal.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.Admin
import com.unifiedpts.staffportal.model.Leave
import com.unifiedpts.staffportal.model.User
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Properties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


/**
 * A simple [Fragment] subclass.
 * Use the [ApplyForLeaveFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ApplyForLeaveFragment : Fragment() {

    private lateinit var storageRef: StorageReference

    lateinit var leave: Leave
    lateinit var user: User

    var fromYear: Int = 0
    var fromMonth: Int = 0
    var fromDate: Int = 0

    var toYear: Int = 0
    var toMonth: Int = 0
    var toDate: Int = 0

    var isFromSelected = false
    var isToSelected = false

    var fromDateInMillis: Long = 0

    lateinit var attachmentTitleTextView: TextView
    private lateinit var progressBar: ProgressBar


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
        val view = inflater.inflate(R.layout.fragment_apply_for_leave, container, false)

        val profileTextView = view.findViewById<TextView>(R.id.appleForLeaveEmployeeIDTextView)

        val sp = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)

        val gson = Gson()
        val json: String = sp.getString("user", "")!!
        user = gson.fromJson(json, User::class.java)

        profileTextView.text = user.empID.toString()

        progressBar = view.findViewById(R.id.appleForLeaveProgressBar)

        val work = arrayOf(
            "Sick Leave",
            "Casual Leave",
            "Privilege Leave",
            "BL Leave",
            "Leave Without Pay"
        )

        val backButton = view.findViewById<ImageView>(R.id.appleForLeaveBackImageView)

        backButton.setOnClickListener {
            MainActivity.closeFragment(requireActivity())
        }

        leave = Leave(user.uid!!)

        //val fromTitleTextView = view.findViewById<TextView>(R.id.appleForLeaveFromTitleTextView)
        val fromSelectTextView = view.findViewById<TextView>(R.id.appleForLeaveFromSelectTextView)
        //val toTitleTextView = view.findViewById<TextView>(R.id.appleForLeaveToTitleTextView)
        val toSelectTextView = view.findViewById<TextView>(R.id.appleForLeaveToSelectTextView)
        val attachmentTextView =
            view.findViewById<TextView>(R.id.appleForLeaveAttachDocumentTimeTextView)
        attachmentTitleTextView =
            view.findViewById(R.id.appleForLeaveAttachDocumentTitleTextView)

        //val leaveTypeSpinnerLayout =
        //  view.findViewById<TextInputLayout>(R.id.appleForLeaveTypeOfLeaveSpinnerLayout)

        val typeOfLeaveACTView =
            view.findViewById<AppCompatAutoCompleteTextView>(R.id.applyForLeaveTypeofLeaveACTView)

        val remarkETView =
            view.findViewById<TextInputEditText>(R.id.appleForLeaveReasonTextInputEditText)

        val fillExpenseButtonTextView =
            view.findViewById<TextView>(R.id.appleForLeaveApplyButtonTextView)

        val adapter =
            ArrayAdapter<Any?>(requireContext(), R.layout.item_drop_down, work)
        typeOfLeaveACTView.setAdapter(adapter)

        typeOfLeaveACTView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                leave.leaveType = parent.getItemAtPosition(position).toString()

            }

        storageRef = Firebase.storage.reference

        fromSelectTextView.setOnClickListener {

            if(fromDateInMillis == 0.toLong()) {

                val c: Calendar = Calendar.getInstance()
                fromYear = c.get(Calendar.YEAR)
                fromMonth = c.get(Calendar.MONTH)
                fromDate = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        fromSelectTextView.text =
                            "$dayOfMonth-${monthOfYear + 1}-$year"
                        isFromSelected = true

                        fromYear = year
                        fromMonth = (monthOfYear + 1)
                        fromDate = dayOfMonth

                        val cal = Calendar.getInstance()
                        cal[Calendar.DAY_OF_MONTH] = dayOfMonth
                        cal[Calendar.MONTH] = monthOfYear
                        cal[Calendar.YEAR] = year

                        fromDateInMillis = cal.timeInMillis

                    }, fromYear, fromMonth, fromDate
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()

            }else{
                isToSelected = false
                toSelectTextView.text = "Select"

                val c: Calendar = Calendar.getInstance()
                fromYear = c.get(Calendar.YEAR)
                fromMonth = c.get(Calendar.MONTH)
                fromDate = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        fromSelectTextView.text =
                            "$dayOfMonth-${monthOfYear + 1}-$year"
                        isFromSelected = true

                        fromYear = year
                        fromMonth = (monthOfYear + 1)
                        fromDate = dayOfMonth

                        val cal = Calendar.getInstance()
                        cal[Calendar.DAY_OF_MONTH] = dayOfMonth
                        cal[Calendar.MONTH] = monthOfYear
                        cal[Calendar.YEAR] = year

                        fromDateInMillis = cal.timeInMillis

                    }, fromYear, fromMonth, fromDate
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis()
                datePickerDialog.show()
            }
        }

        toSelectTextView.setOnClickListener {

            if (fromDateInMillis == 0.toLong()) {
                Toast.makeText(
                    context,
                    "Please select from date",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val c: Calendar = Calendar.getInstance()
                toYear = c.get(Calendar.YEAR)
                toMonth = c.get(Calendar.MONTH)
                toDate = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        toSelectTextView.text = "$dayOfMonth-${monthOfYear + 1}-$year"
                        isToSelected = true

                        toYear = year
                        toMonth = (monthOfYear + 1)
                        toDate = dayOfMonth

                    }, toYear, toMonth, toDate
                )
                datePickerDialog.datePicker.minDate = fromDateInMillis
                datePickerDialog.show()
            }
        }

        uploadImage(attachmentTextView)

        fillExpenseButtonTextView.setOnClickListener {

            val reason = remarkETView.text.toString()

            if ((!isFromSelected) || (!isToSelected)) {
                Toast.makeText(
                    context,
                    "Please select the dates",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (leave.leaveType.isNullOrEmpty()) {
                Toast.makeText(
                    context,
                    "Please select the type of the leave",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (reason.isEmpty()) {
                Toast.makeText(
                    context,
                    "Please specify the reason for the leave",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                if (leave.leaveType!!.compareTo("Leave Without Pay") != 0) {

                    val reqDate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    val fromDay = reqDate.parse("$fromDate/$fromMonth/$fromYear")
                    val toDay = reqDate.parse("$toDate/$toMonth/$toYear")

                    var dayDifference = TimeUnit.MILLISECONDS.toDays(toDay!!.time - fromDay!!.time)

                    dayDifference += 1

                    val leaveTypeDayBalance = when (leave.leaveType) {
                        "Sick Leave" -> user.sickLeave
                        "Casual Leave" -> user.casualLeave
                        "Privilege Leave" -> user.privilegeLeave
                        else -> user.blLeave
                    }

                    if (dayDifference > leaveTypeDayBalance!!) {
                        Toast.makeText(
                            context,
                            "You do not have balance of $dayDifference for ${leave.leaveType}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (leave.leaveType!!.compareTo("Sick Leave") == 0 && dayDifference > 2 && leave.attachmentUrl == null) {
                        Toast.makeText(
                            context,
                            "Attachment is required!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val timeInMillis = System.currentTimeMillis()

                        leave.reason = reason
                        leave.fromDate = fromSelectTextView.text.toString()
                        leave.toDate = toSelectTextView.text.toString()
                        leave.appliedDate = timeInMillis
                        leave.lastReminded = timeInMillis
                        leave.status = Leave.STATUS_PENDING

                        val document = Firebase.firestore.collection("leave").document()

                        document.set(leave).addOnSuccessListener {
                            Firebase.firestore.collection("admin").document("email")
                                .get().addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        val admin = it.result.toObject<Admin>()

                                        val email = admin!!.email
                                        val password = admin.password
                                        val recipient = user.superiorEmail

                                        val props = Properties()
                                        props["mail.smtp.auth"] = "true"
                                        props["mail.smtp.starttls.enable"] = "true"
                                        props["mail.smtp.host"] = "smtp.gmail.com"
                                        props["mail.smtp.port"] = "587"

                                        val session: Session = Session.getInstance(props,
                                            object : Authenticator() {
                                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                                    return PasswordAuthentication(
                                                        email,
                                                        password
                                                    )
                                                }
                                            })

                                        try {
                                            val message: Message = MimeMessage(session)
                                            message.setFrom(InternetAddress(email))
                                            message.setRecipients(
                                                Message.RecipientType.TO,
                                                InternetAddress.parse(recipient)
                                            )
                                            message.subject =
                                                "Action Required (UPPTS App) - New Leave Application Created"
                                            message.setText(
                                                "Leave application is created by the user with below details:\n" +
                                                        "Name: " + user.firstName + " " + user.lastName + "\n" +
                                                        "Phone Number: " + user.phoneNumber + "\n\n" +
                                                        "Leave Details:\n" +
                                                        "From Date: " + leave.fromDate + "\n" +
                                                        "Till Date: " + leave.toDate + "\n" +
                                                        "Type: " + leave.leaveType + "\n" +
                                                        "Reason: " + leave.reason + "\n" +
                                                        "Attachment URL: " + (if (leave.attachmentUrl.isNullOrEmpty()) "No Documents Attached" else leave.attachmentUrl) + "\n" +
                                                        "Click the link below to verify it." + " \n" +
                                                        "https://staffportal.unifiedpts.com/js/leave-approval.html?uid=" + user.uid + "&docId=" + document.id,
                                            )
                                            val executor: ExecutorService =
                                                Executors.newSingleThreadExecutor()
                                            val handler = Handler(Looper.getMainLooper())

                                            executor.execute {
                                                Transport.send(message);
                                                handler.post(Runnable {
                                                    //UI Thread work here
                                                })
                                            }
                                        } catch (mex: MessagingException) {
                                            mex.printStackTrace()
                                        }


                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Error: " + it.exception!!.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                            Toast.makeText(
                                context,
                                "Application is submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            MainActivity.closeFragment(requireActivity())
                        }.addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Error while applying : ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    val timeInMillis = System.currentTimeMillis()

                    leave.reason = reason
                    leave.fromDate = fromSelectTextView.text.toString()
                    leave.toDate = toSelectTextView.text.toString()
                    leave.appliedDate = timeInMillis
                    leave.lastReminded = timeInMillis
                    leave.status = Leave.STATUS_PENDING

                    val document = Firebase.firestore.collection("leave").document()
                    document.set(leave)
                        .addOnSuccessListener {

                            Firebase.firestore.collection("admin").document("email")
                                .get().addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        val admin = it.result.toObject<Admin>()

                                        val email = admin!!.email
                                        val password = admin.password
                                        val recipient = user.superiorEmail

                                        val props = Properties()
                                        props["mail.smtp.auth"] = "true"
                                        props["mail.smtp.starttls.enable"] = "true"
                                        props["mail.smtp.host"] = "smtp.gmail.com"
                                        props["mail.smtp.port"] = "587"

                                        val session: Session = Session.getInstance(props,
                                            object : Authenticator() {
                                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                                    return PasswordAuthentication(
                                                        email,
                                                        password
                                                    )
                                                }
                                            })

                                        try {
                                            val message: Message = MimeMessage(session)
                                            message.setFrom(InternetAddress(email))
                                            message.setRecipients(
                                                Message.RecipientType.TO,
                                                InternetAddress.parse(recipient)
                                            )
                                            message.subject =
                                                "Action Required (UPPTS App) - New Leave Application Created"
                                            message.setText(
                                                "Leave application is created by the user with below details:\n" +
                                                        "Name: " + user.firstName + " " + user.lastName + "\n" +
                                                        "Phone Number: " + user.phoneNumber + "\n\n" +
                                                        "Leave Details:\n" +
                                                        "From Date: " + leave.fromDate + "\n" +
                                                        "Till Date: " + leave.toDate + "\n" +
                                                        "Type: " + leave.leaveType + "\n" +
                                                        "Reason: " + leave.reason + "\n" +
                                                        "Attachment URL: " + (if (leave.attachmentUrl.isNullOrEmpty()) "No Documents Attached" else leave.attachmentUrl) + "\n" +
                                                        "Click the link below to verify it." + " \n" +
                                                        "https://staffportal.unifiedpts.com/js/leave-approval.html?uid=" + user.uid + "&docId=" + document.id,
                                            )
                                            val executor: ExecutorService =
                                                Executors.newSingleThreadExecutor()
                                            val handler = Handler(Looper.getMainLooper())

                                            executor.execute {
                                                Transport.send(message);
                                                handler.post(Runnable {
                                                    //UI Thread work here
                                                })
                                            }
                                        } catch (mex: MessagingException) {
                                            mex.printStackTrace()
                                        }

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Error: " + it.exception!!.message,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }

                            Toast.makeText(
                                requireActivity(),
                                "Application is submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            MainActivity.closeFragment(requireActivity())
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireActivity(),
                                "Error while applying : ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

            }
        }

        return view
    }

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image

                val builder = AlertDialog.Builder(requireView().context)

                builder.setTitle("Please Wait")
                builder.setMessage("Uploading an Attachment")
                builder.setCancelable(false)

                val dialog = builder.create()
                dialog.show()

                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension

                if (imageUri != null) {

                    val sd = getFileName(requireContext(), imageUri)

                    val bmp = MediaStore.Images.Media.getBitmap(
                        requireActivity().contentResolver,
                        imageUri
                    )

                    val baos = ByteArrayOutputStream()

                    // here we can choose quality factor
                    // in third parameter(ex. here it is 25)
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos)

                    val fileInBytes: ByteArray = baos.toByteArray()

                    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
                    val currentDate = sdf.format(Date())

                    val directoryPath = "${user.empID}${user.uid}/leave/$currentDate/$sd"

                    // Upload Task with upload to directory 'file'
                    // and name of the file remains same
                    storageRef.child(directoryPath).putBytes(fileInBytes).addOnSuccessListener {
                        storageRef.child(directoryPath).downloadUrl.addOnSuccessListener {
                            leave.attachmentUrl = it.toString()
                            attachmentTitleTextView.text = "Document Attached"

                            progressBar.visibility = View.GONE
                            dialog.dismiss()
                        }
                    }
                } else {
                    progressBar.visibility = View.GONE
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "Attachment Cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it) }
    }

    private fun uploadImage(textView: TextView) {
        textView.setOnClickListener {

            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)

            progressBar.visibility = View.VISIBLE

        }
    }

}