package com.example.poultry2.ui.global


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.poultry2.databinding.DialogAdminValidationBinding


class AdminValidationDialog : DialogFragment() {

    private lateinit var binding: DialogAdminValidationBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAdminValidationBinding.inflate(requireActivity().layoutInflater)

        val source = arguments?.getString("source").toString()

        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setTitle("Admin Access")
            .setMessage("For I.T access only")
            .setPositiveButton("Ok", null) //Set to null. We override the onclick
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val btOk =dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btOk.setOnClickListener {


                if (source=="activity") {
                    val dialogListener: DialogListener?=activity as DialogListener?
                    dialogListener!!.onFinishValidationDialog(binding.etPin.text.toString())
                }
                else{
                    val dialogListener: DialogListener?=parentFragment as DialogListener?
                    dialogListener!!.onFinishValidationDialog(binding.etPin.text.toString())
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
        fun onFinishValidationDialog(pin:String)
    }

}