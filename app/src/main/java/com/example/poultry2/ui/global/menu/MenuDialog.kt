package com.example.poultry2.ui.global.menu

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.poultry2.R
import com.example.poultry2.databinding.DialogMenuBinding


class MenuDialog(var title:String,var list:List<String>) : DialogFragment()  {

    private  var source=""
    private lateinit var binding: DialogMenuBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMenuBinding.inflate(requireActivity().layoutInflater)
        source = arguments?.getString("source").toString()

        binding.tvDsp.text=title

        showMenu(list)

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
            .create()
    }

     private fun showMenu(list: List<String>){

        val  adapter = MenuAdapter()
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = LinearLayoutManager(activity?.applicationContext)

        val divider = DividerItemDecoration(activity,DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.rvList.addItemDecoration(divider)

        adapter.setData(list)
        adapter.onItemClick = {

            if (source=="activity") {
                val dialogListener: DialogListener?=activity as DialogListener?
                dialogListener!!.onFinishSelectMenuDialog(it)
            }
            else{
                val dialogListener: DialogListener?=parentFragment as DialogListener?
                dialogListener!!.onFinishSelectMenuDialog(it)
            }

        }
    }

    interface DialogListener {
        fun onFinishSelectMenuDialog(menu:String)
    }

}