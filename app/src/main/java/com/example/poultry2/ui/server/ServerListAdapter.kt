package com.example.poultry2.ui.server

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemServerBinding

class ServerListAdapter internal constructor() : RecyclerView.Adapter<ServerListAdapter.ViewHolder>() {

    private var data = emptyList<Data.Server>() // Cached copy of words

    var onItemClick: ((Data.Server)->Unit) ?= null
    inner class ViewHolder(val binding: ItemServerBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]
        with (holder) {
            binding.tvName.text = current.name
        }
    }

    internal fun setData(data: MutableList<Data.Server>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
}