package com.example.poultry2.ui.global.target


import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.DialogUpdateTargetBinding
import com.example.poultry2.ui.function.Utils
import com.example.poultry2.ui.function.Utils.toInt
import com.example.poultry2.ui.global.menu.MenuDialog.DialogListener


class UpdateTargetDialog : DialogFragment() {

    private lateinit var binding: DialogUpdateTargetBinding
    private  var source=""
    private var title:String=""
    private var volumeTarget=0
    private var amountTarget=0
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogUpdateTargetBinding.inflate(requireActivity().layoutInflater)

        source =requireArguments().getString("source","fragment")
        title=requireArguments().getString("title","")
        volumeTarget=requireArguments().getInt("volumeTarget",0)
        amountTarget=requireArguments().getInt("amountTarget",0)

        binding.tvTitle.text=title
        binding.etVolumeTarget.setText( Utils.formatIntToString(volumeTarget))
        binding.etAmountTarget.setText( Utils.formatIntToString(amountTarget))

        val dialog: AlertDialog = AlertDialog.Builder(context)
            .setView(binding.root)
            .setPositiveButton("Ok", null) //Set to null. We override the onclick
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val btOk =dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btOk.setOnClickListener {

                volumeTarget=binding.etVolumeTarget.text.toString().toInt()
                amountTarget=binding.etAmountTarget.text.toString().toInt()



                if (source=="activity") {
                    val dialogListener: DialogListener?=activity as DialogListener?
                    dialogListener!!.onFinishSetTargetDialog(volumeTarget,amountTarget)
                }
                else{
                    val dialogListener: DialogListener?=parentFragment as DialogListener?
                    dialogListener!!.onFinishSetTargetDialog(volumeTarget,amountTarget)
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
        fun onFinishSetTargetDialog(volumeTarget:Int,amountTarget:Int)
    }

}