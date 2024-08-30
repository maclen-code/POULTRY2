package com.example.poultry2.ui.global.menu

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.databinding.ItemMenuBinding


class MenuAdapter internal constructor() : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private var data = emptyList<String>() // Cached copy of words

    var onItemClick: ((String)->Unit) ?= null
    inner class ViewHolder(val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        with (holder) {
            binding.tvMenu.text = item
        }
    }

    internal fun setData(data: List<String>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}