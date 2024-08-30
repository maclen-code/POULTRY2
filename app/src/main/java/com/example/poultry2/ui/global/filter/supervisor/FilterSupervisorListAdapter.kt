package com.example.poultry2.ui.global.filter.supervisor

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemFilterBinding
import com.example.poultry2.ui.global.filter.Filter


class FilterSupervisorListAdapter internal constructor() : RecyclerView.Adapter<FilterSupervisorListAdapter.ViewHolder>() {

    private var data = emptyList< Data.FilterSupervisor>() // Cached copy of words

    var onItemClick: (( Data.FilterSupervisor)->Unit) ?= null
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
            binding.ckItem.text = "${current.supervisor} - ${current.area}"
            binding.ckItem.isChecked=current.isChecked


            binding.ckItem.setOnClickListener(View.OnClickListener {
                val checkState=binding.ckItem.isChecked
                current.isChecked=checkState
                notifyDataSetChanged()
                addToFilter(current)
            })

        }
    }

    internal fun setData(data: List<Data.FilterSupervisor>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size

    private fun addToFilter(item: Data.FilterSupervisor){
        val id=item.sno
        if (item.isChecked && !Filter.sno.contains("[$id]"))
            Filter.sno+="[$id]"
        else
            Filter.sno= Filter.sno.replace("[$id]","")
    }

}