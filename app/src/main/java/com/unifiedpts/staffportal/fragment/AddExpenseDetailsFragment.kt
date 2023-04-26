package com.unifiedpts.staffportal.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.unifiedpts.staffportal.R
import com.unifiedpts.staffportal.model.ExpenseDetails

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

        val submitButtonCardView =
            view.findViewById<MaterialCardView>(R.id.addExpenseDetailsSubmitButtonCardView)


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

        cashWorkerAttachImageView.setOnClickListener {
            Toast.makeText(
                context, "Attachment Dialog will Open", Toast.LENGTH_SHORT
            ).show()
            if (expenseDetails.attachmentUrls!!.isNotEmpty()) {
                expenseDetails.attachmentUrls!!["cashWorkerUrl"] = "www.thisistest.com"
            } else {
                expenseDetails.attachmentUrls = HashMap()
                expenseDetails.attachmentUrls!!["cashWorkerUrl"] = "www.thisistest.com"
            }
            expenseDetails.isDocAttached = true
            cashWorkerAttachImageView.setColorFilter(
                ContextCompat.getColor(
                    requireContext(), R.color.black
                ), android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }

        submitButtonCardView.setOnClickListener {

            expenseDetails.cashWorker = if (cashWorkerET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else cashWorkerET.text.toString().toFloat()
            expenseDetails.workerAuto = if (workerAutoET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else workerAutoET.text.toString().toFloat()
            expenseDetails.workerFood = if (workerFoodET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else workerFoodET.text.toString().toFloat()
            expenseDetails.engineerFood = if (engineerFoodET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else engineerFoodET.text.toString().toFloat()
            expenseDetails.engineerAutoCab = if (engineerAutoCabET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else engineerAutoCabET.text.toString().toFloat()
            expenseDetails.engineerHotel = if (engineerHotelET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else engineerHotelET.text.toString().toFloat()
            expenseDetails.workerHotel = if (workerHotelET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else workerHotelET.text.toString().toFloat()
            expenseDetails.busTrainFare = if (busTrainFareET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else busTrainFareET.text.toString().toFloat()
            expenseDetails.fuel =
                if (fuelET.text.toString().isEmpty()) 0.0.toFloat() else fuelET.text.toString()
                    .toFloat()
            expenseDetails.materialTransportation = if (materialTransportationET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else materialTransportationET.text.toString().toFloat()
            expenseDetails.printingStationary = if (printingET.text.toString()
                    .isEmpty()
            ) 0.0.toFloat() else printingET.text.toString().toFloat()
            expenseDetails.otherExpenses =
                if (otherET.text.toString().isEmpty()) 0.0.toFloat() else otherET.text.toString()
                    .toFloat()
            expenseDetails.totalSpent =
                expenseDetails.cashWorker!! + expenseDetails.workerAuto!! + expenseDetails.workerFood!! + expenseDetails.engineerFood!! + expenseDetails.engineerAutoCab!! + expenseDetails.engineerHotel!! + expenseDetails.workerHotel!! + expenseDetails.busTrainFare!! + expenseDetails.fuel!! + expenseDetails.materialTransportation!! + expenseDetails.printingStationary!! + expenseDetails.otherExpenses!!


            Toast.makeText(
                context, "Data: $expenseDetails", Toast.LENGTH_SHORT
            ).show()
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
}