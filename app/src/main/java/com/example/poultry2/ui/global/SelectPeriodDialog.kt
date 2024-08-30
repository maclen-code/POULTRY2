package com.example.poultry2.ui.global


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.poultry2.databinding.DialogPeriodBinding
import java.util.Calendar
import java.util.Date


class SelectPeriodDialog : DialogFragment() {

    private lateinit var binding: DialogPeriodBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPeriodBinding.inflate(requireActivity().layoutInflater)

        val cal: Calendar = Calendar.getInstance().apply {  Date() }

        binding.pickerMonth.run {
            minValue = 0
            maxValue = 11
            value = cal.get(Calendar.MONTH)
            displayedValues = arrayOf("Jan","Feb","Mar","Apr","May","June","July",
                "Aug","Sep","Oct","Nov","Dec")
        }

        binding.pickerYear.run {
            val year = cal.get(Calendar.YEAR)
            minValue = year-1
            maxValue = year+1
            value = year
        }

        val source = arguments?.getString("source").toString()

        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setTitle("Select Period")
            .setPositiveButton("Ok", null) //Set to null. We override the onclick
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val btOk =dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btOk.setOnClickListener {

                val y: Int = binding.pickerYear.value
                val m: Int = binding.pickerMonth.value + 1

                if (source=="activity") {
                    val dialogListener: DialogListener?=activity as DialogListener?
                    dialogListener!!.onFinishSelectPeriodDialog(m,y)
                }
                else{
                    val dialogListener: DialogListener?=parentFragment as DialogListener?
                    dialogListener!!.onFinishSelectPeriodDialog(m,y)
                }
                dialog.dismiss()
            }

            val btCancel =dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            btCancel.setOnClickListener {
                dialog.dismiss()
            }
        }

        return  dialog
    }

    interface DialogListener {
        fun onFinishSelectPeriodDialog(m:Int,y:Int)
    }

}