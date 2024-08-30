package com.example.poultry2.ui.global.filter.server

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemFilterRadioBinding

import com.example.poultry2.ui.global.filter.Filter


class FilterServerListAdapter internal constructor() : RecyclerView.Adapter<FilterServerListAdapter.ViewHolder>() {

    private var data = emptyList< Data.Server>() // Cached copy of words

    var onItemClick: (( Data.Server)->Unit) ?= null
    private var selectedPosition = -1

    inner class ViewHolder(val binding: ItemFilterRadioBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterRadioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]
        with (holder) {
            binding.rbItem.text = current.name
            binding.rbItem.isChecked = (position
                    == selectedPosition)


            binding.rbItem.setOnClickListener {
                val copyOfLastCheckedPosition: Int = selectedPosition
                selectedPosition = getAdapterPosition()
                Filter.cid=current.cid
                notifyItemChanged(copyOfLastCheckedPosition)
                notifyItemChanged(selectedPosition)
                Filter.user=Data.User(current.userType,current.userCode)
                onItemClick?.invoke(data[adapterPosition])
            }

        }
    }

    internal fun setData(data: MutableList<Data.Server>) {
        this.data = data
        if (data.isNotEmpty() && Filter.cid!="") {
            selectedPosition=data.indexOfFirst { it.cid==Filter.cid }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

}