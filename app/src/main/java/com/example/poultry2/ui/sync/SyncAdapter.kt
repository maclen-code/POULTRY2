package com.example.poultry2.ui.sync

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.poultry2.R
import com.example.poultry2.data.Data
import com.example.poultry2.databinding.ItemSyncBinding
import com.example.poultry2.ui.function.Theme.resolveColorAttr


class SyncAdapter internal constructor(context: Context) : RecyclerView.Adapter<SyncAdapter.ViewHolder>() {

    private var data = emptyList< Data.Sync>() // Cached copy of words


    private var textColorPrimary = context.resolveColorAttr(android.R.attr.textColorPrimary)
    private var textColorSecondary = context.resolveColorAttr(android.R.attr.textColorSecondary)
    private var colorAccent=ContextCompat.getColor(context, R.color.accent)
    var onItemClick: (( Data.Sync)->Unit) ?= null
    inner class ViewHolder(val binding: ItemSyncBinding) : RecyclerView.ViewHolder(binding.root) {
        init{
            itemView.setOnClickListener {
                onItemClick?.invoke(data[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSyncBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = data[position]
        with (holder) {
            var mode="Upload"
            if (current.download) mode="Download"

            binding.tvData.text = "$mode\n${current.dataName}"
            binding.tvStatus.text=current.status

            if (current.process=="error") {
                binding.tvData.setTextColor(textColorPrimary)
                binding.imgCheck.setImageResource(R.drawable.ic_error)
                binding.imgCheck.setColorFilter(
                    ContextCompat.getColor(itemView.context, R.color.sync_error)
                )
            }else {
                if (current.download)
                    binding.imgCheck.setImageResource(R.drawable.ic_download)
                else
                    binding.imgCheck.setImageResource(R.drawable.ic_upload)


                if (current.process == "pending") {
                    binding.tvData.setTextColor(textColorSecondary)
                    binding.imgCheck.setColorFilter(textColorSecondary)
                } else {
                    binding.tvData.setTextColor(textColorPrimary)
                    if (current.process != "error") binding.imgCheck.setColorFilter(colorAccent)
                }
            }

            if (current.process=="get" || current.process=="processing" )
                binding.pb.visibility=View.VISIBLE
            else
                binding.pb.visibility=View.INVISIBLE

//            binding.pb.isIndeterminate=current.process=="get"
            binding.pb.isIndeterminate=false
            binding.pb.progress=current.progress.toInt()
            binding.pb.invalidate()

        }
    }

    internal fun setData(data: List<Data.Sync>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size



}