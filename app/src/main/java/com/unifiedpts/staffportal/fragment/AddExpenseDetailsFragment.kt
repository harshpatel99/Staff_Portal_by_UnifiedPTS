package com.unifiedpts.staffportal.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.unifiedpts.staffportal.MainActivity
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.activity.AuthenticationActivity
import com.unifiedpts.staffportal.model.AgainstExpenses
import com.unifiedpts.staffportal.model.ExpenseDetails
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddExpenseDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddExpenseDetailsFragment : Fragment() {

    private lateinit var expenseDetails: ExpenseDetails

    private lateinit var storageRef: StorageReference
    private lateinit var selectedImage: String
    private lateinit var submitButtonCardView: MaterialCardView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            expenseDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireArguments().getSerializable("expenseDetails", ExpenseDetails::class.java)!!
            } else {
                requireArguments().getSerializable("expenseDetails") as ExpenseDetails
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_expense_details, container, false)

        val layoutCashWorker = view.findViewById<View>(R.id.addExpenseDetailsLayoutCashWorker)
        val layoutWorkerAuto = view.findViewById<View>(R.id.addExpenseDetailsLayoutWorkerAuto)
        val layoutWorkerFood = view.findViewById<View>(R.id.addExpenseDetailsLayoutWorkerFood)
        val layoutEngineerFood = view.findViewById<View>(R.id.addExpenseDetailsLayoutEngineerFood)
        val layoutEngineerAutoCab =
            view.findViewById<View>(R.id.addExpenseDetailsLayoutEngineerAutoCab)
        val layoutEngineerHotel = view.findViewById<View>(R.id.addExpenseDetailsLayoutEngineerHotel)
        val layoutWorkerHotel = view.findViewById<View>(R.id.addExpenseDetailsLayoutWorkerHotel)
        val layoutBusTrainFare = view.findViewById<View>(R.id.addExpenseDetailsLayoutBusTrainFare)
        val layoutPetrolDiesel = view.findViewById<View>(R.id.addExpenseDetailsLayoutPetrolDiesel)
        val layoutMaterialTransportation =
            view.findViewById<View>(R.id.addExpenseDetailsLayoutMaterialTransportation)
        val layoutPrinting = view.findViewById<View>(R.id.addExpenseDetailsLayoutPrintingStationary)
        val layoutOther = view.findViewById<View>(R.id.addExpenseDetailsLayoutOther)

        submitButtonCardView = view.findViewById(R.id.addExpenseDetailsSubmitButtonCardView)
        progressBar = view.findViewById(R.id.signInProgressBar)

        val cashWorkerTextView =
            layoutCashWorker.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val cashWorkerET =
            layoutCashWorker.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val cashWorkerAttachImageView =
            layoutCashWorker.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val workerAutoTextView =
            layoutWorkerAuto.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val workerAutoET =
            layoutWorkerAuto.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val workerAutoAttachImageView =
            layoutWorkerAuto.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val workerFoodTextView =
            layoutWorkerFood.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val workerFoodET =
            layoutWorkerFood.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val workerFoodAttachImageView =
            layoutWorkerFood.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val engineerFoodTextView =
            layoutEngineerFood.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val engineerFoodET =
            layoutEngineerFood.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val engineerFoodAttachImageView =
            layoutEngineerFood.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val engineerAutoCabTextView =
            layoutEngineerAutoCab.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val engineerAutoCabET =
            layoutEngineerAutoCab.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val engineerAutoCabAttachImageView =
            layoutEngineerAutoCab.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val engineerHotelTextView =
            layoutEngineerHotel.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val engineerHotelET =
            layoutEngineerHotel.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val engineerHotelAttachImageView =
            layoutEngineerHotel.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val workerHotelTextView =
            layoutWorkerHotel.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val workerHotelET =
            layoutWorkerHotel.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val workerHotelAttachImageView =
            layoutWorkerHotel.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val busTrainFareTextView =
            layoutBusTrainFare.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val busTrainFareET =
            layoutBusTrainFare.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val busTrainFareAttachImageView =
            layoutBusTrainFare.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val fuelTextView =
            layoutPetrolDiesel.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val fuelET =
            layoutPetrolDiesel.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val fuelAttachImageView =
            layoutPetrolDiesel.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val materialTransportationTextView =
            layoutMaterialTransportation.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val materialTransportationET =
            layoutMaterialTransportation.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val materialTransportationAttachImageView =
            layoutMaterialTransportation.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val printingTextView =
            layoutPrinting.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val printingET =
            layoutPrinting.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val printingAttachImageView =
            layoutPrinting.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)

        val otherTextView =
            layoutOther.findViewById<TextView>(R.id.addExpenseDetailsItemLayoutTextView)
        val otherET =
            layoutOther.findViewById<TextInputEditText>(R.id.addExpenseDetailsItemLayoutTextInputEditText)
        val otherAttachImageView =
            layoutOther.findViewById<ImageView>(R.id.addExpenseDetailsItemLayoutAttachImageView)


        val profileTextView = view.findViewById<TextView>(R.id.addExpenseDetailsEmployeeIDTextView)

        val sp = requireActivity().getSharedPreferences("user",Context.MODE_PRIVATE)

        profileTextView.text = sp.getString("userEmployeeID","000")


        cashWorkerTextView.text = getString(R.string.cash_worker)
        workerAutoTextView.text = getString(R.string.worker_auto)
        workerFoodTextView.text = getString(R.string.worker_food)
        engineerFoodTextView.text = getString(R.string.engineer_food)
        engineerAutoCabTextView.text = getString(R.string.engineer_auto_cab)
        engineerHotelTextView.text = getString(R.string.engineer_hotel)
        workerHotelTextView.text = getString(R.string.worker_hotel)
        busTrainFareTextView.text = getString(R.string.bus_train_fare)
        fuelTextView.text = getString(R.string.petrol_diesel)
        materialTransportationTextView.text = getString(R.string.material_transportation)
        printingTextView.text = getString(R.string.printing_stationary)
        otherTextView.text = getString(R.string.other)



        storageRef = Firebase.storage.reference

        uploadImage(cashWorkerAttachImageView, "cashWorker")
        uploadImage(workerAutoAttachImageView, "workerAuto")
        uploadImage(workerFoodAttachImageView, "workerFood")
        uploadImage(engineerFoodAttachImageView, "engineerFood")
        uploadImage(engineerAutoCabAttachImageView, "engineerAutoCab")
        uploadImage(engineerHotelAttachImageView, "engineerHotel")
        uploadImage(workerHotelAttachImageView, "workerHotel")
        uploadImage(busTrainFareAttachImageView, "busTrainFare")
        uploadImage(fuelAttachImageView, "fuel")
        uploadImage(materialTransportationAttachImageView, "materialTransportation")
        uploadImage(printingAttachImageView, "printingAttach")
        uploadImage(otherAttachImageView, "other")


        submitButtonCardView.setOnClickListener {

            progressBar.visibility = View.VISIBLE

            expenseDetails.cashWorker = if (cashWorkerET.text.toString()
                    .isEmpty()
            ) 0.0 else cashWorkerET.text.toString().toDouble()
            expenseDetails.workerAuto = if (workerAutoET.text.toString()
                    .isEmpty()
            ) 0.0 else workerAutoET.text.toString().toDouble()
            expenseDetails.workerFood = if (workerFoodET.text.toString()
                    .isEmpty()
            ) 0.0 else workerFoodET.text.toString().toDouble()
            expenseDetails.engineerFood = if (engineerFoodET.text.toString()
                    .isEmpty()
            ) 0.0 else engineerFoodET.text.toString().toDouble()
            expenseDetails.engineerAutoCab = if (engineerAutoCabET.text.toString()
                    .isEmpty()
            ) 0.0 else engineerAutoCabET.text.toString().toDouble()
            expenseDetails.engineerHotel = if (engineerHotelET.text.toString()
                    .isEmpty()
            ) 0.0 else engineerHotelET.text.toString().toDouble()
            expenseDetails.workerHotel = if (workerHotelET.text.toString()
                    .isEmpty()
            ) 0.0 else workerHotelET.text.toString().toDouble()
            expenseDetails.busTrainFare = if (busTrainFareET.text.toString()
                    .isEmpty()
            ) 0.0 else busTrainFareET.text.toString().toDouble()
            expenseDetails.fuel = if (fuelET.text.toString()
                    .isEmpty()
            ) 0.0 else fuelET.text.toString()
                .toDouble()
            expenseDetails.materialTransportation = if (materialTransportationET.text.toString()
                    .isEmpty()
            ) 0.0 else materialTransportationET.text.toString().toDouble()
            expenseDetails.printingStationary = if (printingET.text.toString()
                    .isEmpty()
            ) 0.0 else printingET.text.toString().toDouble()
            expenseDetails.otherExpenses =
                if (otherET.text.toString().isEmpty()) 0.0 else otherET.text.toString().toDouble()
            expenseDetails.totalSpent =
                expenseDetails.cashWorker!! + expenseDetails.workerAuto!! + expenseDetails.workerFood!! + expenseDetails.engineerFood!! + expenseDetails.engineerAutoCab!! + expenseDetails.engineerHotel!! + expenseDetails.workerHotel!! + expenseDetails.busTrainFare!! + expenseDetails.fuel!! + expenseDetails.materialTransportation!! + expenseDetails.printingStationary!! + expenseDetails.otherExpenses!!

            val db = Firebase.firestore

            val timeInMillis = System.currentTimeMillis().toString()

            submitButtonCardView.isClickable = false
            submitButtonCardView.isEnabled = false

            db.collection("expenseDetails").document(timeInMillis)
                .set(expenseDetails)
                .addOnSuccessListener {

                    db.collection("balanceDetails").document("againstExpenses")
                        .update("total", FieldValue.increment(expenseDetails.totalSpent!!))
                        .addOnSuccessListener {

                            Toast.makeText(
                                context, "Balances Updated!", Toast.LENGTH_SHORT
                            ).show()

                            MainActivity.closeFragment(requireActivity())

                            progressBar.visibility = View.GONE

                        }
                        .addOnFailureListener {

                            Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                                .show()

                            progressBar.visibility = View.GONE

                            db.collection("expenseDetails").document(timeInMillis)
                                .delete().addOnSuccessListener {
                                    submitButtonCardView.isClickable = true
                                    submitButtonCardView.isEnabled = true

                                    Toast.makeText(activity, "Please try again!", Toast.LENGTH_LONG)
                                        .show()
                                }

                        }

                    Toast.makeText(
                        context, "Data is updated: $expenseDetails", Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener {
                    Toast.makeText(activity, it.message, Toast.LENGTH_LONG)
                        .show()
                    progressBar.visibility = View.GONE
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
         * @return A new instance of fragment AddExpenseDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = AddExpenseDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }


    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null) {
                // getting URI of selected Image
                val imageUri: Uri? = result.data?.data

                // val fileName = imageUri?.pathSegments?.last()

                // extract the file name with extension
                val sd = getFileName(requireContext(), imageUri!!)

                val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
                val currentDate = sdf.format(Date())

                val directoryPath = "expenses/$currentDate/$selectedImage"

                // Upload Task with upload to directory 'file'
                // and name of the file remains same
                storageRef.child(directoryPath).putFile(imageUri).addOnSuccessListener {

                    storageRef.child(directoryPath).downloadUrl.addOnSuccessListener {
                        expenseDetails.attachmentUrls!![selectedImage] = it.toString()
                    }

                    expenseDetails.isDocAttached = true

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

    private fun uploadImage(imageView: ImageView, selection: String) {
        imageView.setOnClickListener {

            selectedImage = selection

            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            imagePickerActivityResult.launch(galleryIntent)

            Toast.makeText(
                context, "Attachment is being Uploaded", Toast.LENGTH_SHORT
            ).show()

        }
    }
}