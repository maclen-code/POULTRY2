package com.example.poultry.ui.global.filter.transType

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry.data.Data
import com.example.poultry.databinding.ItemFilterBinding
import com.example.poultry.ui.global.filter.Filter


class FilterTransTypeListAdapter internal constructor() : RecyclerView.Adapter<FilterTransTypeListAdapter.ViewHolder>() {

    private var data = emptyList< Data.FilterTransType>() // Cached copy of words

    var onItemClick: (( Data.FilterTransType)->Unit) ?= null
    inner class ViewHolder(val binding: ItemFilterBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFilterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]
        with (holder) {
            binding.ckItem.text = current.type
            binding.ckItem.isChecked=current.isChecked


            binding.ckItem.setOnClickListener {
                val checkState = binding.ckItem.isChecked
                current.isChecked = checkState
                notifyDataSetChanged()
                addToFilter(current)
            }

        }
    }

    internal fun setData(data: List<Data.FilterTransType>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    private fun addToFilter(item: Data.FilterTransType){
        val id=item.transType
        if (item.isChecked && !Filter.transType.contains("[$id]"))
            Filter.transType+="[$id]"
        else
            Filter.transType= Filter.transType.replace("[$id]","")
    }

}