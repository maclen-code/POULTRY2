package com.example.poultry2.ui.global

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.DialogSyncConnBinding
import com.example.poultry2.ui.global.filter.Filter
import com.example.poultry2.ui.server.ServerListAdapter


class SyncConnDialog : DialogFragment()  {


    private lateinit var binding: DialogSyncConnBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogSyncConnBinding.inflate(requireActivity().layoutInflater)

        showList()

        return AlertDialog.Builder(requireContext())

            .setTitle("Select Server")
            .setView(binding.root)
            .setNegativeButton("Cancel") { _, _ ->
                dismiss()
            }
            .create()
    }
    // shows connection list
    private  fun showList() {
        val adapter = ServerListAdapter()
        val recyclerView = binding.rvList

        val divider = DividerItemDecoration(activity,DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!)
        binding.rvList.addItemDecoration(divider)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(activity, 1)

        adapter.setData(Filter.listServer)
        adapter.onItemClick = {
            var local=false
            if (binding.rbOffice.isChecked) local=true
            val dialogListener = activity as DialogListener?
            it.isLocal=local
            dialogListener!!.onFinishConnDialog(it)
            dismiss()
        }

    }

    interface DialogListener {
        fun onFinishConnDialog(server: Data.Server)
    }

}